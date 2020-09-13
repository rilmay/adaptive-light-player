package uk.co.caprica.vlcjplayer.light;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;

import java.awt.image.BufferedImage;

public class CameraManager {
    public static BufferedImage getImage() {
        BufferedImage result = null;
        try {
            Webcam webcam = Webcam.getDefault();
            webcam.open();
            result = webcam.getImage();
            webcam.close();
        } catch (WebcamException e) {
            System.out.println("Something wrong with your webcam");
        }
        return result;
    }
}
