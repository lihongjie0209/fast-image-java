package cn.lihongjie.image.performance;

import cn.lihongjie.image.FastImageUtils;
import cn.lihongjie.image.util.TestImageLoader;
import org.openjdk.jmh.annotations.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * JMH Performance benchmarks comparing native FastImage library with Java built-in implementations
 * 
 * Tests:
 * 1. JPEG Compression - Native (mozjpeg) vs Native (fast) vs Java ImageIO
 * 2. PNG Compression - Native (imagequant + png) vs Java ImageIO  
 * 3. Image Rotation - Native (image crate) vs Java Graphics2D
 * 
 * Note: Java PNG compression doesn't support quality parameter but does basic compression
 * 
 * Run with: mvn jmh:run
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
public class ImageProcessingBenchmark {

    // Test data - different sizes and formats
    private byte[] smallJpegData;
    private byte[] mediumJpegData;
    private byte[] largeJpegData;
    
    private byte[] smallPngData;
    private byte[] mediumPngData;
    private byte[] largePngData;

    @Setup
    public void setup() throws IOException {
        // Load real test images of different sizes
        smallJpegData = TestImageLoader.loadSmallJpegImage();     // 700K.jpg
        mediumJpegData = TestImageLoader.loadMediumJpegImage();   // 1.5M.jpg  
        largeJpegData = TestImageLoader.loadLargeJpegImage();     // 7.8M.jpg
        
        smallPngData = TestImageLoader.loadSmallPngImage();       // 500K.png
        mediumPngData = TestImageLoader.loadMediumPngImage();     // 2M.png
        largePngData = TestImageLoader.loadLargePngImage();       // 5M.png
        
        System.out.println("Benchmark setup completed:");
        System.out.printf("Small JPEG: %d bytes%n", smallJpegData.length);
        System.out.printf("Medium JPEG: %d bytes%n", mediumJpegData.length);
        System.out.printf("Large JPEG: %d bytes%n", largeJpegData.length);
        System.out.printf("Small PNG: %d bytes%n", smallPngData.length);
        System.out.printf("Medium PNG: %d bytes%n", mediumPngData.length);
        System.out.printf("Large PNG: %d bytes%n", largePngData.length);
    }

    // =================================================================
    // JPEG COMPRESSION BENCHMARKS
    // =================================================================

    @Benchmark
    public byte[] nativeJpegCompressionSmall() {
        return FastImageUtils.compress(smallJpegData, 70);
    }

    @Benchmark
    public byte[] javaJpegCompressionSmall() throws IOException {
        return compressJpegWithJava(smallJpegData, 0.7f);
    }

    @Benchmark
    public byte[] nativeJpegCompressionMedium() {
        return FastImageUtils.compress(mediumJpegData, 70);
    }

    @Benchmark
    public byte[] javaJpegCompressionMedium() throws IOException {
        return compressJpegWithJava(mediumJpegData, 0.7f);
    }

    @Benchmark
    public byte[] nativeJpegCompressionLarge() {
        return FastImageUtils.compress(largeJpegData, 70);
    }

    @Benchmark
    public byte[] javaJpegCompressionLarge() throws IOException {
        return compressJpegWithJava(largeJpegData, 0.7f);
    }

    // =================================================================
    // FAST JPEG COMPRESSION BENCHMARKS
    // =================================================================

    @Benchmark
    public byte[] nativeFastJpegCompressionSmall() {
        return FastImageUtils.compressJpegFast(smallJpegData, 70);
    }

    @Benchmark
    public byte[] nativeFastJpegCompressionMedium() {
        return FastImageUtils.compressJpegFast(mediumJpegData, 70);
    }

    @Benchmark
    public byte[] nativeFastJpegCompressionLarge() {
        return FastImageUtils.compressJpegFast(largeJpegData, 70);
    }

    // =================================================================
    // PNG COMPRESSION BENCHMARKS
    // =================================================================

    @Benchmark
    public byte[] nativePngCompressionSmall() {
        return FastImageUtils.compress(smallPngData, 70);
    }

//    @Benchmark
//    public byte[] javaPngCompressionSmall() throws IOException {
//        return compressPngWithJava(smallPngData);
//    }
//
//    @Benchmark
//    public byte[] nativePngCompressionMedium() {
//        return FastImageUtils.compress(mediumPngData, 70);
//    }
//
//    @Benchmark
//    public byte[] javaPngCompressionMedium() throws IOException {
//        return compressPngWithJava(mediumPngData);
//    }
//
//    @Benchmark
//    public byte[] nativePngCompressionLarge() {
//        return FastImageUtils.compress(largePngData, 70);
//    }
//
//    @Benchmark
//    public byte[] javaPngCompressionLarge() throws IOException {
//        return compressPngWithJava(largePngData);
//    }

    // =================================================================
    // IMAGE ROTATION BENCHMARKS
    // =================================================================

    @Benchmark
    public byte[] nativeRotationSmallPng() {
        return FastImageUtils.rotate90(smallPngData);
    }

    @Benchmark
    public byte[] javaRotationSmallPng() throws IOException {
        return rotateImageWithJava(smallPngData, 90, "PNG");
    }

    @Benchmark
    public byte[] nativeRotationMediumPng() {
        return FastImageUtils.rotate90(mediumPngData);
    }

    @Benchmark
    public byte[] javaRotationMediumPng() throws IOException {
        return rotateImageWithJava(mediumPngData, 90, "PNG");
    }

    @Benchmark
    public byte[] nativeRotationLargePng() {
        return FastImageUtils.rotate90(largePngData);
    }

