package cn.lihongjie.image;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * 性能测试：比较 FastImageUtils 与 JDK 自带的 JPEG 压缩性能和效率
 * 
 * 测试维度：
 * 1. 压缩速度对比
 * 2. 压缩比率对比
 * 3. 内存使用对比
 * 4. 不同图片尺寸下的性能表现
 */
public class ImageCompressionBenchmark {
    
    private static final int WARMUP_ITERATIONS = 5;
    private static final int TEST_ITERATIONS = 10;
    
    // 测试用的不同尺寸图片数据
    private byte[] smallImageJpeg;    // 500x300
    private byte[] mediumImageJpeg;   // 1920x1080  
    private byte[] largeImageJpeg;    // 3840x2160
    
    @Before
    public void setUp() throws IOException {
        System.out.println("=== 图像压缩性能基准测试 ===");
        System.out.println("FastImageUtils vs JDK ImageIO");
        System.out.println();
        
        // 创建不同尺寸的测试图片
        smallImageJpeg = createTestJpegImage(500, 300, 0.9f);
        mediumImageJpeg = createTestJpegImage(1920, 1080, 0.9f);
        largeImageJpeg = createTestJpegImage(3840, 2160, 0.9f);
        
        System.out.printf("测试图片大小：\n");
        System.out.printf("小图片 (500x300): %s\n", formatBytes(smallImageJpeg.length));
        System.out.printf("中图片 (1920x1080): %s\n", formatBytes(mediumImageJpeg.length));
        System.out.printf("大图片 (3840x2160): %s\n", formatBytes(largeImageJpeg.length));
        System.out.println();
        
        // 检查FastImageUtils是否可用
        System.out.println("FastImageUtils 库状态：");
        System.out.println(FastImageUtils.getPlatformInfo());
        System.out.println("库测试结果: " + (FastImageUtils.testLibrary() ? "通过" : "失败"));
        System.out.println();
    }
    
    @Test
    public void benchmarkSmallImages() {
        System.out.println("=== 小图片性能测试 (500x300) ===");
        runBenchmarkSuite("小图片", smallImageJpeg);
        System.out.println();
    }
    
    @Test
    public void benchmarkMediumImages() {
        System.out.println("=== 中等图片性能测试 (1920x1080) ===");
        runBenchmarkSuite("中等图片", mediumImageJpeg);
        System.out.println();
    }
    
    @Test
    public void benchmarkLargeImages() {
        System.out.println("=== 大图片性能测试 (3840x2160) ===");
        runBenchmarkSuite("大图片", largeImageJpeg);
        System.out.println();
    }
    
    @Test
    public void benchmarkQualityComparison() {
        System.out.println("=== 压缩质量对比测试 ===");
        
        int[] qualities = {30, 50, 70, 90};
        
        for (int quality : qualities) {
            System.out.printf("\n--- 质量设置: %d%% ---\n", quality);
            
            BenchmarkResult fastResult = benchmarkFastImageUtils(mediumImageJpeg, quality);
            BenchmarkResult jdkResult = benchmarkJdkCompression(mediumImageJpeg, quality / 100.0f);
            
            System.out.printf("FastImageUtils - 耗时: %s, 压缩后: %s, 压缩比: %.1f%%\n",
                formatTime(fastResult.avgTimeMs),
                formatBytes(fastResult.outputSize),
                (1.0 - (double)fastResult.outputSize / mediumImageJpeg.length) * 100
            );
            
            System.out.printf("JDK ImageIO   - 耗时: %s, 压缩后: %s, 压缩比: %.1f%%\n",
                formatTime(jdkResult.avgTimeMs),
                formatBytes(jdkResult.outputSize),
                (1.0 - (double)jdkResult.outputSize / mediumImageJpeg.length) * 100
            );
            
            double speedup = (double)jdkResult.avgTimeMs / fastResult.avgTimeMs;
            System.out.printf("性能提升: %.2fx %s\n", 
                Math.abs(speedup), 
                speedup > 1 ? "(FastImageUtils更快)" : "(JDK更快)"
            );
        }
        System.out.println();
    }
    
