package cn.lihongjie.image.performance;

import cn.lihongjie.image.FastImageUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 综合压缩性能基准测试
 * 
 * 测试维度：
 * - 图片类型：JPEG, PNG
 * - 压缩工具：JDK原生, FastImage.compress, FastImage.compressJpegFast
 * - 测试图片大小：500K, 1M, 5M, 10M
 * 
 * 输出指标：
 * - 压缩时长 (ms)
 * - 压缩文件大小 (KB)
 * - 压缩率 (%)
 * - 相对于JDK的时间比较
 * - 相对于JDK的压缩率比较
 */
public class ComprehensiveCompressionBenchmark {

    // 测试图片配置 - 使用 src/test/resources 中的真实图片
    private static final TestImageConfig[] TEST_IMAGES = {
        new TestImageConfig("500K.png", "PNG", "500K"),
        new TestImageConfig("700K.jpg", "JPEG", "700K"),
        new TestImageConfig("1.5M.jpg", "JPEG", "1.5M"), 
        new TestImageConfig("2M.png", "PNG", "2M"),
        new TestImageConfig("5M.png", "PNG", "5M"),
        new TestImageConfig("7.8M.jpg", "JPEG", "7.8M")
    };
    
    // 压缩质量
    private static final int COMPRESSION_QUALITY = 70;
    
    // 测试图片配置类
    private static class TestImageConfig {
        final String fileName;
        final String format;
        final String sizeLabel;
        
        TestImageConfig(String fileName, String format, String sizeLabel) {
            this.fileName = fileName;
            this.format = format;
            this.sizeLabel = sizeLabel;
        }
    }
    
    // 测试结果存储
    private static class TestResult {
        String imageFormat;
        String sizeLabel;
        String originalFileName;
        int originalSize;
        String compressionTool;
        long compressionTime;
        int compressedSize;
        double compressionRatio;
        String timeVsJdk;
        String ratioVsJdk;
        boolean supported;
        String savedPath; // 保存的文件路径
        
        @Override
        public String toString() {
            if (!supported) {
                return String.format("| %s | %s | %.1f KB | %s | × | × | × | × | × |",
                    imageFormat, sizeLabel, originalSize / 1024.0, compressionTool);
            }
            return String.format("| %s | %s | %.1f KB | %s | %d ms | %.1f KB | %.1f%% | %s | %s |",
                imageFormat, sizeLabel, originalSize / 1024.0, compressionTool, compressionTime, 
                compressedSize / 1024.0, compressionRatio, timeVsJdk, ratioVsJdk);
        }
    }

    @Test
    public void runComprehensiveBenchmark() {
        System.out.println("===============================================");
        System.out.println("        综合压缩性能基准测试");
        System.out.println("===============================================");
        System.out.println();
        
        List<TestResult> allResults = new ArrayList<>();
        
        // 对每个测试图片进行测试
        for (TestImageConfig imageConfig : TEST_IMAGES) {
            System.out.println("📊 测试图片: " + imageConfig.fileName + " (" + imageConfig.format + " " + imageConfig.sizeLabel + ")");
            
            try {
                // 读取测试图片
                byte[] testImage = loadTestImage(imageConfig.fileName);
                int actualSize = testImage.length;
                
                System.out.printf("   图片文件: %s, 实际大小: %.1f KB%n", 
                    imageConfig.fileName, actualSize / 1024.0);
                
                // 测试不同压缩工具
                List<TestResult> results = testCompressionTools(imageConfig.format, imageConfig.sizeLabel, imageConfig.fileName, testImage, actualSize);
                allResults.addAll(results);
                
            } catch (Exception e) {
                System.err.println("   ❌ 读取图片 " + imageConfig.fileName + " 失败: " + e.getMessage());
            }
            
            System.out.println();
        }
        
        // 输出markdown格式的结果
        printMarkdownResults(allResults);
    }
    
    /**
     * 从 resources 文件夹加载测试图片
     */
    private byte[] loadTestImage(String fileName) throws Exception {
        // 尝试从 classpath 读取
        try (InputStream is = getClass().getResourceAsStream("/" + fileName)) {
            if (is != null) {
                return is.readAllBytes();
            }
        }
        
        // 如果 classpath 读取失败，尝试从文件系统读取
        String resourcePath = "src/test/resources/" + fileName;
        return Files.readAllBytes(Paths.get(resourcePath));
    }
    
