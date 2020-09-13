package uk.co.caprica.vlcjplayer.light;

import java.util.Arrays;
import java.util.Optional;

public enum ScreenArea {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    WHOLE_SCREEN,
    TOP,
    BOTTOM,
    LEFT,
    RIGHT;

    public static Optional<ScreenArea> getTag(String corner) {
        return Arrays.stream(ScreenArea.values())
                .filter(e -> e.name().toLowerCase().replaceAll("_"," ").equals(corner))
                .findFirst();
    }
}
