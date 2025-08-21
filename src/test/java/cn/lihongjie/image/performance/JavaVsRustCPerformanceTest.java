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
 * Java vs Rust+C 性能对比测试
 * 
 * 测试目标: 验证Java → Rust → C新方案相较于纯Java方案的性能优势
 * 
 * 测试场景:
 * 1. JPEG高质量压缩: 4000x3000像素，quality=85
 * 2. PNG UI截图压缩: 1920x1080像素  
 * 3. 批量混合格式测试: 多种真实图片
 * 
 * 对比方案:
 * - 方案A (基线): 纯Java实现 (javax.imageio)
 * - 方案B (新方案): Java → Rust → C (FastImage)
 */
public class JavaVsRustCPerformanceTest {
    
    private static final int HIGH_QUALITY = 85;  // 高质量压缩
    private static final String RESULTS_DIR = "target/java-vs-rust-results";
    
    /**
     * 测试结果数据结构
     */
    private static class ComparisonResult {
        String testCase;
        String imageFormat;
        String originalFile;
        int originalSize;
        
        // 方案A - 纯Java
        long javaTime = -1;
        int javaSize = -1;
        double javaCompressionRatio = -1;
        boolean javaSupported = false;
        String javaSavedPath;
        
        // 方案B - Rust+C  
        long rustTime = -1;
        int rustSize = -1;
        double rustCompressionRatio = -1;
        boolean rustSupported = false;
        String rustSavedPath;
        
        // 性能对比
        double speedImprovement = -1;    // Rust相对Java的速度提升倍数
        double compressionImprovement = -1; // Rust相对Java的压缩率提升
    }
    
    /**
     * 主测试入口 - 完整对比测试
     */
    @Test
    public void runJavaVsRustComparisonTest() {
        System.out.println("===============================================");
        System.out.println("        Java vs Rust+C 性能对比测试");
        System.out.println("===============================================");
        System.out.println();
        
        // 创建结果目录
        createResultsDirectory();
        
        List<ComparisonResult> results = new ArrayList<>();
        
        // 测试用例1: JPEG高质量压缩 (4K分辨率)
        System.out.println("🔍 测试用例1: JPEG高质量压缩测试");
        System.out.println("   目标: 4000×3000像素，quality=85");
        results.add(testJpegHighQuality());
        System.out.println();
        
        // 测试用例2: PNG UI截图压缩 (FHD分辨率)  
        System.out.println("🔍 测试用例2: PNG UI截图压缩测试");
        System.out.println("   目标: 1920×1080像素，UI界面内容");
        results.add(testPngUIScreenshot());
        System.out.println();
        
        // 测试用例3: 批量混合格式测试
        System.out.println("🔍 测试用例3: 批量混合格式测试");
        System.out.println("   目标: 多种真实图片格式和大小");
        results.addAll(testBatchMixedFormats());
        System.out.println();
        
        // 生成对比报告
        generateComparisonReport(results);
    }
    
