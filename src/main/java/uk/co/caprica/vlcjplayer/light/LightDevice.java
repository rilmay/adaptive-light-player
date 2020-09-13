package uk.co.caprica.vlcjplayer.light;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LightDevice {
    private String ip;
    private int port;
    private Map<Feature, Integer> featuresWithValues;
    private ScreenArea screenArea;

    public ScreenArea getScreenArea() {
        return screenArea;
    }

    public void setScreenArea(ScreenArea screenArea) {
        this.screenArea = screenArea;
    }

    public LightDevice(String ip, int port, Set<Feature> features, ScreenArea screenArea) {
        this.ip = ip;
        this.port = port;
        this.screenArea = screenArea;
        this.setFeatures(features);
    }

    public LightDevice() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Set<Feature> getFeatures() {
        return featuresWithValues.keySet();
    }

    public void setFeatures(Set<Feature> features) {
        this.featuresWithValues = new HashMap<>();
        features.forEach(feature -> featuresWithValues.put(feature, Defaults.INITIAL_VALUE));
    }

    public Integer getCurrentValueByFeature(Feature feature) {
        return featuresWithValues.get(feature);
    }

    public void setCurrentValueByFeature(Feature feature, int value) {
        if(featuresWithValues.keySet().contains(feature)){
            featuresWithValues.put(feature, value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LightDevice device = (LightDevice) o;
        return ip.equals(device.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip);
    }

    @Override
    public String toString() {
        return "LightDevice{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", screenArea=" + screenArea +
                ", features=" + featuresWithValues.keySet() +
                '}';
    }
}
