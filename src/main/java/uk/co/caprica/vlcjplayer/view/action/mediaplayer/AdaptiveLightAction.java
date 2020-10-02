package uk.co.caprica.vlcjplayer.view.action.mediaplayer;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcjplayer.event.LightDisabledEvent;
import uk.co.caprica.vlcjplayer.event.LightEnabledEvent;
import uk.co.caprica.vlcjplayer.light.*;
import uk.co.caprica.vlcjplayer.view.action.Resource;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static uk.co.caprica.vlcjplayer.Application.application;


public class AdaptiveLightAction extends MediaPlayerAction {
    private YeelightManager yeelightManager;
    private LightConfig config;
    private boolean isEnabled;
    private ScheduledExecutorService scheduler;
    private boolean cameraNotWorking = false;

    AdaptiveLightAction(Resource resource, MediaPlayer mediaPlayer) {
        super(resource, mediaPlayer);
        isEnabled = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isEnabled) {
            if (yeelightManager != null) {
                yeelightManager.setPower(false);
                if (scheduler != null) {
                    scheduler.shutdown();
                }
                application().post(LightDisabledEvent.INSTANCE);
            }
            isEnabled = false;
        } else if (mediaPlayer.isPlaying()) {
            if (config == null) {
                config = JsonParser.getLightConfig(null);
            }
            if (config != null) {
                if (yeelightManager == null) {
                    yeelightManager = new YeelightManager(config);
                    boolean successfulInit = yeelightManager.initDevices();
                    if (!successfulInit) {
                        yeelightManager = null;
                        return;
                    }
                }
                yeelightManager.setPower(true);

                scheduler = Executors.newScheduledThreadPool(2);
                scheduler.scheduleAtFixedRate(this::changeLight, 0, config.getRefreshInterval(), TimeUnit.MILLISECONDS);
                if (config.isCheck()) {
                    scheduler.scheduleAtFixedRate(this::checkRoom, 0, config.getCheckInterval(), TimeUnit.MINUTES);
                }
                application().post(LightEnabledEvent.INSTANCE);
                isEnabled = true;
            }
        }
    }


    private void checkRoom() {
        if (cameraNotWorking) {
            return;
        }
        BufferedImage cameraImage = CameraManager.getImage();
        if (cameraImage != null) {
            ImageHandler handler = new ImageHandler(cameraImage, null);
            double brightnessRate = 1 + (double) (handler.getValue(ScreenArea.WHOLE_SCREEN, Feature.BRIGHTNESS, false)) / 100;
            config.setBrightnessRate(brightnessRate);
        } else {
            cameraNotWorking = true;
        }
    }

    private void changeLight() {
        BufferedImage image = mediaPlayer.getSnapshot();
        if (!mediaPlayer.isPlaying() || image == null) {
            return;
        }
        ImageHandler handler = new ImageHandler(image, config);
        config.getDevices().forEach(lightDevice -> {
            if (lightDevice.getFeatures().contains(Feature.COLOR)) {
                setFeature(Feature.COLOR, 0, lightDevice, handler);
            } else {
                lightDevice.getFeatures()
                        .forEach(feature -> setFeature(feature, Defaults.CHANGE_VALUE_TRESHOLD, lightDevice, handler));
            }
        });
    }

    private void setFeature(Feature feature, Integer threshold, LightDevice lightDevice, ImageHandler handler) {
        Integer currentValue = lightDevice.getCurrentValueByFeature(feature);
        Integer newValue = handler.getValue(lightDevice.getScreenArea(), feature, true);
        if (Math.abs(currentValue - newValue) > threshold) {
            lightDevice.setCurrentValueByFeature(feature, newValue);
            yeelightManager.setFeature(lightDevice, feature, newValue);
        }
    }
}
