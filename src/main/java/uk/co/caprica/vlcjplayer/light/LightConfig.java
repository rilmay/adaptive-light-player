package uk.co.caprica.vlcjplayer.light;

import java.util.Set;

public class LightConfig {
    private double brightnessRate;
    private double temperatureRate;
    private boolean check = false;
    private int checkInterval;
    private int refreshInterval;
    private Set<LightDevice> devices;

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public LightConfig() {
    }

    public LightConfig(double brightnessRate, double temperatureRate, int checkInterval, Set<LightDevice> devices) {
        this.brightnessRate = brightnessRate;
        this.temperatureRate = temperatureRate;
        this.check = true;
        this.checkInterval = checkInterval;
        this.devices = devices;
    }

    public double getBrightnessRate() {
        return brightnessRate;
    }

    public void setBrightnessRate(double brightnessRate) {
        this.brightnessRate = brightnessRate;
    }

    public double getTemperatureRate() {
        return temperatureRate;
    }

    public void setTemperatureRate(double temperatureRate) {
        this.temperatureRate = temperatureRate;
    }

    public Set<LightDevice> getDevices() {
        return devices;
    }

    public void setDevices(Set<LightDevice> devices) {
        this.devices = devices;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }

    @Override
    public String toString() {
        return "LightConfig{" +
                "brightnessRate=" + brightnessRate +
                ", temperatureRate=" + temperatureRate +
                ", check=" + check +
                ", checkInterval=" + checkInterval +
                ", refreshInterval=" + refreshInterval +
                ", devices=" + devices +
                '}';
    }
}
