package hse.marinosyan;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Chunk extends JButton {

    private BufferedImage image; // An image which is used as a background of this instance
    private int number; // A unique invariant number of this tile used to check winning condition and solvability

    public BufferedImage getImage() {
        return image;
    }

    int getNumer() {
        return number;
    }

    // Property used to tell whether this tile is emtpy
    boolean isEmpty() {
        return image == null;
    }

    // Constructor for chunks with images
    Chunk(BufferedImage image, int column, int row, int chunkWidth, int chunkHeight, int number) {
        super();
        this.image = image.getSubimage(column * chunkWidth, row * chunkHeight, chunkWidth, chunkHeight);
        this.number = number;
        setIcon(new ImageIcon(this.image));
        setBorder(BorderFactory.createEmptyBorder());
    }

    // Constructor for empty chunk
    Chunk(int chunkWidth, int chunkHeight, int number) {
        super();
        setSize(chunkWidth, chunkHeight);
        image = null;
        this.number = number;
        setBorder(BorderFactory.createEmptyBorder());
        setFont(new java.awt.Font("Arial", Font.ITALIC, 16));
    }
}