    /**
     * 保存压缩后的图片到 target 文件夹
     * @param compressedData 压缩后的图片数据
     * @param originalFileName 原始文件名
     * @param compressionTool 压缩工具名称
     * @param compressionRatio 压缩率
     * @return 保存的文件路径
     */
    private String saveCompressedImage(byte[] compressedData, String originalFileName, String compressionTool, double compressionRatio) {
        try {
            // 创建输出目录
            Path outputDir = Paths.get("target", "compression-results");
            Files.createDirectories(outputDir);
            
            // 解析原始文件名
            String baseName = getBaseName(originalFileName);
            String originalExt = getExtension(originalFileName);
            
            // 生成输出文件名
            String toolName = compressionTool.replace(".", "_").replace(" ", "_");
            String outputFileName = String.format("%s_%s_q%d_ratio%.1f%%",
                baseName, toolName, COMPRESSION_QUALITY, compressionRatio);
            
            // 确定输出文件扩展名
            String outputExt = determineOutputExtension(compressionTool, originalExt);
            String fullOutputFileName = outputFileName + outputExt;
            
            // 保存文件
            Path outputPath = outputDir.resolve(fullOutputFileName);
            Files.write(outputPath, compressedData);
            
            return outputPath.toString();
            
        } catch (Exception e) {
            System.err.println("   ⚠️ 保存压缩结果失败: " + e.getMessage());
            return "保存失败";
        }
    }
    
