package uk.co.caprica.vlcjplayer.light;

import com.mollin.yapi.YeelightDevice;
import com.mollin.yapi.enumeration.YeelightEffect;
import com.mollin.yapi.exception.YeelightResultErrorException;
import com.mollin.yapi.exception.YeelightSocketException;
import uk.co.caprica.vlcjplayer.view.main.MainFrame;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class YeelightManager {
    private LightConfig config;
    private Map<LightDevice, YeelightDevice> yeelightDevices;

    public YeelightManager(LightConfig config) {
        this.config = config;
    }

    public void setPower(Boolean power) {
        yeelightDevices.values().forEach(device -> this.setPower(device, power));
    }


    public boolean initDevices() {
        this.yeelightDevices = new HashMap<>();
        int badDevicesCount = 0;
        for (LightDevice device : config.getDevices()) {
            YeelightDevice yeelightDevice = initDevice(device, YeelightEffect.SMOOTH, Defaults.DURATION);
            if (yeelightDevice != null) {
                yeelightDevices.put(device, yeelightDevice);
            }else{
                badDevicesCount++;
            }
        }
        return badDevicesCount < config.getDevices().size();
    }

    private YeelightDevice initDevice(LightDevice device, YeelightEffect effect, Integer duration) {
        YeelightDevice yeelightDevice = null;
        try {
            yeelightDevice = new YeelightDevice(device.getIp(), device.getPort(), effect, duration);
        } catch (YeelightSocketException e) {
            JOptionPane.showMessageDialog(MainFrame.instance(),"Something wrong with your Yeelight device with ip " + device.getIp() + ", reason " + e.getMessage(), "Yeelight device error", JOptionPane.ERROR_MESSAGE);
        }
        return yeelightDevice;
    }

    public void setFeature(LightDevice lightDevice, Feature feature, Integer value) {
        YeelightDevice yeelightDevice = yeelightDevices.get(lightDevice);
        if (yeelightDevice == null) {
            return;
        }
        try {
            switch (feature) {
                case BRIGHTNESS:
                    yeelightDevice.setBrightness(value);
                    break;
                case TEMPERATURE:
                    yeelightDevice.setColorTemperature(getColorTemperature(value));
                    break;
                case COLOR:
                    int[] rgb = ImageHandler.getRgbArray(value);
                    yeelightDevice.setRGB(rgb[0], rgb[1], rgb[2]);
            }
        } catch (YeelightSocketException | YeelightResultErrorException e) {
        }
    }

    private int getColorTemperature(int percent){
        return Defaults.TEMPERATURE_MIN + ((Defaults.TEMPERATURE_MAX - Defaults.TEMPERATURE_MIN) / 100 * (100 - percent));
    }

    private void setPower(YeelightDevice device, Boolean power) {
        try {
            device.setPower(power);
        } catch (YeelightSocketException | YeelightResultErrorException e) {
        }
    }
}
