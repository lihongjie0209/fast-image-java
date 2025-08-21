package cn.lihongjie.image.util;

import java.io.InputStream;

/**
 * 测试图片资源加载工具
 * 用于从 resources 目录加载预定义的测试图片
 */
public class TestImageLoader {
    
    /**
     * 加载小尺寸PNG测试图片 (500K.png)
     */
    public static byte[] loadSmallPngImage() {
        return loadTestImage("500K.png");
    }
    
    /**
     * 加载小尺寸JPEG测试图片 (700K.jpg)
     */
    public static byte[] loadSmallJpegImage() {
        return loadTestImage("700K.jpg");
    }
    
    /**
     * 加载中等尺寸JPEG测试图片 (1.5M.jpg)
     */
    public static byte[] loadMediumJpegImage() {
        return loadTestImage("1.5M.jpg");
    }
    
    /**
     * 加载中等尺寸PNG测试图片 (2M.png)
     */
    public static byte[] loadMediumPngImage() {
        return loadTestImage("2M.png");
    }
    
    /**
     * 加载大尺寸PNG测试图片 (5M.png)
     */
    public static byte[] loadLargePngImage() {
        return loadTestImage("5M.png");
    }
    
    /**
     * 加载大尺寸JPEG测试图片 (7.8M.jpg)
     */
    public static byte[] loadLargeJpegImage() {
        return loadTestImage("7.8M.jpg");
    }
    
    /**
     * 根据文件名加载测试图片
     */
    public static byte[] loadTestImage(String fileName) {
        try (InputStream is = TestImageLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException("找不到测试图片: " + fileName);
            }
            return is.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("加载测试图片失败: " + fileName, e);
        }
    }
    
    /**
     * 格式化文件大小显示
     */
    public static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }
    }
    
    /**
     * 简单的图片格式验证 (检查文件头)
     */
    public static boolean isValidImageFormat(byte[] imageData, String expectedFormat) {
        if (imageData == null || imageData.length < 4) {
            return false;
        }
        
        switch (expectedFormat.toUpperCase()) {
            case "PNG":
                return imageData[0] == (byte) 0x89 && imageData[1] == 'P' && 
                       imageData[2] == 'N' && imageData[3] == 'G';
            case "JPEG":
            case "JPG":
                return imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8;
            default:
                return true; // 对于其他格式，暂时返回true
        }
    }
}
