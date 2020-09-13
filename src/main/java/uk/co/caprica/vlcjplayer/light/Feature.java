package uk.co.caprica.vlcjplayer.light;

import java.util.Arrays;
import java.util.Optional;

public enum Feature {
    BRIGHTNESS,
    TEMPERATURE,
    COLOR;

    public static Optional<Feature> getTag(String feature) {
        return Arrays.stream(Feature.values()).filter(e -> e.name().toLowerCase().equals(feature)).findFirst();
    }

}
