package cn.lihongjie.image;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Fast Image Compression Utilities with Cross-Platform Support
 * 
 * This utility class handles automatic loading of native libraries
 * for different operating systems and architectures.
 * 
 * Supports:
 * - Windows x64 (.dll)
 * - Linux x64 (.so)  
 * - macOS Intel x64 (.dylib)
 * - macOS Apple Silicon ARM64 (.dylib)
 * 
 * Usage:
 * <pre>
 * // Compress an image with 70% quality
 * byte[] imageData = Files.readAllBytes(Paths.get("image.jpg"));
 * byte[] compressed = FastImageUtils.compress(imageData, 70);
 * </pre>
 */
public class FastImageUtils {
    
    private static boolean isInitialized = false;
    private static RuntimeException initializationError = null;
    
    // Static initialization block
    static {
        try {
            init();
            isInitialized = true;
        } catch (RuntimeException e) {
            initializationError = e;
        }
    }
    
    /**
     * Initialize the native library by detecting platform and loading appropriate library
     * 
     * @throws RuntimeException if library loading fails
     */
    private static void init() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        
        // Detect platform and architecture
        String platform = detectPlatform(osName, osArch);
        String libraryName = getLibraryName(platform);
        
        try {
            // Try to load library from resources
            loadLibraryFromResources(libraryName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Fast Image native library for platform: " + platform, e);
        }
    }
    
    /**
     * Detect the current platform and architecture
     * 
     * @param osName Operating system name
     * @param osArch Architecture name
     * @return Platform identifier
     */
    private static String detectPlatform(String osName, String osArch) {
        // Normalize architecture names
        String normalizedArch;
        if (osArch.contains("64")) {
            if (osArch.contains("aarch") || osArch.contains("arm")) {
                normalizedArch = "aarch64";
            } else {
                normalizedArch = "x86_64";
            }
        } else if (osArch.contains("86")) {
            normalizedArch = "x86";
        } else if (osArch.contains("arm") || osArch.contains("aarch")) {
            normalizedArch = "aarch64";
        } else {
            normalizedArch = osArch;
        }
        
        // Detect OS
        if (osName.contains("windows")) {
            return "windows-" + normalizedArch;
        } else if (osName.contains("linux")) {
            return "linux-" + normalizedArch;
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            return "macos-" + normalizedArch;
        } else {
            throw new RuntimeException("Unsupported operating system: " + osName);
        }
    }
    
    /**
     * Get the native library file name for the platform
     * 
     * @param platform Platform identifier
     * @return Library file name
     */
    private static String getLibraryName(String platform) {
        switch (platform) {
            case "windows-x86_64":
                return "fast_image-windows-x86_64.dll";
            case "windows-aarch64":
                return "fast_image-windows-aarch64.dll";
            case "linux-x86_64":
                return "libfast_image-linux-x86_64.so";
            case "linux-aarch64":
                return "libfast_image-linux-aarch64.so";
            case "macos-x86_64":
                return "libfast_image-macos-x86_64.dylib";
            case "macos-aarch64":
                return "libfast_image-macos-aarch64.dylib";
            default:
                throw new RuntimeException("Unsupported platform: " + platform);
        }
    }
    
