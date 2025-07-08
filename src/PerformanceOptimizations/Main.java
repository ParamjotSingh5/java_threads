package PerformanceOptimizations;

public class Main {

    public static void main(String[] args) {
        // Example usage of the RGB color manipulation methods
        int red = 255;
        int green = 100;
        int blue = 50;

        int rgb = createRGBFromColors(red, green, blue);
        System.out.println("RGB Value: " + Integer.toHexString(rgb));

        System.out.println("Red: " + getRed(rgb));
        System.out.println("Green: " + getGreen(rgb));
        System.out.println("Blue: " + getBlue(rgb));
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }
}
