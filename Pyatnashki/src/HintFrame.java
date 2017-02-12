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
        this.parent = parent;
        setSize(width, height);
        setLocation(parent.getLocation());
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JLabel hintLabel = new JLabel();
        hintLabel.setIcon(new ImageIcon(sourceImage));
        getContentPane().add(hintLabel);

        JButton okBtn = new JButton("OK, now I'm ready to win!");
        okBtn.setSize(width/4, 20);
        getContentPane().add(okBtn, BorderLayout.SOUTH);
        pack();

        okBtn.addActionListener(event -> {
            parent.setVisible(true);
            this.setVisible(false);
        });
    }
}
