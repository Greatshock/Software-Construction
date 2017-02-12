import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class GameFrame extends JFrame {

    private static final int ROWS = 4;
    private static final int COLUMNS = 4;
    private static final int CHUNKS = 16;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 640;
    private static final int GAME_FIELD_MEASURE = 600;

    private static final String DESCRIPTION = "The app is a kind of sliding puzzle mini game.\nSimply upload " +
            "an image via" + " [File -> Open] and start playing!\n" + "Designed by: Nikita Marinosyan\n" +
            "Higher School of Economics, Faculty of Computer Science\n" + "2017";

    private final JFileChooser fc = new JFileChooser();
    private BufferedImage sourceImage = null;
    private int hintsUsed = 0;
    private int movesMade = 0;
    private Chunk[] chunks = new Chunk[CHUNKS];

    public GameFrame() {

        onPaint();
        createMenu();
    }

    private void onPaint() {

        /*---------------------------------- Set initial values to the frame's properties ----------------------------*/

        // Set general properties
        setTitle("Sliding Puzzle Game");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        getContentPane().setBackground(new Color(242, 242, 242));
        getContentPane().setLayout(new GridLayout(4, 4, 3, 3));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));

        pack();
    }

    private void createMenu() {
        /*-------------------------------------- Creating a menu structure -------------------------------------------*/

        // Add menu bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Add menus
        JMenu file = new JMenu("File");
        menuBar.add(file);

        JMenu hint = new JMenu("Hint");
        menuBar.add(hint);

        JMenu help = new JMenu("Help");
        menuBar.add(help);

        // Add menu items
        JMenuItem open = new JMenuItem("Open");
        file.add(open);
        JMenuItem shuffle = new JMenuItem("Shuffle");
        file.add(shuffle);
        shuffle.setEnabled(false);

        JMenuItem showSrcImg = new JMenuItem("Show source image");
        hint.add(showSrcImg);
        showSrcImg.setEnabled(false);

        JMenuItem about = new JMenuItem("About");
        help.add(about);

        pack();
        /*-------------------------------------- Handling clicks on menu items ---------------------------------------*/

        // Handle the event of pressing About menu item
        ImageIcon infoIcon = new ImageIcon("res/infoIcon.png");
        about.addActionListener(event ->
                JOptionPane.showMessageDialog(about, GameFrame.DESCRIPTION, "About the app",
                        JOptionPane.QUESTION_MESSAGE, infoIcon)
        );

        // Handle the event of pressing Open menu item
        open.addActionListener(event -> {

            // Allow user to choose only images
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(new FileNameExtensionFilter("Image files",
                    "bmp",
                    "jpg",
                    "jpeg",
                    "png",
                    "gif",
                    "tiff")
            );

            // Check if the file was chosen successfully
            int returnVal = fc.showOpenDialog(open);
            if (returnVal == JFileChooser.APPROVE_OPTION) { // All good

                // Get new image and split it into chunks
                sourceImage = getResizedImageFromFile(fc.getSelectedFile(), GAME_FIELD_MEASURE, GAME_FIELD_MEASURE);
                splitIntoChunks(sourceImage);
                movesMade = 0;
                hintsUsed = 0;
                String info = "Moves made: " + String.valueOf(movesMade) +
                        "\nHints used: " + String.valueOf(hintsUsed);
                chunks[CHUNKS - 1].setText("<html>" + info.replaceAll("\\n", "<br>") + "</html>");

                // Make new options available
                showSrcImg.setEnabled(true);
                shuffle.setEnabled(true);

                // Shuffle chunks and display them
                shuffleChunks();
                displayChunks();
            }
            else if (returnVal == JFileChooser.ERROR_OPTION) { // Something went wrong
                JOptionPane.showMessageDialog(fc, "Unable to open selected file!",
                        "Error!", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Handle event of using hint
        showSrcImg.addActionListener(event -> {
            HintFrame hintFrame = new HintFrame(this, this.getWidth(), this.getHeight(), sourceImage,
                    "Close the hint by pressing OK to continue!");
            String info = "Moves made: " + String.valueOf(movesMade) +
                    "\nHints used: " + String.valueOf(++hintsUsed);
            chunks[indexOfEmptyChunk()].setText("<html>" + info.replaceAll("\\n", "<br>") + "</html>");
            hintFrame.setVisible(true);
            this.setVisible(false);
        });

        // Handle event of pressing shuffle button
        shuffle.addActionListener(event -> {
            shuffleChunks();
            displayChunks();
        });
    }

    private void splitIntoChunks(BufferedImage image) {

        // Determine the chunk width and height
        int chunkWidth = image.getWidth() / COLUMNS;
        int chunkHeight = image.getHeight() / ROWS;

        int count = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {

                // Instantiate a new chunk
                chunks[count] = new Chunk(image, column, row, chunkWidth, chunkHeight);

                // Add action listener to enable swapping
                chunks[count++].addActionListener(e -> {

                    // Get the index of the button which was pressed
                    int pressedIndex = Arrays.asList(chunks).indexOf(e.getSource());

                    // Find the empty button index
                    int emptyIndex = indexOfEmptyChunk();

                    // Check if the pressed chunk is near empty chunk
                    if ((pressedIndex - 1 == emptyIndex) || (pressedIndex + 1 == emptyIndex) ||
                            (pressedIndex - ROWS == emptyIndex) || (pressedIndex + COLUMNS == emptyIndex)){ // It is near

                        // Swap chunks
                        Chunk tmp = chunks[pressedIndex];
                        chunks[pressedIndex] = chunks[emptyIndex];
                        chunks[emptyIndex] = tmp;
                        String info = "Moves made: " + String.valueOf(++movesMade) +
                        "\nHints used: " + String.valueOf(hintsUsed);
                        chunks[pressedIndex].setText("<html>" + info.replaceAll("\\n", "<br>") + "</html>");
                    }

                    // Repaint the game field
                    displayChunks();
                });
            }
        }

        // Create empty chunk
        chunks[CHUNKS - 1] = new Chunk(chunkWidth, chunkHeight);
    }

    private void shuffleChunks() {

        // Shuffle
        Collections.shuffle(Arrays.asList(chunks).subList(0, 15));

        // Set default values to the empty chunk
        movesMade = 0;
        hintsUsed = 0;
        String info = "Moves made: " + String.valueOf(movesMade) +
                "\nHints used: " + String.valueOf(hintsUsed);
        chunks[indexOfEmptyChunk()].setText("<html>" + info.replaceAll("\\n", "<br>") + "</html>");
    }

    private void displayChunks() {

        getContentPane().removeAll();
        getContentPane().revalidate();
        getContentPane().repaint();

        for (int count = 0; count < 16; count++) {
            getContentPane().add(chunks[count]);
        }
    }

    private int indexOfEmptyChunk() {

        for (int i = 0; i < CHUNKS; i++) {
            if (chunks[i].isEmpty()) return i;
        }

        return -1;
    }

    private BufferedImage getResizedImageFromFile(File file, int width, int height) {
        BufferedImage resizedImage = null;
        try {
            BufferedImage image = ImageIO.read(file);
            resizedImage = new BufferedImage(width, height, image.getType());
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            g.dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(fc, "Unable to upload the image!",
                    "Error!", JOptionPane.ERROR_MESSAGE);
        }

        return resizedImage;
    }
}
