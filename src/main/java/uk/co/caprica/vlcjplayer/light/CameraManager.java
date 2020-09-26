package uk.co.caprica.vlcjplayer.light;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import uk.co.caprica.vlcjplayer.view.main.MainFrame;

import javax.swing.*;
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
            JOptionPane.showMessageDialog(MainFrame.instance(),"Something wrong with your webcam, message: " + e.getMessage(), "Webcam error", JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }
}
