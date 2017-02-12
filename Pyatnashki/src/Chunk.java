import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Chunk extends JButton {

    private BufferedImage image;

    public BufferedImage getImage() {
        return image;
    }

    public boolean isEmpty() {
        return image == null;
    }

    public Chunk(BufferedImage image, int column, int row, int chunkWidth, int chunkHeight) {
        super();
        this.image = image.getSubimage(column * chunkWidth, row * chunkHeight, chunkWidth, chunkHeight);
        setIcon(new ImageIcon(this.image));
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder());
    }

    public Chunk(int chunkWidth, int chunkHeight) {
        super();
        setSize(chunkWidth, chunkHeight);
        image = null;
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder());
        setFont(new java.awt.Font("Arial", Font.ITALIC, 16));
    }
}
