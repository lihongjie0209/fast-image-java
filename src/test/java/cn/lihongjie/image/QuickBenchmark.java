package cn.lihongjie.image;

/**
 * 简化的性能测试运行器
 * 可以直接运行来快速对比 FastImageUtils 和 JDK 的压缩性能
 */
public class QuickBenchmark {
    
    public static void main(String[] args) {
        System.out.println("=== FastImageUtils vs JDK ImageIO 快速性能对比 ===\n");
        
        try {
            QuickBenchmark benchmark = new QuickBenchmark();
            benchmark.runQuickTest();
        } catch (Exception e) {
            System.err.println("测试运行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void runQuickTest() throws Exception {
        // 检查FastImageUtils状态
        System.out.println("1. 检查 FastImageUtils 库状态:");
        System.out.println(FastImageUtils.getPlatformInfo());
        boolean libraryWorking = FastImageUtils.testLibrary();
        System.out.println("库可用性: " + (libraryWorking ? "✓ 可用" : "✗ 不可用"));
        System.out.println();
        
        if (!libraryWorking) {
            System.out.println("⚠️  FastImageUtils 库不可用，只能测试 JDK 压缩");
            testJdkOnly();
            return;
        }
        
        // 创建测试图片
        System.out.println("2. 创建测试图片...");
        byte[] testImage = createSimpleTestImage();
        System.out.printf("测试图片大小: %s\n\n", formatBytes(testImage.length));
        
        // 测试不同质量级别
        int[] qualities = {30, 50, 70, 90};
        
        System.out.println("3. 性能对比测试结果:");
        System.out.println("质量  | FastImageUtils        | JDK ImageIO          | 性能提升");
        System.out.println("------|----------------------|----------------------|----------");
        
        for (int quality : qualities) {
            TestResult fastResult = testFastImageUtils(testImage, quality);
            TestResult jdkResult = testJdkImageIO(testImage, quality / 100.0f);
            
            double speedup = (double) jdkResult.timeMs / fastResult.timeMs;
            String speedupStr = speedup > 1 ? 
                String.format("%.2fx 更快", speedup) : 
                String.format("%.2fx 更慢", 1.0 / speedup);
                
            System.out.printf("%3d%% | %4d ms (%s) | %4d ms (%s) | %s\n",
                quality,
                fastResult.timeMs, formatBytes(fastResult.size),
                jdkResult.timeMs, formatBytes(jdkResult.size),
                speedupStr
            );
        }
        
        System.out.println();
        
        // 压缩率对比
        System.out.println("4. 压缩率对比 (基于1080p测试图片):");
        byte[] largeTestImage = createLargeTestImage();
        System.out.printf("原始大小: %s\n\n", formatBytes(largeTestImage.length));
        
        System.out.println("质量  | FastImageUtils 压缩率 | JDK ImageIO 压缩率   | 压缩率差异");
        System.out.println("------|----------------------|----------------------|----------");
        
        for (int quality : qualities) {
            TestResult fastResult = testFastImageUtils(largeTestImage, quality);
            TestResult jdkResult = testJdkImageIO(largeTestImage, quality / 100.0f);
            
            double fastRatio = (1.0 - (double)fastResult.size / largeTestImage.length) * 100;
            double jdkRatio = (1.0 - (double)jdkResult.size / largeTestImage.length) * 100;
            double ratioDiff = fastRatio - jdkRatio;
            
            System.out.printf("%3d%% |          %.1f%%         |          %.1f%%         | %+.1f%%\n",
                quality, fastRatio, jdkRatio, ratioDiff
            );
        }
        
        System.out.println("\n=== 测试完成 ===");
    }
    
    private void testJdkOnly() throws Exception {
        System.out.println("JDK ImageIO 独立性能测试:");
        
        byte[] testImage = createSimpleTestImage();
        System.out.printf("测试图片大小: %s\n\n", formatBytes(testImage.length));
        
        int[] qualities = {30, 50, 70, 90};
        
        System.out.println("质量  | 压缩时间 | 压缩后大小 | 压缩率");
        System.out.println("------|----------|------------|--------");
        
        for (int quality : qualities) {
            TestResult result = testJdkImageIO(testImage, quality / 100.0f);
            double ratio = (1.0 - (double)result.size / testImage.length) * 100;
            
            System.out.printf("%3d%% |   %4d ms | %10s | %5.1f%%\n",
                quality, result.timeMs, formatBytes(result.size), ratio
            );
        }
    }
    
    private TestResult testFastImageUtils(byte[] imageData, int quality) {
        try {
            // 预热
            for (int i = 0; i < 3; i++) {
                FastImageUtils.compress(imageData, quality);
            }
            
            // 测试
            long startTime = System.currentTimeMillis();
            byte[] result = FastImageUtils.compress(imageData, quality);
            long endTime = System.currentTimeMillis();
            
            return new TestResult(endTime - startTime, result.length);
        } catch (Exception e) {
            return new TestResult(-1, -1);
        }
    }
    
    private TestResult testJdkImageIO(byte[] imageData, float quality) {
        try {
            // 预热
            for (int i = 0; i < 3; i++) {
                compressWithJdk(imageData, quality);
            }
            
            // 测试
            long startTime = System.currentTimeMillis();
            byte[] result = compressWithJdk(imageData, quality);
            long endTime = System.currentTimeMillis();
            
            return new TestResult(endTime - startTime, result.length);
        } catch (Exception e) {
            return new TestResult(-1, -1);
        }
    }
    
    private byte[] compressWithJdk(byte[] imageData, float quality) throws Exception {
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(imageData);
        java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(bais);
        
        if (image == null) {
            throw new Exception("无法读取图片数据");
        }
        
        java.util.Iterator<javax.imageio.ImageWriter> writers = 
            javax.imageio.ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new Exception("没有找到JPEG写入器");
        }
        
        javax.imageio.ImageWriter writer = writers.next();
        javax.imageio.ImageWriteParam writeParam = writer.getDefaultWriteParam();
        writeParam.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(quality);
        
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        javax.imageio.stream.ImageOutputStream ios = javax.imageio.ImageIO.createImageOutputStream(baos);
        
        writer.setOutput(ios);
        writer.write(null, new javax.imageio.IIOImage(image, null, null), writeParam);
        writer.dispose();
        ios.close();
        
        return baos.toByteArray();
    }
    
    private byte[] createSimpleTestImage() throws Exception {
        // 创建 800x600 的测试图片
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            800, 600, java.awt.image.BufferedImage.TYPE_INT_RGB);
        
        java.awt.Graphics2D g2d = image.createGraphics();
        
        // 渐变背景
        java.awt.GradientPaint gradient = new java.awt.GradientPaint(
            0, 0, java.awt.Color.BLUE,
            800, 600, java.awt.Color.RED
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 800, 600);
        
        // 添加一些细节
        g2d.setColor(java.awt.Color.WHITE);
        for (int i = 0; i < 50; i++) {
            int x = (int)(Math.random() * 800);
            int y = (int)(Math.random() * 600);
            g2d.fillOval(x, y, 10, 10);
        }
        
        // 添加文字
        g2d.setColor(java.awt.Color.BLACK);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        g2d.drawString("性能测试图片 800x600", 50, 50);
        
        g2d.dispose();
        
        // 转换为高质量JPEG
        return compressWithJdk(imageToBytes(image), 0.95f);
    }
    