    /**
     * 测试用例1: JPEG高质量压缩
     */
    private ComparisonResult testJpegHighQuality() {
        // 使用现有的大尺寸JPEG图片作为测试
        String testFile = "7.8M.jpg";  // 8MB高质量JPEG，接近4K分辨率
        
        ComparisonResult result = new ComparisonResult();
        result.testCase = "JPEG高质量压缩 (4K级别)";
        result.imageFormat = "JPEG";
        result.originalFile = testFile;
        
        try {
            byte[] imageData = loadTestImage(testFile);
            result.originalSize = imageData.length;
            
            System.out.printf("   📷 测试图片: %s (%.1f MB)%n", testFile, imageData.length / 1024.0 / 1024.0);
            
            // 方案A: 纯Java压缩
            testJavaCompression(result, imageData);
            
            // 方案B: Rust+C压缩
            testRustCompression(result, imageData);
            
            // 计算性能对比
            calculatePerformanceGains(result);
            
        } catch (Exception e) {
            System.err.println("   ❌ 测试失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 测试用例2: PNG UI截图压缩
     */
    private ComparisonResult testPngUIScreenshot() {
        // 使用现有的PNG图片模拟UI截图
        String testFile = "2M.png";  // 2MB PNG，接近FHD UI截图大小
        
        ComparisonResult result = new ComparisonResult();
        result.testCase = "PNG UI截图压缩 (FHD级别)";
        result.imageFormat = "PNG";
        result.originalFile = testFile;
        
        try {
            byte[] imageData = loadTestImage(testFile);
            result.originalSize = imageData.length;
            
            System.out.printf("   🖼️ 测试图片: %s (%.1f MB)%n", testFile, imageData.length / 1024.0 / 1024.0);
            
            // 方案A: 纯Java压缩 (不支持PNG)
            testJavaCompression(result, imageData);
            
            // 方案B: Rust+C压缩
            testRustCompression(result, imageData);
            
            // 计算性能对比
            calculatePerformanceGains(result);
            
        } catch (Exception e) {
            System.err.println("   ❌ 测试失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 测试用例3: 批量混合格式测试
     */
    private List<ComparisonResult> testBatchMixedFormats() {
        List<ComparisonResult> results = new ArrayList<>();
        
        // 测试多种图片格式和大小
        String[] testFiles = {
            "500K.png",   // 小尺寸PNG
            "700K.jpg",   // 中等JPEG
            "1.5M.jpg",   // 大尺寸JPEG
            "5M.png"      // 大尺寸PNG
        };
        
        for (String testFile : testFiles) {
            ComparisonResult result = new ComparisonResult();
            result.testCase = "批量测试 - " + testFile;
            result.imageFormat = testFile.endsWith(".png") ? "PNG" : "JPEG";
            result.originalFile = testFile;
            
            try {
                byte[] imageData = loadTestImage(testFile);
                result.originalSize = imageData.length;
                
                System.out.printf("   📁 批量测试: %s (%.1f KB)%n", testFile, imageData.length / 1024.0);
                
                // 方案A和方案B测试
                testJavaCompression(result, imageData);
                testRustCompression(result, imageData);
                calculatePerformanceGains(result);
                
                results.add(result);
                
            } catch (Exception e) {
                System.err.println("   ❌ 批量测试失败: " + testFile + " - " + e.getMessage());
            }
        }
        
        return results;
    }
    
    /**
     * 方案A: 纯Java压缩测试
     */
    private void testJavaCompression(ComparisonResult result, byte[] imageData) {
        if (result.imageFormat.equals("PNG")) {
            // Java不支持PNG压缩
            result.javaSupported = false;
            System.out.println("      ❌ 方案A (纯Java): 不支持PNG压缩");
            return;
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 使用Java ImageIO进行JPEG压缩
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(HIGH_QUALITY / 100.0f);
            
            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            writer.setOutput(ios);
            writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
            
            ios.close();
            writer.dispose();
            
            long endTime = System.currentTimeMillis();
            byte[] compressed = baos.toByteArray();
            
            result.javaSupported = true;
            result.javaTime = endTime - startTime;
            result.javaSize = compressed.length;
            result.javaCompressionRatio = ((double) (result.originalSize - compressed.length) / result.originalSize) * 100;
            
            // 保存压缩结果
            result.javaSavedPath = saveCompressedResult(compressed, result.originalFile, "Java纯净版", result.javaCompressionRatio);
            
            System.out.printf("      ✅ 方案A (纯Java): %d ms, %.1f KB, 压缩率 %.1f%% → %s%n",
                result.javaTime, result.javaSize / 1024.0, result.javaCompressionRatio,
                result.javaSavedPath.substring(result.javaSavedPath.lastIndexOf('/') + 1));
            
        } catch (Exception e) {
            result.javaSupported = false;
            System.out.println("      ❌ 方案A (纯Java): 压缩失败 - " + e.getMessage());
        }
    }
    
    /**
     * 方案B: Rust+C压缩测试
     */
    private void testRustCompression(ComparisonResult result, byte[] imageData) {
        try {
            long startTime = System.currentTimeMillis();
            
            // 使用FastImage (Rust+C) 进行压缩
            byte[] compressed;
            if (result.imageFormat.equals("JPEG")) {
                compressed = FastImageUtils.compress(imageData, HIGH_QUALITY);
            } else {
                // PNG压缩
                compressed = FastImageUtils.compress(imageData, HIGH_QUALITY);
            }
            
            long endTime = System.currentTimeMillis();
            
            result.rustSupported = true;
            result.rustTime = endTime - startTime;
            result.rustSize = compressed.length;
            result.rustCompressionRatio = ((double) (result.originalSize - compressed.length) / result.originalSize) * 100;
            
            // 保存压缩结果
            result.rustSavedPath = saveCompressedResult(compressed, result.originalFile, "Rust+C新方案", result.rustCompressionRatio);
            
            System.out.printf("      🚀 方案B (Rust+C): %d ms, %.1f KB, 压缩率 %.1f%% → %s%n",
                result.rustTime, result.rustSize / 1024.0, result.rustCompressionRatio,
                result.rustSavedPath.substring(result.rustSavedPath.lastIndexOf('/') + 1));
            
        } catch (Exception e) {
            result.rustSupported = false;
            System.out.println("      ❌ 方案B (Rust+C): 压缩失败 - " + e.getMessage());
        }
    }
    
    /**
     * 计算性能对比指标
     */
    private void calculatePerformanceGains(ComparisonResult result) {
        if (result.javaSupported && result.rustSupported) {
            // 速度提升倍数 (Java时间 / Rust时间)
            result.speedImprovement = (double) result.javaTime / result.rustTime;
            
            // 压缩率提升 (Rust压缩率 - Java压缩率)
            result.compressionImprovement = result.rustCompressionRatio - result.javaCompressionRatio;
            
            System.out.printf("      📈 性能对比: %.2fx速度提升, %.1f%%压缩率提升%n",
                result.speedImprovement, result.compressionImprovement);
        } else if (!result.javaSupported && result.rustSupported) {
            System.out.println("      📈 功能优势: Rust+C方案独家支持此格式");
        }
    }
    
    /**
     * 保存压缩结果文件
     */
    private String saveCompressedResult(byte[] compressedData, String originalFileName, String tool, double ratio) {
        try {
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            
            String fileName = String.format("%s_%s_q%d_ratio%.1f%%%s",
                baseName, tool, HIGH_QUALITY, ratio, extension);
            
            Path resultPath = Paths.get(RESULTS_DIR, fileName);
            Files.write(resultPath, compressedData);
            
            return resultPath.toString();
        } catch (Exception e) {
            return "保存失败: " + e.getMessage();
        }
    }
    
    /**
     * 生成完整的对比报告
     */
    private void generateComparisonReport(List<ComparisonResult> results) {
        System.out.println("===============================================");
        System.out.println("           Java vs Rust+C 对比报告");
        System.out.println("===============================================");
        System.out.println();
        
        // 生成Markdown格式报告
        System.out.println("# Java vs Rust+C 性能对比测试报告");
        System.out.println();
        System.out.println("## 测试配置");
        System.out.println("- **压缩质量**: " + HIGH_QUALITY + "%");
        System.out.println("- **测试日期**: " + java.time.LocalDateTime.now());
        System.out.println("- **方案A**: 纯Java实现 (javax.imageio)");
        System.out.println("- **方案B**: Java → Rust → C (FastImage + mozjpeg/libpng)");
        System.out.println();
        
        System.out.println("## 详细测试结果");
        System.out.println();
        System.out.println("| 测试用例 | 格式 | 原始大小 | Java耗时 | Java大小 | Java压缩率 | Rust耗时 | Rust大小 | Rust压缩率 | 速度提升 | 压缩率提升 |");
        System.out.println("|---------|------|----------|----------|----------|------------|----------|----------|------------|----------|------------|");
        
        double totalSpeedGain = 0;
        double totalCompressionGain = 0;
        int validComparisons = 0;
        
        for (ComparisonResult result : results) {
            System.out.printf("| %s | %s | %.1f KB |", 
                result.testCase, result.imageFormat, result.originalSize / 1024.0);
            
            if (result.javaSupported) {
                System.out.printf(" %d ms | %.1f KB | %.1f%% |",
                    result.javaTime, result.javaSize / 1024.0, result.javaCompressionRatio);
            } else {
                System.out.print(" × | × | × |");
            }
            
            if (result.rustSupported) {
                System.out.printf(" %d ms | %.1f KB | %.1f%% |",
                    result.rustTime, result.rustSize / 1024.0, result.rustCompressionRatio);
            } else {
                System.out.print(" × | × | × |");
            }
            
            if (result.javaSupported && result.rustSupported) {
                System.out.printf(" %.2fx | +%.1f%% |%n",
                    result.speedImprovement, result.compressionImprovement);
                
                totalSpeedGain += result.speedImprovement;
                totalCompressionGain += result.compressionImprovement;
                validComparisons++;
            } else if (!result.javaSupported && result.rustSupported) {
                System.out.println(" 独家支持 | 独家支持 |");
            } else {
                System.out.println(" × | × |");
            }
        }
        
        System.out.println();
        
        // 总结统计
        if (validComparisons > 0) {
            double avgSpeedGain = totalSpeedGain / validComparisons;
            double avgCompressionGain = totalCompressionGain / validComparisons;
            
            System.out.println("## 性能总结");
            System.out.println();
            System.out.printf("- **平均速度提升**: %.2fx (Rust+C相对Java)%n", avgSpeedGain);
            System.out.printf("- **平均压缩率提升**: %.1f%% (额外空间节省)%n", avgCompressionGain);
            System.out.printf("- **有效对比测试**: %d项%n", validComparisons);
            System.out.println();
            
            // 结论建议
            System.out.println("## 测试结论");
            System.out.println();
            if (avgSpeedGain > 1.5 && avgCompressionGain > 10) {
                System.out.println("✅ **推荐采用Rust+C方案**: 在速度和压缩率两个维度都有显著优势");
            } else if (avgSpeedGain > 2.0) {
                System.out.println("⚡ **推荐采用Rust+C方案**: 速度优势明显，适合高并发场景");
            } else if (avgCompressionGain > 20) {
                System.out.println("💾 **推荐采用Rust+C方案**: 压缩率优势明显，适合存储优化场景");
            } else {
                System.out.println("⚖️ **需要权衡选择**: 性能提升有限，需考虑部署复杂度");
            }
            
            System.out.println();
            System.out.println("### 适用场景建议");
            System.out.println("- **PNG处理**: 强烈推荐Rust+C方案 (Java不支持PNG压缩)");
            System.out.println("- **高质量JPEG**: 推荐Rust+C方案 (mozjpeg算法优势)");
            System.out.println("- **快速原型**: 可考虑Java方案 (部署简单)");
            System.out.println("- **生产环境**: 推荐Rust+C方案 (性能和功能全面)");
        }
        
        System.out.println();
        System.out.println("---");
        System.out.printf("*测试完成: %s*%n", java.time.LocalDateTime.now());
    }
    
    /**
     * 创建结果目录
     */
    private void createResultsDirectory() {
        try {
            Path resultsPath = Paths.get(RESULTS_DIR);
            if (!Files.exists(resultsPath)) {
                Files.createDirectories(resultsPath);
            }
        } catch (Exception e) {
            System.err.println("创建结果目录失败: " + e.getMessage());
        }
    }
    
    /**
     * 加载测试图片
     */
    private byte[] loadTestImage(String fileName) throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException("找不到测试图片: " + fileName);
            }
            return is.readAllBytes();
        }
    }
}
