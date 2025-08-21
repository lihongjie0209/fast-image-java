package cn.lihongjie.image.performance;

import cn.lihongjie.image.FastImageUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * 交互式图片处理测试
 * 允许用户输入文件夹路径，对所有图片执行压缩和旋转操作
 * 输出到 target 目录，使用后缀名区分不同操作
 */
public class InteractiveImageProcessingTest {

    @Test
    public void testInteractiveImageProcessing() {
        System.out.println("===============================================");
        System.out.println("        交互式图片处理测试");
        System.out.println("===============================================");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        
        try {
            // 获取用户输入的文件夹路径
            System.out.print("请输入图片文件夹路径: ");
            String inputFolderPath = scanner.nextLine().trim();
            
            // 验证路径
            Path inputPath = Paths.get(inputFolderPath);
            if (!Files.exists(inputPath) || !Files.isDirectory(inputPath)) {
                System.err.println("❌ 错误: 路径不存在或不是文件夹: " + inputFolderPath);
                return;
            }

            // 创建输出目录
            final Path outputPath = Paths.get("target", "processed-images");
            try {
                Files.createDirectories(outputPath);
                System.out.println("✅ 输出目录: " + outputPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("❌ 无法创建输出目录: " + e.getMessage());
                return;
            }

            System.out.println();
            System.out.println("开始处理图片...");
            System.out.println();

            // 处理文件夹中的所有图片
            try {
                Files.walk(inputPath)
                        .filter(Files::isRegularFile)
                        .filter(this::isImageFile)
                        .forEach(imagePath -> processImage(imagePath, outputPath));
            } catch (IOException e) {
                System.err.println("❌ 处理文件夹时出错: " + e.getMessage());
                return;
            }

            System.out.println();
            System.out.println("===============================================");
            System.out.println("           处理完成！");
            System.out.println("===============================================");
            System.out.println("输出目录: " + outputPath.toAbsolutePath());
            
        } finally {
            scanner.close();
        }
        System.out.println();
        System.out.println("文件命名规则:");
        System.out.println("  原文件名_compressed_standard.jpg  - 标准JPEG压缩 (mozjpeg)");
        System.out.println("  原文件名_compressed_fast.jpg      - 快速JPEG压缩");
        System.out.println("  原文件名_rotated_90.jpg           - 顺时针旋转90度");
        System.out.println("  原文件名_rotated_180.jpg          - 旋转180度");
        System.out.println("  原文件名_rotated_270.jpg          - 顺时针旋转270度");
        System.out.println();
        System.out.println("格式说明:");
        System.out.println("  📦 压缩操作 → 总是输出JPEG (专门的JPEG压缩算法)");
        System.out.println("  🔄 旋转操作 → 输出JPEG (当前FastImageUtils实现)");
        System.out.println("  💡 这样确保了压缩效果的一致性和兼容性");
        System.out.println();
    }

    /**
     * 判断是否为支持的图片文件
     */
    private boolean isImageFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".jpg") || 
               fileName.endsWith(".jpeg") || 
               fileName.endsWith(".png") || 
               fileName.endsWith(".bmp");
    }

    /**
     * 处理单个图片文件
     * 为每个图片创建单独的文件夹，先复制原图，再执行各种操作
     */
    private void processImage(Path imagePath, Path outputPath) {
        String originalName = imagePath.getFileName().toString();
        String baseName = getBaseName(originalName);
        String originalExtension = getExtension(originalName);
        
        System.out.println("🔄 处理: " + originalName);

        try {
            // 为当前图片创建单独的文件夹
            Path imageOutputDir = outputPath.resolve(baseName);
            Files.createDirectories(imageOutputDir);
            
            // 1. 复制原图到目标文件夹
            Path originalCopy = imageOutputDir.resolve("original" + originalExtension);
            Files.copy(imagePath, originalCopy, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("   📁 创建目录: " + imageOutputDir.getFileName());
            System.out.println("   📄 复制原图: " + originalCopy.getFileName() + 
                              " (大小: " + formatFileSize(Files.size(originalCopy)) + ")");

            // 读取原始图片数据
            byte[] imageData = Files.readAllBytes(imagePath);
            long originalSize = imageData.length;

            // 2. 压缩操作
            if (originalExtension.toLowerCase().equals(".png")) {
                // PNG 文件：标准压缩保持PNG格式，快速压缩转为JPEG格式
                processCompression(imageData, imageOutputDir, baseName, "compressed_standard", 70, originalSize, false, originalExtension);
                // processCompression(imageData, imageOutputDir, baseName, "compressed_fast_to_jpeg", 70, originalSize, true, originalExtension);
            } else {
                // JPEG 文件：两种压缩方式都保持JPEG格式
                processCompression(imageData, imageOutputDir, baseName, "compressed_standard", 70, originalSize, false, originalExtension);
                processCompression(imageData, imageOutputDir, baseName, "compressed_fast", 70, originalSize, true, originalExtension);
            }

            // 3. 旋转操作 (保持原格式: PNG→PNG, JPEG→JPEG)
            processRotation(imageData, imageOutputDir, baseName, 90, originalExtension);
            processRotation(imageData, imageOutputDir, baseName, 180, originalExtension);
            processRotation(imageData, imageOutputDir, baseName, 270, originalExtension);

            System.out.println("   ✅ 完成: " + originalName + " (输出目录: " + imageOutputDir.getFileName() + ")");

        } catch (Exception e) {
            System.err.println("   ❌ 处理失败: " + originalName + " - " + e.getMessage());
        }
        
        System.out.println();
    }

    /**
     * 处理压缩操作
     * 使用 FastImageUtils.compress() 会保持原格式输出（PNG→PNG, JPEG→JPEG）
     * 使用 FastImageUtils.compressJpegFast() 会转换为 JPEG 格式
     */
    private void processCompression(byte[] imageData, Path outputPath, String baseName, 
                                  String suffix, int quality, long originalSize, boolean useFast, 
                                  String originalExtension) {
        try {
            long startTime = System.currentTimeMillis();
            
            byte[] compressed;
            String outputExtension;
            
            if (useFast) {
                // 快速压缩，强制输出为 JPEG
                compressed = FastImageUtils.compressJpegFast(imageData, quality);
                outputExtension = ".jpg";
            } else {
                // 标准压缩，保持原格式
                compressed = FastImageUtils.compress(imageData, quality);
                outputExtension = originalExtension;
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 保存文件
            String outputFileName = baseName + "_" + suffix + outputExtension;
            Path outputFile = outputPath.resolve(outputFileName);
            Files.write(outputFile, compressed);

            // 计算压缩比
            double compressionRatio = (double) compressed.length / originalSize;
            double spaceSaved = (1.0 - compressionRatio) * 100;

            System.out.printf("   📦 %s: %d ms, %.1f KB → %.1f KB (节省 %.1f%%) [%s]%n",
                    suffix, duration, originalSize / 1024.0, compressed.length / 1024.0, 
                    spaceSaved, outputExtension.toUpperCase().substring(1));

        } catch (Exception e) {
            System.err.println("   ❌ 压缩失败 (" + suffix + "): " + e.getMessage());
        }
    }

    /**
     * 处理旋转操作
     * FastImageUtils.rotate() 支持多种格式并保持原格式输出（PNG、JPEG、WebP、GIF、BMP）
     */
    private void processRotation(byte[] imageData, Path outputPath, String baseName, int degrees, String extension) {
        try {
            long startTime = System.currentTimeMillis();
            
            byte[] rotated = FastImageUtils.rotate(imageData, degrees);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 保存文件 - 保持原始格式
            String outputFileName = baseName + "_rotated_" + degrees + extension;
            Path outputFile = outputPath.resolve(outputFileName);
            Files.write(outputFile, rotated);

            System.out.printf("   🔄 旋转%d度: %d ms, %.1f KB%n",
                    degrees, duration, rotated.length / 1024.0);

        } catch (Exception e) {
            System.err.println("   ❌ 旋转失败 (" + degrees + "度): " + e.getMessage());
        }
    }

    /**
     * 获取文件的基础名称（不含扩展名）
     */
    private String getBaseName(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    /**
     * 获取文件的扩展名
     */
    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex); // 包含点号
        }
        return ".jpg"; // 默认扩展名
    }

    /**
     * 批量处理模式测试 - 使用示例文件夹
     */
    @Test
    public void testBatchProcessingWithExampleImages() {
        System.out.println("===============================================");
        System.out.println("        批量处理示例图片");
        System.out.println("===============================================");
        System.out.println();

        // 使用项目中的示例图片
        Path exampleImagesPath = Paths.get("../fast-image/images");
        if (!Files.exists(exampleImagesPath)) {
            exampleImagesPath = Paths.get("images"); // 备用路径
        }
        
        if (!Files.exists(exampleImagesPath)) {
            System.out.println("❌ 未找到示例图片文件夹，请手动运行 testInteractiveImageProcessing() 测试");
            return;
        }

        Path outputPath = Paths.get("target", "example-processed");
        try {
            Files.createDirectories(outputPath);
        } catch (IOException e) {
            System.err.println("❌ 无法创建输出目录: " + e.getMessage());
            return;
        }

        System.out.println("📂 输入目录: " + exampleImagesPath.toAbsolutePath());
        System.out.println("📂 输出目录: " + outputPath.toAbsolutePath());
        System.out.println();

        try {
            Files.walk(exampleImagesPath)
                    .filter(Files::isRegularFile)
                    .filter(this::isImageFile)
                    .forEach(imagePath -> processImage(imagePath, outputPath));
        } catch (IOException e) {
            System.err.println("❌ 处理示例图片时出错: " + e.getMessage());
        }

        System.out.println("✅ 示例图片批量处理完成！");
    }

    /**
     * 单文件处理测试
     */
    @Test
    public void testSingleFileProcessing() {
        System.out.println("===============================================");
        System.out.println("        单文件处理测试");
        System.out.println("===============================================");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        
        try {
            System.out.print("请输入图片文件路径: ");
            String filePath = scanner.nextLine().trim();
            
            Path imagePath = Paths.get(filePath);
            if (!Files.exists(imagePath) || !Files.isRegularFile(imagePath)) {
                System.err.println("❌ 错误: 文件不存在: " + filePath);
                return;
            }

            if (!isImageFile(imagePath)) {
                System.err.println("❌ 错误: 不支持的图片格式");
                return;
            }

            Path outputPath = Paths.get("target", "single-processed");
            try {
                Files.createDirectories(outputPath);
            } catch (IOException e) {
                System.err.println("❌ 无法创建输出目录: " + e.getMessage());
                return;
            }

            processImage(imagePath, outputPath);
            
            System.out.println("✅ 单文件处理完成！");
            System.out.println("📂 输出目录: " + outputPath.toAbsolutePath());
        } finally {
            scanner.close();
        }
    }
    
    /**
     * 格式化文件大小显示
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }
    }
}
