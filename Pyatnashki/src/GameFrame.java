import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;


public class GameFrame extends JFrame {

    private static final String DESCRIPTION = "The app is a kind of barley-break mini game.\nSimply upload " +
            "an image via" + " File -> Open and start playing!\n" + "Designed by: Nikita Marinosyan\n" +
            "Higher School of Economics, Faculty of Computer Science\n" + "2017";

    private final JFileChooser fc = new JFileChooser();

    public GameFrame() {
        /*----------------------------- Setting initial values to the frame's properties -----------------------------*/

        setTitle("Barley-break Game");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);

        /*---------------------------------- Adding required components to the frame ---------------------------------*/

        JRootPane rootPane = new JRootPane();
        setRootPane(rootPane);

        ImagePanel imagePanel = null;
        try {
            imagePanel = new ImagePanel(ImageIO.read(new File("res/fish.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootPane.setContentPane(imagePanel);

        /*-------------------------------------- Creating a menu structure -------------------------------------------*/

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu file = new JMenu("File");
        menuBar.add(file);

        JMenuItem open = new JMenuItem("Open");
        file.add(open);

        JMenu help = new JMenu("Help");
        menuBar.add(help);

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
                    "wbmp",
                    "png",
                    "gif",
                    "tiff")
            );

            // Check if the file was chosen successfully
            int returnVal = fc.showOpenDialog(open);
            if (returnVal == JFileChooser.APPROVE_OPTION) { // All good
                File f = fc.getSelectedFile();
            }
            else if (returnVal == JFileChooser.ERROR_OPTION) { // Something went wrong
                JOptionPane.showMessageDialog(fc, "Unable to open selected file!",
                        "Error!", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Make the frame g̶r̶e̶a̶t̶ ̶a̶g̶a̶i̶n̶ show up
        setVisible(true);
    }
}
