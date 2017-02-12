import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Chunk extends JButton {

    private BufferedImage image;

    public BufferedImage getImage() {
        return image;
    }

    Chunk(BufferedImage image) {
        super();
        this.image = image;
    }

    public void setChunk(int index, int chunkWidth, int chunkHeight) {
        drawChunk(index, chunkWidth, chunkHeight);
        setIcon(imageToIcon(image, chunkWidth, chunkHeight));
    }

    private void drawChunk(int index, int chunkWidth, int chunkHeight) {
        Graphics2D gr = image.createGraphics();
        gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * index, chunkHeight * index,
                chunkWidth * index + chunkWidth, chunkHeight * index + chunkHeight, null);
        gr.dispose();
    }

    private ImageIcon imageToIcon(Image image, int width, int height) {
        return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
}
