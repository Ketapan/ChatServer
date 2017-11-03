package Prozess;

import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import java.awt.*;

public class PicturePanel extends JPanel {
    private Image image;

    PicturePanel(Image image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }
}

