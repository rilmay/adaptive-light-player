package uk.co.caprica.vlcjplayer.light;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import uk.co.caprica.vlcjplayer.view.main.MainFrame;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JsonParser {
    private final static String IP_PATTERN = "^(([0-9]){1,3}\\.){1,3}[0-9]{1,3}$";
    private final static String BRIGHTNESS_RATE_TAG = "brightness_rate";
    private final static String TEMPERATURE_RATE_TAG = "temperature_rate";
    private final static String DEVICES_TAG = "devices";
    private final static String ROOM_CHECK_TAG = "room_check";
    private final static String FEATURES_TAG = "features";
    private final static String CHECK_INTERVAL_TAG = "check_interval";
    private final static String REFRESH_INTERVAL_TAG = "refresh_interval";
    private final static String IP_TAG = "ip";
    private final static String PORT_TAG = "port";
    private final static String SCREEN_AREA_TAG = "screen_area";

    public static LightConfig getLightConfig(String filename) {
        LightConfig config = null;
        try (FileReader reader = new FileReader((filename == null) ? Defaults.CONFIG_FILENAME : filename)) {
            config = JsonParser.parse(reader);
        } catch (FileNotFoundException e) {
            if (filename == null) {
                Optional.ofNullable(findFile()).ifPresent(JsonParser::getLightConfig);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MainFrame.instance(),"Cant read file, give up, message: " + e.getMessage(), "Config file error", JOptionPane.ERROR_MESSAGE);
        }
        return config;
    }

    private static String findFile(){
        String path = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".json") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "json";
            }
        });

        int chosenResult = chooser.showOpenDialog(MainFrame.instance());
        if (chosenResult == 0) {
            File f = chooser.getSelectedFile();
            path = f.getAbsolutePath();
        }
        return path;
    }


    private static LightConfig parse(FileReader reader) {
        JSONParser jsonParser = new JSONParser();
        Object jsonConfigRaw;
        try {
            jsonConfigRaw = jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            return null;
        }
        if (!(jsonConfigRaw instanceof JSONObject)) {
            return null;
        }

        JSONObject jsonConfig = (JSONObject) jsonConfigRaw;
        LightConfig config = new LightConfig();

        Object devices = getValue(DEVICES_TAG, jsonConfig, null);
        if (!(devices instanceof JSONArray)) {
            return null;
        }
        Set<LightDevice> deviceSet = (Set<LightDevice>) ((JSONArray) devices).stream()
                .filter(x -> x instanceof JSONObject && getValue(IP_TAG, (JSONObject) x, "").matches(IP_PATTERN))
                .map(x -> {
                    JSONObject obj = (JSONObject) x;
                    LightDevice device = new LightDevice();
                    device.setPort(getValue(PORT_TAG, obj, Defaults.PORT));
                    device.setIp(getValue(IP_TAG, obj, null));
                    device.setScreenArea(ScreenArea.getTag(getValue(SCREEN_AREA_TAG, obj, "")).orElse(ScreenArea.WHOLE_SCREEN));
                    Set<Feature> featureSet;
                    Object features = getValue(FEATURES_TAG, obj, null);
                    if (features instanceof JSONArray) {
                        featureSet = (Set<Feature>) ((JSONArray) features).stream()
                                .map(f -> Feature.getTag(f.toString()).orElse(Feature.BRIGHTNESS))
                                .collect(Collectors.toSet());
                    } else {
                        featureSet = Collections.singleton(Feature.BRIGHTNESS);
                    }
                    device.setFeatures(featureSet);
                    return device;
                }).collect(Collectors.toSet());
        if (deviceSet.isEmpty()) {
            return null;
        }
        config.setDevices(deviceSet);

        String roomCheck = getValue(ROOM_CHECK_TAG, jsonConfig, null);
        if (roomCheck != null) {
            config.setCheck(true);
            Integer checkInterval = getValue(CHECK_INTERVAL_TAG, jsonConfig, Defaults.CHECK_INTERVAL_MIN);
            config.setCheckInterval(checkInterval);
        }

        double brightnessRate = getValue(BRIGHTNESS_RATE_TAG, jsonConfig, Defaults.BRIGHTNESS_RATE);
        config.setBrightnessRate(brightnessRate);

        double temperatureRate = getValue(TEMPERATURE_RATE_TAG, jsonConfig, Defaults.TEMPERATURE_RATE);
        config.setTemperatureRate(temperatureRate);

        int refreshInterval = getValue(REFRESH_INTERVAL_TAG, jsonConfig, Defaults.REFRESH_INTERVAL_MSEC);
        config.setRefreshInterval(refreshInterval);

        return config;
    }

    private static <T> T getValue(String tagName, JSONObject object, T defaultValue) {
        T value = defaultValue;
        Object valueRaw = object.get(tagName);
        if (valueRaw != null && (defaultValue == null || defaultValue.getClass().isAssignableFrom(valueRaw.getClass()))) {
            value = (T) valueRaw;
        } else if (valueRaw instanceof Number) {
            if (defaultValue instanceof Double) {
                value = (T) new Double(((Number) valueRaw).doubleValue());
            } else if (defaultValue instanceof Integer) {
                value = (T) new Integer(((Number) valueRaw).intValue());
            }
        }
        return value;
    }
}