    @Benchmark
    public byte[] javaRotationLargePng() throws IOException {
        return rotateImageWithJava(largePngData, 90, "PNG");
    }

    @Benchmark
    public byte[] nativeRotationSmallJpeg() {
        return FastImageUtils.rotate90(smallJpegData);
    }

    @Benchmark
    public byte[] javaRotationSmallJpeg() throws IOException {
        return rotateImageWithJava(smallJpegData, 90, "JPEG");
    }

    @Benchmark
    public byte[] nativeRotationMediumJpeg() {
        return FastImageUtils.rotate90(mediumJpegData);
    }

    @Benchmark
    public byte[] javaRotationMediumJpeg() throws IOException {
        return rotateImageWithJava(mediumJpegData, 90, "JPEG");
    }

    @Benchmark
    public byte[] nativeRotationLargeJpeg() {
        return FastImageUtils.rotate90(largeJpegData);
    }

    @Benchmark
    public byte[] javaRotationLargeJpeg() throws IOException {
        return rotateImageWithJava(largeJpegData, 90, "JPEG");
    }

    // =================================================================
    // COMBINED OPERATIONS BENCHMARKS
    // =================================================================

    @Benchmark
    public byte[] nativeCombinedRotateAndCompressJpeg() {
        byte[] rotated = FastImageUtils.rotate90(mediumJpegData);
        return FastImageUtils.compress(rotated, 70);
    }

    @Benchmark
    public byte[] javaCombinedRotateAndCompressJpeg() throws IOException {
        byte[] rotated = rotateImageWithJava(mediumJpegData, 90, "JPEG");
        return compressJpegWithJava(rotated, 0.7f);
    }

    @Benchmark
    public byte[] nativeCombinedRotateAndCompressPng() {
        byte[] rotated = FastImageUtils.rotate90(mediumPngData);
        return FastImageUtils.compress(rotated, 70);
    }

//    @Benchmark
//    public byte[] javaCombinedRotateAndCompressPng() throws IOException {
//        byte[] rotated = rotateImageWithJava(mediumPngData, 90, "PNG");
//        return compressPngWithJava(rotated);
//    }

    @Benchmark
    public byte[] nativeFastCombinedRotateAndCompressJpeg() {
        byte[] rotated = FastImageUtils.rotate90(mediumJpegData);
        return FastImageUtils.compressJpegFast(rotated, 70);
    }

    // =================================================================
    // HELPER METHODS FOR JAVA IMPLEMENTATIONS
    // =================================================================

    /**
     * Compress JPEG using Java's built-in ImageIO with quality control
     */
    private byte[] compressJpegWithJava(byte[] imageData, float quality) throws IOException {
        // Read the image
        BufferedImage image;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
            image = ImageIO.read(bais);
        }

        if (image == null) {
            throw new IOException("Failed to read image data");
        }

        // Write with compression
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        var writers = ImageIO.getImageWritersByFormatName("JPEG");
        if (!writers.hasNext()) {
            throw new IOException("No JPEG writers available");
        }
        
        var writer = writers.next();
        var ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);
        
        var param = writer.getDefaultWriteParam();
        param.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);
        
        writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
        
        writer.dispose();
        ios.close();
        
        return baos.toByteArray();
    }

//    /**
//     * Compress PNG using Java's built-in ImageIO (no quality parameter, just basic compression)
//     */
//    private byte[] compressPngWithJava(byte[] imageData) throws IOException {
//        // Read the image
//        BufferedImage image;
//        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
//            image = ImageIO.read(bais);
//        }
//
//        if (image == null) {
//            throw new IOException("Failed to read image data");
//        }
//
//        // Write PNG with basic compression (Java doesn't support quality-based PNG compression)
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        var writers = ImageIO.getImageWritersByFormatName("PNG");
//        if (!writers.hasNext()) {
//            throw new IOException("No PNG writers available");
//        }
//
//        var writer = writers.next();
//        var ios = ImageIO.createImageOutputStream(baos);
//        writer.setOutput(ios);
//
//        // PNG compression in Java is automatic, no quality parameter available
//        writer.write(null, new javax.imageio.IIOImage(image, null, null), null);
//
//        writer.dispose();
//        ios.close();
//
//        return baos.toByteArray();
//    }

    /**
     * Rotate image using Java's Graphics2D
     */
    private byte[] rotateImageWithJava(byte[] imageData, int angleDegrees, String format) throws IOException {
        // Read the image
        BufferedImage originalImage;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
            originalImage = ImageIO.read(bais);
        }

        if (originalImage == null) {
            throw new IOException("Failed to read image data");
        }

        // Calculate rotation
        double angleRadians = Math.toRadians(angleDegrees);
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Calculate new dimensions after rotation
        double sin = Math.abs(Math.sin(angleRadians));
        double cos = Math.abs(Math.cos(angleRadians));
        int newWidth = (int) Math.round(originalWidth * cos + originalHeight * sin);
        int newHeight = (int) Math.round(originalWidth * sin + originalHeight * cos);

        // Create rotated image
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g2d = rotatedImage.createGraphics();
        
        try {
            // Set rendering hints for quality
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Create transform
            AffineTransform transform = new AffineTransform();
            transform.translate(newWidth / 2.0, newHeight / 2.0);
            transform.rotate(angleRadians);
            transform.translate(-originalWidth / 2.0, -originalHeight / 2.0);

            // Apply transform and draw
            g2d.setTransform(transform);
            g2d.drawImage(originalImage, 0, 0, null);
        } finally {
            g2d.dispose();
        }

        // Convert back to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(rotatedImage, format, baos);
        return baos.toByteArray();
    }
}