    /**
     * Load native library from JAR resources
     * 
     * @param libraryName Name of the library file
     * @throws IOException if library extraction or loading fails
     */
    private static void loadLibraryFromResources(String libraryName) throws IOException {
        String resourcePath = "/native/" + libraryName;
        
        // Get resource as stream
        try (InputStream in = FastImageUtils.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Native library not found in resources: " + resourcePath);
            }
            
            // Create temporary file
            String tempFileName = "fast_image_" + System.currentTimeMillis();
            String fileExtension = getFileExtension(libraryName);
            Path tempFile = Files.createTempFile(tempFileName, fileExtension);
            
            // Copy library to temporary file
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            
            // Load the library
            System.load(tempFile.toAbsolutePath().toString());
            
            // Delete temp file on exit
            tempFile.toFile().deleteOnExit();
        }
    }
    
    /**
     * Extract file extension from library name
     * 
     * @param libraryName Library file name
     * @return File extension with dot (e.g., ".dll", ".so", ".dylib")
     */
    private static String getFileExtension(String libraryName) {
        int lastDot = libraryName.lastIndexOf('.');
        return lastDot >= 0 ? libraryName.substring(lastDot) : "";
    }
    
    /**
     * Check if the native library is properly initialized
     * 
     * @throws RuntimeException if initialization failed
     */
    private static void ensureInitialized() {
        if (!isInitialized) {
            if (initializationError != null) {
                throw new RuntimeException("Fast Image native library initialization failed", initializationError);
            } else {
                throw new RuntimeException("Fast Image native library not initialized");
            }
        }
    }
    
    /**
     * Compress image data with automatic format detection and quality control
     * 
     * This method automatically detects whether the input is PNG or JPEG format
     * and applies the appropriate compression algorithm. The output format will
     * be the same as the input format (PNG input -> PNG output, JPEG input -> JPEG output).
     * 
     * @param imageBytes Input image data as byte array (PNG or JPEG format)
     * @param quality Compression quality (0-100, where 0 is highest compression, 100 is best quality)
     * @return Compressed image data as byte array in the same format as input
     * @throws IllegalArgumentException if quality is not in range 0-100 or data is empty
     * @throws RuntimeException if compression fails or image format is unsupported
     */
    public static byte[] compress(byte[] imageBytes, int quality) {
        ensureInitialized();
        return compressNative(imageBytes, quality);
    }
    
    /**
     * Compress image with high quality (quality = 90)
     * Output format will be the same as input format.
     * 
     * @param imageBytes Input image data (PNG or JPEG format)
     * @return Compressed image data in the same format as input
     */
    public static byte[] compressHigh(byte[] imageBytes) {
        return compress(imageBytes, 90);
    }
    
    /**
     * Compress image with medium quality (quality = 60)
     * Output format will be the same as input format.
     * 
     * @param imageBytes Input image data (PNG or JPEG format)
     * @return Compressed image data in the same format as input
     */
    public static byte[] compressMedium(byte[] imageBytes) {
        return compress(imageBytes, 60);
    }
    
    /**
     * Compress image with low quality for maximum compression (quality = 30)
     * Output format will be the same as input format.
     * 
     * @param imageBytes Input image data (PNG or JPEG format)
     * @return Compressed image data in the same format as input
     */
    public static byte[] compressLow(byte[] imageBytes) {
        return compress(imageBytes, 30);
    }
    
    /**
     * Get information about the current platform and loaded library
     * 
     * @return Platform information string
     */
    public static String getPlatformInfo() {
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String javaVersion = System.getProperty("java.version");
        
        try {
            String platform = detectPlatform(osName.toLowerCase(), osArch.toLowerCase());
            String libraryName = getLibraryName(platform);
            
            return String.format(
                "Platform: %s (%s)\nArchitecture: %s\nJava: %s\nNative Library: %s\nInitialized: %s",
                osName, platform, osArch, javaVersion, libraryName, isInitialized
            );
        } catch (Exception e) {
            return String.format(
                "Platform: %s\nArchitecture: %s\nJava: %s\nError: %s",
                osName, osArch, javaVersion, e.getMessage()
            );
        }
    }
    
    /**
     * Test if the native library is working correctly
     * 
     * @return true if library is working, false otherwise
     */
    public static boolean testLibrary() {
        try {
            ensureInitialized();
            
            // Create minimal test data (PNG signature)
            byte[] testData = {
                (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
            };
            
            // This should fail but not crash
            compress(testData, 50);
            return false; // Should not reach here
        } catch (RuntimeException e) {
            // Expected - minimal data should fail compression but not crash
            return e.getMessage().contains("compression failed") || 
                   e.getMessage().contains("Failed to load");
        } catch (Exception e) {
            return false; // Unexpected exception
        }
    }
    
    // Native method declaration
    private static native byte[] compressNative(byte[] imageBytes, int quality);
}