    /**
     * 获取文件基础名（不含扩展名）
     */
    private String getBaseName(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot) : ".jpg";
    }
    
    /**
     * 确定输出文件的扩展名
     */
    private String determineOutputExtension(String compressionTool, String originalExt) {
        if (compressionTool.contains("compressJpegFast")) {
            return ".jpg"; // compressJpegFast 总是输出 JPEG
        } else if (compressionTool.contains("FastImage.compress")) {
            return originalExt; // compress 保持原格式
        } else {
            return ".jpg"; // JDK 原生只支持 JPEG
        }
    }
    
    /**
     * 测试所有压缩工具
     */
    private List<TestResult> testCompressionTools(String format, String sizeLabel, String originalFileName, byte[] imageData, int originalSize) {
        List<TestResult> results = new ArrayList<>();
        
        // 测试 JDK 原生压缩
        TestResult jdkResult = testJdkCompression(format, sizeLabel, originalFileName, imageData, originalSize);
        results.add(jdkResult);
        
        // 测试 FastImage.compress
        TestResult fastImageResult = testFastImageCompress(format, sizeLabel, originalFileName, imageData, originalSize, jdkResult);
        results.add(fastImageResult);
        
        // 测试 FastImage.compressJpegFast
        TestResult fastJpegResult = testFastImageJpegFast(format, sizeLabel, originalFileName, imageData, originalSize, jdkResult);
        results.add(fastJpegResult);
        
        return results;
    }
    
    /**
     * 测试 JDK 原生压缩
     */
    private TestResult testJdkCompression(String format, String sizeLabel, String originalFileName, byte[] imageData, int originalSize) {
        TestResult result = new TestResult();
        result.imageFormat = format;
        result.sizeLabel = sizeLabel;
        result.originalFileName = originalFileName;
        result.originalSize = originalSize;
        result.compressionTool = "JDK原生";
        
        if (format.equals("PNG")) {
            // JDK 不支持 PNG 压缩
            result.supported = false;
            System.out.println("   🚫 JDK原生: 不支持PNG压缩");
            return result;
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 读取图片
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            
            // 使用 JDK 的 JPEG 压缩
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = writers.next();
            writer.setOutput(ios);
            
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(COMPRESSION_QUALITY / 100.0f);
            
            writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
            
            writer.dispose();
            ios.close();
            
            byte[] compressed = baos.toByteArray();
            long endTime = System.currentTimeMillis();
            
            result.supported = true;
            result.compressionTime = endTime - startTime;
            result.compressedSize = compressed.length;
            result.compressionRatio = ((double) (originalSize - compressed.length) / originalSize) * 100;
            result.timeVsJdk = "基准";
            result.ratioVsJdk = "基准";
            
            // 保存压缩结果
            result.savedPath = saveCompressedImage(compressed, originalFileName, result.compressionTool, result.compressionRatio);
            
            System.out.printf("   ✅ JDK原生: %d ms, %.1f KB, 压缩率 %.1f%% → %s%n",
                result.compressionTime, result.compressedSize / 1024.0, result.compressionRatio, 
                result.savedPath.substring(result.savedPath.lastIndexOf('/') + 1));
            
        } catch (Exception e) {
            result.supported = false;
            System.out.println("   ❌ JDK原生: 压缩失败 - " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 测试 FastImage.compress
     */
    private TestResult testFastImageCompress(String format, String sizeLabel, String originalFileName, byte[] imageData, int originalSize, TestResult jdkResult) {
        TestResult result = new TestResult();
        result.imageFormat = format;
        result.sizeLabel = sizeLabel;
        result.originalFileName = originalFileName;
        result.originalSize = originalSize;
        result.compressionTool = "FastImage.compress";
        
        try {
            long startTime = System.currentTimeMillis();
            byte[] compressed = FastImageUtils.compress(imageData, COMPRESSION_QUALITY);
            long endTime = System.currentTimeMillis();
            
            result.supported = true;
            result.compressionTime = endTime - startTime;
            result.compressedSize = compressed.length;
            result.compressionRatio = ((double) (originalSize - compressed.length) / originalSize) * 100;
            
            // 与 JDK 比较
            if (jdkResult.supported) {
                double timeRatio = (double) result.compressionTime / jdkResult.compressionTime;
                double ratioRatio = result.compressionRatio / jdkResult.compressionRatio;
                
                result.timeVsJdk = String.format("%.2fx", timeRatio);
                result.ratioVsJdk = String.format("%.2fx", ratioRatio);
            } else {
                result.timeVsJdk = "N/A";
                result.ratioVsJdk = "N/A";
            }
            
            // 保存压缩结果
            result.savedPath = saveCompressedImage(compressed, originalFileName, result.compressionTool, result.compressionRatio);
            
            System.out.printf("   ✅ FastImage.compress: %d ms, %.1f KB, 压缩率 %.1f%% (vs JDK: %s 时间, %s 压缩率) → %s%n",
                result.compressionTime, result.compressedSize / 1024.0, result.compressionRatio,
                result.timeVsJdk, result.ratioVsJdk, 
                result.savedPath.substring(result.savedPath.lastIndexOf('/') + 1));
            
        } catch (Exception e) {
            result.supported = false;
            System.out.println("   ❌ FastImage.compress: 压缩失败 - " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 测试 FastImage.compressJpegFast
     */
    private TestResult testFastImageJpegFast(String format, String sizeLabel, String originalFileName, byte[] imageData, int originalSize, TestResult jdkResult) {
        TestResult result = new TestResult();
        result.imageFormat = format;
        result.sizeLabel = sizeLabel;
        result.originalFileName = originalFileName;
        result.originalSize = originalSize;
        result.compressionTool = "FastImage.compressJpegFast";
        
        if (format.equals("PNG")) {
            // compressJpegFast 不支持 PNG
            result.supported = false;
            System.out.println("   🚫 FastImage.compressJpegFast: 不支持PNG输入");
            return result;
        }
        
        try {
            long startTime = System.currentTimeMillis();
            byte[] compressed = FastImageUtils.compressJpegFast(imageData, COMPRESSION_QUALITY);
            long endTime = System.currentTimeMillis();
            
            result.supported = true;
            result.compressionTime = endTime - startTime;
            result.compressedSize = compressed.length;
            result.compressionRatio = ((double) (originalSize - compressed.length) / originalSize) * 100;
            
            // 与 JDK 比较
            if (jdkResult.supported) {
                double timeRatio = (double) result.compressionTime / jdkResult.compressionTime;
                double ratioRatio = result.compressionRatio / jdkResult.compressionRatio;
                
                result.timeVsJdk = String.format("%.2fx", timeRatio);
                result.ratioVsJdk = String.format("%.2fx", ratioRatio);
            } else {
                result.timeVsJdk = "N/A";
                result.ratioVsJdk = "N/A";
            }
            
            // 保存压缩结果
            result.savedPath = saveCompressedImage(compressed, originalFileName, result.compressionTool, result.compressionRatio);
            
            System.out.printf("   ✅ FastImage.compressJpegFast: %d ms, %.1f KB, 压缩率 %.1f%% (vs JDK: %s 时间, %s 压缩率) → %s%n",
                result.compressionTime, result.compressedSize / 1024.0, result.compressionRatio,
                result.timeVsJdk, result.ratioVsJdk,
                result.savedPath.substring(result.savedPath.lastIndexOf('/') + 1));
            
        } catch (Exception e) {
            result.supported = false;
            System.out.println("   ❌ FastImage.compressJpegFast: 压缩失败 - " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 输出 Markdown 格式的测试结果
     */
    private void printMarkdownResults(List<TestResult> results) {
        System.out.println();
        System.out.println("===============================================");
        System.out.println("           Markdown 格式测试结果");
        System.out.println("===============================================");
        System.out.println();
        
        // Markdown 表格头部
        System.out.println("# 综合压缩性能基准测试结果");
        System.out.println();
        System.out.println("## 测试配置");
        System.out.println("- **压缩质量**: " + COMPRESSION_QUALITY + "%");
        System.out.println("- **测试图片大小**: 500K, 1M, 5M, 10M");
        System.out.println("- **图片格式**: JPEG, PNG");
        System.out.println("- **压缩工具**: JDK原生, FastImage.compress, FastImage.compressJpegFast");
        System.out.println();
        System.out.println("## 兼容性说明");
        System.out.println("- **JDK原生**: 仅支持JPEG压缩，不支持PNG");
        System.out.println("- **FastImage.compress**: 支持JPEG和PNG格式");
        System.out.println("- **FastImage.compressJpegFast**: 仅支持JPEG输入，输出JPEG");
        System.out.println();
        System.out.println("## 测试结果");
        System.out.println();
        
        // 表格头部
        System.out.println("| 图片格式 | 大小 | 原始大小 | 压缩工具 | 压缩时长 | 压缩大小 | 压缩率 | 时间对比JDK | 压缩率对比JDK |");
        System.out.println("|---------|------|----------|----------|----------|----------|--------|-------------|---------------|");
        
        // 输出结果
        for (TestResult result : results) {
            System.out.println(result.toString());
        }
        
        System.out.println();
        System.out.println("## 结果说明");
        System.out.println("- **压缩时长**: 毫秒 (ms)");
        System.out.println("- **压缩大小**: 千字节 (KB)");
        System.out.println("- **压缩率**: (原始大小-压缩后大小)/原始大小 × 100% (越大越好，表示节省的空间比例)");
        System.out.println("- **时间对比JDK**: 相对于JDK原生的时间倍数 (< 1.0 表示更快)");
        System.out.println("- **压缩率对比JDK**: 相对于JDK原生的压缩率倍数 (> 1.0 表示压缩效果更好)");
        System.out.println("- **×**: 表示该组合不支持");
        System.out.println("- **N/A**: 表示无法与JDK对比 (JDK不支持该格式)");
        
        // 生成统计分析
        generateSummaryAnalysis(results);
    }
    
    /**
     * 生成统计分析
     */
    private void generateSummaryAnalysis(List<TestResult> results) {
        System.out.println();
        System.out.println("## 性能分析");
        System.out.println();
        
        // 按工具统计
        System.out.println("### 按压缩工具统计");
        
        // FastImage.compress 统计
        List<TestResult> fastImageResults = results.stream()
            .filter(r -> r.compressionTool.equals("FastImage.compress") && r.supported)
            .collect(java.util.stream.Collectors.toList());
        
        if (!fastImageResults.isEmpty()) {
            double avgTime = fastImageResults.stream().mapToLong(r -> r.compressionTime).average().orElse(0);
            double avgRatio = fastImageResults.stream().mapToDouble(r -> r.compressionRatio).average().orElse(0);
            System.out.printf("- **FastImage.compress**: 平均压缩时间 %.1f ms, 平均压缩率 %.1f%%%n", avgTime, avgRatio);
        }
        
        // FastImage.compressJpegFast 统计 (仅JPEG)
        List<TestResult> fastJpegResults = results.stream()
            .filter(r -> r.compressionTool.equals("FastImage.compressJpegFast") && r.supported)
            .collect(java.util.stream.Collectors.toList());
        
        if (!fastJpegResults.isEmpty()) {
            double avgTime = fastJpegResults.stream().mapToLong(r -> r.compressionTime).average().orElse(0);
            double avgRatio = fastJpegResults.stream().mapToDouble(r -> r.compressionRatio).average().orElse(0);
            System.out.printf("- **FastImage.compressJpegFast**: 平均压缩时间 %.1f ms, 平均压缩率 %.1f%% (仅JPEG)%n", avgTime, avgRatio);
        }
        
        // JDK 原生统计 (仅JPEG)
        List<TestResult> jdkResults = results.stream()
            .filter(r -> r.compressionTool.equals("JDK原生") && r.supported)
            .collect(java.util.stream.Collectors.toList());
        
        if (!jdkResults.isEmpty()) {
            double avgTime = jdkResults.stream().mapToLong(r -> r.compressionTime).average().orElse(0);
            double avgRatio = jdkResults.stream().mapToDouble(r -> r.compressionRatio).average().orElse(0);
            System.out.printf("- **JDK原生**: 平均压缩时间 %.1f ms, 平均压缩率 %.1f%% (仅JPEG)%n", avgTime, avgRatio);
        }
        
        System.out.println();
        System.out.println("### 推荐使用场景");
        System.out.println("- **PNG压缩**: 使用 FastImage.compress (JDK不支持PNG压缩)");
        System.out.println("- **JPEG高质量压缩**: 使用 FastImage.compress (mozjpeg算法)");
        System.out.println("- **JPEG快速压缩**: 使用 FastImage.compressJpegFast");
        System.out.println("- **兼容性优先**: 使用 JDK原生 (仅支持JPEG)");
        
        System.out.println();
        System.out.println("---");
        System.out.println("*测试完成时间: " + new java.util.Date() + "*");
    }
    
    /**
     * 快速测试（仅测试小图片）
     */
    @Test
    public void runQuickBenchmark() {
        System.out.println("===============================================");
        System.out.println("        快速压缩性能测试");
        System.out.println("===============================================");
        System.out.println();
        
        List<TestResult> results = new ArrayList<>();
        
        // 仅测试小一些的图片
        TestImageConfig[] quickTestImages = {
            TEST_IMAGES[0], // 500K.png
            TEST_IMAGES[1]  // 700K.jpg
        };
        
        for (TestImageConfig imageConfig : quickTestImages) {
            System.out.println("📊 测试图片: " + imageConfig.fileName + " (" + imageConfig.format + " " + imageConfig.sizeLabel + ")");
            
            try {
                byte[] testImage = loadTestImage(imageConfig.fileName);
                List<TestResult> imageResults = testCompressionTools(imageConfig.format, imageConfig.sizeLabel, imageConfig.fileName, testImage, testImage.length);
                results.addAll(imageResults);
            } catch (Exception e) {
                System.err.println("   ❌ 读取图片 " + imageConfig.fileName + " 失败: " + e.getMessage());
            }
            
            System.out.println();
        }
        
        // 输出简化的结果
        System.out.println("## 快速测试结果");
        System.out.println();
        System.out.println("| 图片格式 | 原始大小 | 压缩工具 | 压缩时长 | 压缩大小 | 压缩率 |");
        System.out.println("|---------|----------|----------|----------|----------|--------|");
        
        for (TestResult result : results) {
            if (result.supported) {
                System.out.printf("| %s | %.1f KB | %s | %d ms | %.1f KB | %.1f%% |%n",
                    result.imageFormat, result.originalSize / 1024.0, result.compressionTool, result.compressionTime,
                    result.compressedSize / 1024.0, result.compressionRatio);
            } else {
                System.out.printf("| %s | %.1f KB | %s | × | × | × |%n",
                    result.imageFormat, result.originalSize / 1024.0, result.compressionTool);
            }
        }
    }
}
