import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;


public class GameFrame extends JFrame {

    private final JFileChooser fc = new JFileChooser();
    private Chunk[] chunks = new Chunk[CHUNKS];
    private BufferedImage sourceImage;
    private JLabel defaultImageLabel = new JLabel();

    private static final String DESCRIPTION = "The app is a kind of sliding puzzle mini game.\nSimply upload " +
            "an image via" + " [File -> Open] and start playing!\n" + "Designed by: Nikita Marinosyan\n" +
            "Higher School of Economics, Faculty of Computer Science\n" + "2017";

    private static final int ROWS = 4;
    private static final int COLUMNS = 4;
    private static final int CHUNKS = 16;

    public GameFrame() {

        onPaint();
        createMenu();
    }

    private void onPaint() {

        /*---------------------------------- Set initial values to the frame's properties ----------------------------*/

        // Set general properties
        setTitle("Sliding Puzzle Game");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 640);
        setResizable(false);
        getContentPane().setBackground(Color.ORANGE);

        // Disable auto0positioning of the components
        setLayout(null);

        /*---------------------------------- Adding required components to the frame ---------------------------------*/
        defaultImageLabel.setSize(540, 540);
        sourceImage = getResizedImageFromFile(new File("res/nova.jpg"), defaultImageLabel.getWidth(),
                defaultImageLabel.getHeight());
        defaultImageLabel.setIcon(new ImageIcon(sourceImage));
        getContentPane().add(defaultImageLabel).setLocation(30, 26);
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

        JMenuItem showSrcImg = new JMenuItem("Show source image");
        hint.add(showSrcImg);

        JMenuItem about = new JMenuItem("About");
        help.add(about);

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

                sourceImage = getResizedImageFromFile(fc.getSelectedFile(), 800, 580);
                splitIntoChunks(sourceImage);
                defaultImageLabel.setVisible(false);
            }
            else if (returnVal == JFileChooser.ERROR_OPTION) { // Something went wrong
                JOptionPane.showMessageDialog(fc, "Unable to open selected file!",
                        "Error!", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void splitIntoChunks(BufferedImage image) {

        // Determine the chunk width and height
        int chunkWidth = image.getWidth() / COLUMNS - 4;
        int chunkHeight = image.getHeight() / ROWS - 4;

        int count = 0;
        for (int i = 0; i < CHUNKS - 1; i++) {

            // Instantiate a new chunk
            chunks[count] = new Chunk(new BufferedImage(chunkWidth, chunkHeight, image.getType()));
            chunks[count].setSize(chunkWidth, chunkHeight);

            // Set the image to the chunk
            chunks[count++].setChunk(i, chunkWidth, chunkHeight);
        }

        //TODO draw empty chunk -> chunks[count+1] = new Chunk()
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

    private void displayChunks(BufferedImage[] chunks) {
        Random rnd = new Random();
    }
}
