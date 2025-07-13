package PerformanceOptimizations;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static final String SOURCE_FILE = "./resources/many-flowers.jpg";
    public static final String DESTINATION_FILE = "./out/many-flowers.jpg";

    public static void main(String[] args) throws IOException {

        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        long startTime = System.currentTimeMillis();

        //Performed these tests on my laptop with an 16 core CPU

        recolorSingleThreaded(originalImage, resultImage); // took 388 milliseconds with 1 thread

        //recolorMultiThreaded(originalImage, resultImage, 4); // took 3 milliseconds with 4 threads

        //recolorMultiThreaded(originalImage, resultImage, 16); // took 3 milliseconds with 16 threads

        //recolorMultiThreaded(originalImage, resultImage, 32); // took 10 milliseconds with 32 threads

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(resultImage, "jpg", outputFile);

        System.out.println(duration);

    }

    public static void recolorMultiThreaded(BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int segmentHeight = height / numberOfThreads;

        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                int topCorner = threadIndex * segmentHeight;

                recolorImage(originalImage, resultImage, 0, topCorner, width, segmentHeight);
            });
            threads[i].start();
        }
    }

    public static void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }


    public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner,
                                    int width, int height) {
        for(int x = leftCorner ; x < leftCorner + width && x < originalImage.getWidth() ; x++) {
            for(int y = topCorner ; y < topCorner + height && y < originalImage.getHeight() ; y++) {
                recolorPixel(originalImage, resultImage, x , y);
            }
        }
    }

    /**
     * Recolors a single pixel in the result image based on the color of the corresponding pixel in the original image.
     * If the pixel is a shade of gray, it modifies the RGB values to create a new color.
     *
     * @param originalImage The original BufferedImage from which to read pixel colors.
     * @param resultImage   The BufferedImage where the recolored pixel will be set.
     * @param x             The x-coordinate of the pixel to recolor.
     * @param y             The y-coordinate of the pixel to recolor.
     */
    public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;

        if(isShadeOfGray(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    /**
     * Sets the RGB value of a pixel in a BufferedImage at the specified coordinates.
     *
     * @param image The BufferedImage to modify.
     * @param x     The x-coordinate of the pixel.
     * @param y     The y-coordinate of the pixel.
     * @param rgb   The RGB value to set (in the format 0xAARRGGBB).
     */
    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    /**
     * Checks if the given RGB values represent a shade of gray.
     * A color is considered a shade of gray if the red, green, and blue components are close to each other.
     *
     * @param red   The red component of the color.
     * @param green The green component of the color.
     * @param blue  The blue component of the color.
     * @return true if the color is a shade of gray, false otherwise.
     */
    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs( green - blue) < 30;
    }

    /**
     * Creates an RGB integer from the given red, green, and blue color components.
     * The alpha channel is set to 255 (fully opaque).
     *
     * @param red   The red component (0-255).
     * @param green The green component (0-255).
     * @param blue  The blue component (0-255).
     * @return An integer representing the RGB color.
     */
    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8; // shift green 8 bits to the left to place it in the correct position
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0xFF0000) >> 16; // get a full value of 0x00red0000, bcz x AND 1 returns x. >> 16 shifts it to the right
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x00FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x0000FF;
    }
}
