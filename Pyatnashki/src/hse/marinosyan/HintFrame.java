package hse.marinosyan;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HintFrame extends JFrame {

    private JFrame parent;

    public JFrame getParent() {
        return parent;
    }

    HintFrame(JFrame parent, int width, int height, BufferedImage sourceImage, String title) {
        super(title);

        // Get reference to the parent frame
        this.parent = parent;

        //Set default properties
        setSize(width, height);
        setLocation(parent.getLocation());
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Add label which will be containing a source image
        JLabel hintLabel = new JLabel();
        hintLabel.setIcon(new ImageIcon(sourceImage));
        getContentPane().add(hintLabel);

        // Add the button to close the hint
        JButton okBtn = new JButton("OK, now I'm ready to win!");
        okBtn.setSize(width/4, 20);
        getContentPane().add(okBtn, BorderLayout.SOUTH);
        pack();

        // Handle the event of closing the hint
        okBtn.addActionListener(event -> {
            parent.setVisible(true);
            this.setVisible(false);
        });
    }
}
