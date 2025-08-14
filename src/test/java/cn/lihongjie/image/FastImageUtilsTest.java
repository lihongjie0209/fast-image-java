package cn.lihongjie.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test class for FastImageUtils with cross-platform support
 */
public class FastImageUtilsTest {
    
    public static void main(String[] args) {
        System.out.println("=== Fast Image Utils Cross-Platform Test ===");
        
        try {
            // Display platform information
            System.out.println("📋 Platform Information:");
            System.out.println(FastImageUtils.getPlatformInfo());
            System.out.println();
            
            // Test library loading
            System.out.println("🧪 Testing Library:");
            boolean libraryWorking = FastImageUtils.testLibrary();
            System.out.println("Library test result: " + (libraryWorking ? "✅ Working" : "❌ Failed"));
            System.out.println();
            
            // Test with actual image file if provided as argument
            if (args.length > 0) {
                testWithImageFile(args[0]);
            } else {
                System.out.println("💡 To test with an image file, provide the path as an argument:");
                System.out.println("   java cn.lihongjie.image.FastImageUtilsTest path/to/your/image.jpg");
                System.out.println();
                
                // Test convenience methods with minimal data
                testConvenienceMethods();
            }
            
            // Test error handling
            testErrorHandling();
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testWithImageFile(String imagePath) {
        try {
            byte[] imageData = Files.readAllBytes(Paths.get(imagePath));
            System.out.println("📁 Loaded image: " + imagePath);
            System.out.println("📏 Original size: " + formatBytes(imageData.length));
            System.out.println();
            
            // Test main compress method with different quality levels
            System.out.println("🎯 Testing FastImageUtils.compress() with different quality levels:");
            int[] qualities = {30, 50, 70, 90};
            
            for (int quality : qualities) {
                long startTime = System.currentTimeMillis();
                byte[] compressed = FastImageUtils.compress(imageData, quality);
                long endTime = System.currentTimeMillis();
                
                double compressionRatio = (double) compressed.length / imageData.length;
                long timeTaken = endTime - startTime;
                
                System.out.printf("  Quality %d: %s (%.1f%% of original) - %d ms%n", 
                    quality, formatBytes(compressed.length), compressionRatio * 100, timeTaken);
            }
            System.out.println();
            
            // Test convenience methods
            System.out.println("🚀 Testing convenience methods:");
            
            long startTime = System.currentTimeMillis();
            byte[] highQuality = FastImageUtils.compressHigh(imageData);
            long highTime = System.currentTimeMillis() - startTime;
            
            startTime = System.currentTimeMillis();
            byte[] mediumQuality = FastImageUtils.compressMedium(imageData);
            long mediumTime = System.currentTimeMillis() - startTime;
            
            startTime = System.currentTimeMillis();
            byte[] lowQuality = FastImageUtils.compressLow(imageData);
            long lowTime = System.currentTimeMillis() - startTime;
            
            System.out.printf("  High Quality (90%%):   %s (%.1f%% of original) - %d ms%n",
                formatBytes(highQuality.length), 
                (double) highQuality.length / imageData.length * 100, highTime);
            
            System.out.printf("  Medium Quality (60%%): %s (%.1f%% of original) - %d ms%n",
                formatBytes(mediumQuality.length),
                (double) mediumQuality.length / imageData.length * 100, mediumTime);
            
            System.out.printf("  Low Quality (30%%):    %s (%.1f%% of original) - %d ms%n",
                formatBytes(lowQuality.length),
                (double) lowQuality.length / imageData.length * 100, lowTime);
            
        } catch (IOException e) {
            System.err.println("❌ Cannot read image file: " + e.getMessage());
        }
    }
    
    private static void testConvenienceMethods() {
        System.out.println("🧪 Testing convenience methods with minimal data...");
        
        // Create minimal test data (will fail compression but test the methods)
        byte[] testData = createMinimalTestData();
        
        try {
            FastImageUtils.compressHigh(testData);
            System.out.println("⚠️ compressHigh() completed (unexpected)");
        } catch (RuntimeException e) {
            System.out.println("✅ compressHigh() correctly handled invalid data");
        }
        
        try {
            FastImageUtils.compressMedium(testData);
            System.out.println("⚠️ compressMedium() completed (unexpected)");
        } catch (RuntimeException e) {
            System.out.println("✅ compressMedium() correctly handled invalid data");
        }
        
        try {
            FastImageUtils.compressLow(testData);
            System.out.println("⚠️ compressLow() completed (unexpected)");
        } catch (RuntimeException e) {
            System.out.println("✅ compressLow() correctly handled invalid data");
        }
        
        System.out.println();
    }
    
    private static void testErrorHandling() {
        System.out.println("🧪 Testing error handling:");
        
        // Test with empty data
        try {
            FastImageUtils.compress(new byte[0], 50);
            System.out.println("❌ Should have failed with empty data");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Correctly rejected empty data: " + e.getMessage());
        }
        
        // Test with invalid quality values
        byte[] testData = createMinimalTestData();
        
        try {
            FastImageUtils.compress(testData, -1);
            System.out.println("❌ Should have failed with negative quality");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Correctly rejected negative quality: " + e.getMessage());
        }
        
        try {
            FastImageUtils.compress(testData, 150);
            System.out.println("❌ Should have failed with quality > 100");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Correctly rejected quality > 100: " + e.getMessage());
        }
    }
    
    private static byte[] createMinimalTestData() {
        // Create minimal PNG signature data
        return new byte[] {
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A // PNG signature
        };
    }
    
    private static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }
    }
}