    @Test
    public void benchmarkMemoryUsage() {
        System.out.println("=== 内存使用对比测试 ===");
        
        Runtime runtime = Runtime.getRuntime();
        
        // 测试FastImageUtils内存使用
        System.gc();
        long beforeFast = runtime.totalMemory() - runtime.freeMemory();
        
        for (int i = 0; i < 100; i++) {
            try {
                FastImageUtils.compress(mediumImageJpeg, 70);
            } catch (Exception e) {
                System.out.println("FastImageUtils 压缩失败: " + e.getMessage());
                break;
            }
        }
        
        System.gc();
        long afterFast = runtime.totalMemory() - runtime.freeMemory();
        long fastMemoryUsed = afterFast - beforeFast;
        
        // 测试JDK压缩内存使用
        System.gc();
        long beforeJdk = runtime.totalMemory() - runtime.freeMemory();
        
        for (int i = 0; i < 100; i++) {
            try {
                compressWithJdk(mediumImageJpeg, 0.7f);
            } catch (Exception e) {
                System.out.println("JDK 压缩失败: " + e.getMessage());
                break;
            }
        }
        
        System.gc();
        long afterJdk = runtime.totalMemory() - runtime.freeMemory();
        long jdkMemoryUsed = afterJdk - beforeJdk;
        
        System.out.printf("FastImageUtils 内存使用: %s\n", formatBytes(Math.max(0, fastMemoryUsed)));
        System.out.printf("JDK ImageIO 内存使用: %s\n", formatBytes(Math.max(0, jdkMemoryUsed)));
        System.out.println();
    }
    
    /**
     * 运行完整的性能测试套件
     */
    private void runBenchmarkSuite(String testName, byte[] imageData) {
        int[] qualities = {30, 70, 90};
        
        for (int quality : qualities) {
            System.out.printf("--- 质量: %d%% ---\n", quality);
            
            // FastImageUtils 测试
            BenchmarkResult fastResult = benchmarkFastImageUtils(imageData, quality);
            if (fastResult != null) {
                System.out.printf("FastImageUtils: %s (压缩后: %s, 压缩比: %.1f%%)\n",
                    formatTime(fastResult.avgTimeMs),
                    formatBytes(fastResult.outputSize),
                    (1.0 - (double)fastResult.outputSize / imageData.length) * 100
                );
            } else {
                System.out.println("FastImageUtils: 测试失败");
            }
            
            // JDK 测试
            BenchmarkResult jdkResult = benchmarkJdkCompression(imageData, quality / 100.0f);
            if (jdkResult != null) {
                System.out.printf("JDK ImageIO:   %s (压缩后: %s, 压缩比: %.1f%%)\n",
                    formatTime(jdkResult.avgTimeMs),
                    formatBytes(jdkResult.outputSize),
                    (1.0 - (double)jdkResult.outputSize / imageData.length) * 100
                );
            } else {
                System.out.println("JDK ImageIO: 测试失败");
            }
            
            // 性能对比
            if (fastResult != null && jdkResult != null) {
                double speedRatio = (double)jdkResult.avgTimeMs / fastResult.avgTimeMs;
                double sizeRatio = (double)fastResult.outputSize / jdkResult.outputSize;
                
                System.out.printf("性能对比: FastImageUtils %s %.2fx\n",
                    speedRatio > 1 ? "快" : "慢",
                    Math.abs(speedRatio)
                );
                System.out.printf("大小对比: FastImageUtils %s %.2fx\n",
                    sizeRatio < 1 ? "更小" : "更大",
                    Math.abs(sizeRatio)
                );
            }
            System.out.println();
        }
    }
    
