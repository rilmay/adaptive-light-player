package uk.co.caprica.vlcjplayer.light;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class ImageHandler {
    private static List<ScreenArea> mainAreas = Arrays.asList(ScreenArea.TOP_LEFT, ScreenArea.TOP_RIGHT, ScreenArea.BOTTOM_LEFT, ScreenArea.BOTTOM_RIGHT);
    private static int scaledWidth = 20;
    private static int scaledHeight = 20;
    private static int scaledWidthCenter = (scaledWidth) / 2;
    private static int scaledHeightCenter = (scaledHeight) / 2;
    private Map<ScreenArea, Integer> screenData;
    private LightConfig config;

    private int[] getDimensions(ScreenArea area) {
        int[] dimensions = new int[4];
        if (!mainAreas.contains(area)) {
            return dimensions;
        }
        String name = area.name().toLowerCase();
        dimensions[0] = (name.contains("left")) ? 0 : scaledWidthCenter;
        dimensions[1] = (name.contains("top")) ? 0 : scaledHeightCenter;
        dimensions[2] = scaledWidthCenter;
        dimensions[3] = scaledHeightCenter;
        return dimensions;
    }

    private BufferedImage getScaledImage(BufferedImage image, int width, int height) {
        Image tmp = image.getScaledInstance(width, height, Image.SCALE_FAST);
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return scaledImage;
    }

    public ImageHandler(BufferedImage image, LightConfig config) {
        this.config = config;
        BufferedImage scaledImage = getScaledImage(image, scaledWidth, scaledHeight);

        screenData = new HashMap<>();
        mainAreas.forEach(area -> {
            int[] dimensions = getDimensions(area);
            BufferedImage subImage = scaledImage.getSubimage(dimensions[0], dimensions[1], dimensions[2], dimensions[3]);

            int average = IntStream.range(0, dimensions[3])
                    .flatMap(row -> IntStream.range(0, dimensions[2]).map(col -> subImage.getRGB(col, row))).boxed()
                    .reduce(new ColorAveragerer(), (t, u) -> {
                        t.accept(u);
                        return t;
                    }, (t, u) -> {
                        t.combine(u);
                        return t;
                    }).average();

            screenData.put(area, average);
        });
    }


    public int getValue(ScreenArea area, Feature feature, Boolean considerRate) {
        Integer intValue = screenData.get(area);
        if (intValue != null) {
            Color color = new Color(intValue);
            if (feature == Feature.COLOR) {
                return color.getRGB();
            } else if (feature == Feature.BRIGHTNESS) {
                int value = (int) ((color.getRed() * 0.2126f + color.getGreen() * 0.7152f + color.getBlue() * 0.0722f) / 255 * 100);
                if (considerRate) {
                    value = 10 + (int) (value * config.getBrightnessRate());
                }
                return (value > 100) ? 100 : value;
            } else if (feature == Feature.TEMPERATURE) {
                int value = (int) ((float) (color.getRed() - color.getBlue()) / 255 * 100);
                value = (value < 0) ? 0 : value;
                if (considerRate) {
                    value = 10 + (int) (value * config.getTemperatureRate());
                }
                value = (value > 100) ? 100 : value;
                return value;
            } else {
                return 0;
            }
        } else {
            calculateArea(area);
            return getValue(area, feature, considerRate);
        }
    }

    private void calculateArea(ScreenArea area) {
        int value = 0;
        switch (area) {
            case TOP:
                value = getAverage(ScreenArea.TOP_LEFT, ScreenArea.TOP_RIGHT);
                break;
            case BOTTOM:
                value = getAverage(ScreenArea.BOTTOM_LEFT, ScreenArea.BOTTOM_RIGHT);
                break;
            case LEFT:
                value = getAverage(ScreenArea.BOTTOM_LEFT, ScreenArea.TOP_LEFT);
                break;
            case RIGHT:
                value = getAverage(ScreenArea.BOTTOM_RIGHT, ScreenArea.TOP_RIGHT);
                break;
            case WHOLE_SCREEN:
                value = getAverage(mainAreas.toArray(new ScreenArea[0]));
                break;
        }
        screenData.put(area, value);
    }

    private int getAverage(ScreenArea... areas) {
        return Arrays.stream(areas).map(color -> screenData.get(color))
                .reduce(new ColorAveragerer(), (t, u) -> {
                    t.accept(u);
                    return t;
                }, (t, u) -> {
                    t.combine(u);
                    return t;
                }).average();
    }


    public static int[] getRgbArray(int color) {
        int[] rgb = new int[3];
        rgb[0] = (color >>> 16) & 0xFF;
        rgb[1] = (color >>> 8) & 0xFF;
        rgb[2] = (color >>> 0) & 0xFF;
        return rgb;
    }

    public static int getRgbInt(int[] pixel) {
        int value = ((255 & 0xFF) << 24) |
                ((pixel[0] & 0xFF) << 16) |
                ((pixel[1] & 0xFF) << 8) |
                ((pixel[2] & 0xFF) << 0);
        return value;
    }


    private class ColorAveragerer {
        private int[] total = new int[]{0, 0, 0};
        private int count = 0;

        private ColorAveragerer() {
        }

        private int average() {
            int[] rgb = new int[3];
            for (int it = 0; it < total.length; it++) {
                rgb[it] = total[it] / count;
            }

            return count > 0 ? getRgbInt(rgb) : 0;
        }

        private void accept(int i) {
            int[] rgb = getRgbArray(i);
            for (int it = 0; it < total.length; it++) {
                total[it] += rgb[it];
            }
            count++;
        }

        private void combine(ColorAveragerer other) {
            for (int it = 0; it < total.length; it++) {
                total[it] += other.total[it];
            }
            count += other.count;
        }
    }
}
