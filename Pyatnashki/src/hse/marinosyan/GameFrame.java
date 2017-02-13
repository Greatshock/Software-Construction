package hse.marinosyan;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowEvent;
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

    /**
     * Method used to set default values to the frame's properies
     */
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

    /**
     * Method used to create a menu and it's features
     */
    private void createMenu() {
        /*-------------------------------------- Creating a menu structure -------------------------------------------*/

        // Add menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        setJMenuBar(menuBar);

        // Add menus
        JMenu file = new JMenu("File");
        menuBar.add(file);

        JMenu hint = new JMenu("Hint");
        menuBar.add(hint);

        JMenu help = new JMenu("Help");
        menuBar.add(help);

        JMenu settings = new JMenu("Settings");
        menuBar.add(settings);

        // Add menu items
        JMenuItem open = new JMenuItem("Open");
        file.add(open);
        JMenuItem shuffle = new JMenuItem("Shuffle");
        file.add(shuffle);
        shuffle.setEnabled(false);
        JMenuItem exit = new JMenuItem("Exit");
        file.add(exit);

        JMenuItem showSrcImg = new JMenuItem("Show source image");
        hint.add(showSrcImg);
        showSrcImg.setEnabled(false);

        JMenuItem about = new JMenuItem("About");
        help.add(about);

        JMenu backgroundColor = new JMenu("Background color");
        settings.add(backgroundColor);
        JMenuItem blue = new JMenuItem("Blue");
        backgroundColor.add(blue);
        JMenuItem cyan = new JMenuItem("Cyan");
        backgroundColor.add(cyan);
        JMenuItem gray = new JMenuItem("Gray");
        backgroundColor.add(gray);
        JMenuItem green = new JMenuItem("Green");
        backgroundColor.add(green);
        JMenuItem magenta = new JMenuItem("Magenta");
        backgroundColor.add(magenta);
        JMenuItem orange = new JMenuItem("Orange");
        backgroundColor.add(orange);
        JMenuItem yellow = new JMenuItem("Yellow");
        backgroundColor.add(yellow);

        pack();
        /*-------------------------------------- Handling clicks on menu items ---------------------------------------*/

        // Handle the event of pressing About menu item
        ImageIcon infoIcon = new ImageIcon("res/infoIcon.png");
        about.addActionListener(e ->
                JOptionPane.showMessageDialog(about, GameFrame.DESCRIPTION, "About the app",
                        JOptionPane.QUESTION_MESSAGE, infoIcon)
        );

        // Handle the event of pressing Open menu item
        open.addActionListener(e -> {

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

                // Shuffle chunks and check the solving possibility
                shuffleChunks();

                // Display chunks
                repaintChunks();
            }
            else if (returnVal == JFileChooser.ERROR_OPTION) { // Something went wrong
                JOptionPane.showMessageDialog(fc, "Unable to open selected file!",
                        "Error!", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Handle event of file->exit
        exit.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        // Handle event of using hint
        showSrcImg.addActionListener(e -> {
            HintFrame hintFrame = new HintFrame(this, this.getWidth(), this.getHeight(), sourceImage,
                    "Close the hint by pressing OK to continue!");
            String info = "Moves made: " + String.valueOf(movesMade) +
                    "\nHints used: " + String.valueOf(++hintsUsed);
            chunks[indexOfEmptyChunk()].setText("<html>" + info.replaceAll("\\n", "<br>") + "</html>");
            hintFrame.setVisible(true);
            this.setVisible(false);
        });

        // Handle event of pressing shuffle button
        shuffle.addActionListener(e -> {
            shuffleChunks();
            repaintChunks();
        });

        // Handle events of setting background color
        yellow.addActionListener(e -> getContentPane().setBackground(Color.yellow));
        orange.addActionListener(e -> getContentPane().setBackground(Color.orange));
        green.addActionListener(e -> getContentPane().setBackground(Color.green));
        magenta.addActionListener(e -> getContentPane().setBackground(Color.magenta));
        cyan.addActionListener(e -> getContentPane().setBackground(Color.cyan));
        blue.addActionListener(e -> getContentPane().setBackground(Color.blue));
        gray.addActionListener(e -> getContentPane().setBackground(new Color(242, 242, 242)));
    }

    /**
     * Method used to upload user's image and split it into chunks
     * @param image - image uploaded by user
     */
    private void splitIntoChunks(BufferedImage image) {

        // Determine the chunk width and height
        int chunkWidth = image.getWidth() / COLUMNS;
        int chunkHeight = image.getHeight() / ROWS;

        int count = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {

                // Instantiate a new chunk
                chunks[count] = new Chunk(image, column, row, chunkWidth, chunkHeight, count);

                // Add action listener to enable swapping
                chunks[count++].addActionListener(e -> {

                    // Check if the player has already won
                    // and we don't need to do anything
                    if (isWon()) return;

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

                        String info;
                        if (isWon()) {
                            info = "YOU WIN!!!" +
                                    "\nMoves made: " + String.valueOf(++movesMade);
                            chunks[pressedIndex].setText("<html>" + info.replaceAll("\\n", "<br>") + "</html>");
                        }
                        else {
                            info = "Moves made: " + String.valueOf(++movesMade) +
                                    "\nHints used: " + String.valueOf(hintsUsed);
                            chunks[pressedIndex].setText("<html>" + info.replaceAll("\\n", "<br>") + "</html>");
                        }
                    }

                    // Repaint the game field
                    repaintChunks();
                });
            }
        }

        // Create empty chunk
        chunks[CHUNKS - 1] = new Chunk(chunkWidth, chunkHeight, CHUNKS - 1);
    }

    /**
     * Method to shuffle chunks and create new game
     * which has a solution
     */
    private void shuffleChunks() {

        // Shuffle
        do {
            Collections.shuffle(Arrays.asList(chunks).subList(0, 15));
        }
        while (!isSolvable());

        // Set default values to the empty chunk
        movesMade = 0;
        hintsUsed = 0;
        String info = "Moves made: " + String.valueOf(movesMade) +
                "\nHints used: " + String.valueOf(hintsUsed);
        chunks[indexOfEmptyChunk()].setText("<html>" + info.replaceAll("\\n", "<br>") + "</html>");
    }

    /**
     * Method to check if this combination
     * of chunks can be solved or not
     * @return true - if the combination has a solution, false - otherwise
     */
    private boolean isSolvable() {
        int parity = 0;

        for (int i = 0; i < CHUNKS; i++)
        {
            for (int j = i + 1; j < CHUNKS; j++)
            {
                if (chunks[i].getNumer() > chunks[j].getNumer())
                {
                    parity++;
                }
            }
        }

        return parity % 2 == 0;
    }

    /**
     * Method to check if the player has
     * completed the puzzle
     * @return true - if the player has completed the puzzle, false - otherwise
     */
    private boolean isWon() {
        for (int i = 0; i < CHUNKS - 1; i++) {
            if (chunks[i].getNumer() != i) return false;
        }

        return true;
    }

    /**
     * Method used to update the game frame and
     * depict all the changes happened to it
     */
    private void repaintChunks() {

        getContentPane().removeAll();
        getContentPane().revalidate();
        getContentPane().repaint();

        for (int count = 0; count < 16; count++) {
            getContentPane().add(chunks[count]);
        }
    }

    /**
     * Method to search the index of the empty chunk
     * @return index of the empty chunk
     */
    private int indexOfEmptyChunk() {

        for (int i = 0; i < CHUNKS; i++) {
            if (chunks[i].isEmpty()) return i;
        }

        return -1;
    }

    /**
     * Parse the image file and sets it to the defined size
     * @param file - file with the image
     * @param width - required width
     * @param height - require height
     * @return resized image with defined width and length
     */
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