    /**
     * FastImageUtils 性能测试
     */
    private BenchmarkResult benchmarkFastImageUtils(byte[] imageData, int quality) {
        try {
            // 预热
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                FastImageUtils.compress(imageData, quality);
            }
            
            // 正式测试
            long totalTime = 0;
            byte[] lastResult = null;
            
            for (int i = 0; i < TEST_ITERATIONS; i++) {
                long startTime = System.nanoTime();
                lastResult = FastImageUtils.compress(imageData, quality);
                long endTime = System.nanoTime();
                
                totalTime += (endTime - startTime);
            }
            
            long avgTimeMs = totalTime / TEST_ITERATIONS / 1_000_000;
            int outputSize = lastResult != null ? lastResult.length : 0;
            
            return new BenchmarkResult(avgTimeMs, outputSize);
            
        } catch (Exception e) {
            System.out.println("FastImageUtils 测试出现异常: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * JDK ImageIO 性能测试
     */
    private BenchmarkResult benchmarkJdkCompression(byte[] imageData, float quality) {
        try {
            // 预热
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                compressWithJdk(imageData, quality);
            }
            
            // 正式测试
            long totalTime = 0;
            byte[] lastResult = null;
            
            for (int i = 0; i < TEST_ITERATIONS; i++) {
                long startTime = System.nanoTime();
                lastResult = compressWithJdk(imageData, quality);
                long endTime = System.nanoTime();
                
                totalTime += (endTime - startTime);
            }
            
            long avgTimeMs = totalTime / TEST_ITERATIONS / 1_000_000;
            int outputSize = lastResult != null ? lastResult.length : 0;
            
            return new BenchmarkResult(avgTimeMs, outputSize);
            
        } catch (Exception e) {
            System.out.println("JDK ImageIO 测试出现异常: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 使用JDK ImageIO压缩图片
     */
    private byte[] compressWithJdk(byte[] imageData, float quality) throws IOException {
        // 读取图片
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        BufferedImage image = ImageIO.read(bais);
        
        if (image == null) {
            throw new IOException("无法读取图片数据");
        }
        
        // 获取JPEG写入器
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IOException("没有找到JPEG写入器");
        }
        
        ImageWriter writer = writers.next();
        
        // 设置压缩参数
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(quality);
        
        // 压缩图片
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), writeParam);
        }
        writer.dispose();
        
        return baos.toByteArray();
    }
    
    /**
     * 创建测试用的JPEG图片
     */
    private byte[] createTestJpegImage(int width, int height, float quality) throws IOException {
        // 创建彩色渐变图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置渐变背景
        GradientPaint gradient = new GradientPaint(
            0, 0, Color.BLUE,
            width, height, Color.RED
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // 添加一些细节纹理
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < Math.min(width, height) / 10; i++) {
            int x = (int)(Math.random() * width);
            int y = (int)(Math.random() * height);
            int size = (int)(Math.random() * 20 + 5);
            g2d.fillOval(x, y, size, size);
        }
        
        // 添加文字
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, Math.max(12, Math.min(width, height) / 50)));
        String text = String.format("测试图片 %dx%d", width, height);
        g2d.drawString(text, 20, 30);
        
        g2d.dispose();
        
        // 转换为JPEG字节数组
        return compressWithJdk(imageToBytes(image), quality);
    }
    
    /**
     * BufferedImage转换为字节数组
     */
    private byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
    
    /**
     * 格式化字节大小
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    /**
     * 格式化时间
     */
    private String formatTime(long timeMs) {
        if (timeMs < 1000) return timeMs + " ms";
        return String.format("%.2f s", timeMs / 1000.0);
    }
    
    /**
     * 基准测试结果
     */
    private static class BenchmarkResult {
        final long avgTimeMs;
        final int outputSize;
        
        BenchmarkResult(long avgTimeMs, int outputSize) {
            this.avgTimeMs = avgTimeMs;
            this.outputSize = outputSize;
        }
    }
}