    private byte[] createLargeTestImage() throws Exception {
        // 创建 1920x1080 的测试图片
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            1920, 1080, java.awt.image.BufferedImage.TYPE_INT_RGB);
        
        java.awt.Graphics2D g2d = image.createGraphics();
        
        // 复杂的渐变背景
        java.awt.GradientPaint gradient1 = new java.awt.GradientPaint(
            0, 0, java.awt.Color.CYAN,
            960, 540, java.awt.Color.MAGENTA
        );
        g2d.setPaint(gradient1);
        g2d.fillRect(0, 0, 1920, 1080);
        
        // 添加更多细节
        g2d.setColor(java.awt.Color.WHITE);
        for (int i = 0; i < 200; i++) {
            int x = (int)(Math.random() * 1920);
            int y = (int)(Math.random() * 1080);
            int size = (int)(Math.random() * 20 + 5);
            g2d.fillOval(x, y, size, size);
        }
        
        // 添加网格
        g2d.setColor(new java.awt.Color(255, 255, 255, 100));
        for (int i = 0; i < 1920; i += 100) {
            g2d.drawLine(i, 0, i, 1080);
        }
        for (int i = 0; i < 1080; i += 100) {
            g2d.drawLine(0, i, 1920, i);
        }
        
        // 添加文字
        g2d.setColor(java.awt.Color.BLACK);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
        g2d.drawString("大图片性能测试 1920x1080", 100, 100);
        
        g2d.dispose();
        
        return compressWithJdk(imageToBytes(image), 0.95f);
    }
    
    private byte[] imageToBytes(java.awt.image.BufferedImage image) throws Exception {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 0) return "错误";
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return String.format("%.1fKB", bytes / 1024.0);
        return String.format("%.1fMB", bytes / (1024.0 * 1024));
    }
    
    private static class TestResult {
        final long timeMs;
        final int size;
        
        TestResult(long timeMs, int size) {
            this.timeMs = timeMs;
            this.size = size;
        }
    }
}
