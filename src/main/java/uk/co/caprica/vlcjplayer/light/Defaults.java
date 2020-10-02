package uk.co.caprica.vlcjplayer.light;

public class Defaults {
    public static final String ROOM_CHECK_FALSE = "false";
    public static final int CHECK_INTERVAL_MIN = 30;
    public static final double BRIGHTNESS_RATE = 1.5;
    public static final double TEMPERATURE_RATE = 1.5;
    public static final int PORT = 55443;
    public static final int REFRESH_INTERVAL_MSEC = 1000;
    public static final int DURATION = 1000;
    public static final String CONFIG_FILENAME = "config.json";
    public static final int CHANGE_VALUE_TRESHOLD = 10;
    public static final int INITIAL_VALUE = 0;

    public static final int TEMPERATURE_MAX = 5000;
    public static final int TEMPERATURE_MIN = 2600;

    public static double getTemperatureRate(double inputRate){
        return (inputRate < 0.5 || inputRate > 4.0)? TEMPERATURE_RATE: inputRate;
    }

    public static double getBrightnessRate(double inputRate){
        return (inputRate < 0.5 || inputRate > 4.0)? BRIGHTNESS_RATE: inputRate;
    }

    public static int getCheckInterval(int inputInterval){
        return (inputInterval < 1 || inputInterval > 120)? CHECK_INTERVAL_MIN: inputInterval;
    }

    public static int getRefreshInterval(int inputInterval){
        return (inputInterval < 500 || inputInterval > 120000)? REFRESH_INTERVAL_MSEC: inputInterval;
    }
}
