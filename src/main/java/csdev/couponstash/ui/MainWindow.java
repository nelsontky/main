package csdev.couponstash.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import csdev.couponstash.commons.core.GuiSettings;
import csdev.couponstash.commons.core.LogsCenter;
import csdev.couponstash.logic.Logic;
import csdev.couponstash.logic.commands.CommandResult;
import csdev.couponstash.logic.commands.HelpCommand;
import csdev.couponstash.logic.commands.ShareCommand;
import csdev.couponstash.logic.commands.exceptions.CommandException;
import csdev.couponstash.logic.parser.exceptions.ParseException;
import csdev.couponstash.model.coupon.Coupon;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private CalendarResultDisplayPane calendarResultPane;
    private TabsPanel tabPanel;
    private CouponWindow expandedCouponWindow;
    private CommandBox commandBox;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private StackPane calendarPanePlaceholder;

    @FXML
    private StackPane calendarResultPlaceholder;

    @FXML
    private StackPane tabPanePlaceholder;

    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        tabPanel = new TabsPanel(logic);
        tabPanePlaceholder.getChildren().add(tabPanel.getRoot());
        tabPanel.fillInnerParts();

        commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        calendarResultPane = new CalendarResultDisplayPane(logic);
        calendarResultPlaceholder.getChildren().add(calendarResultPane.getRoot());
        calendarResultPane.fillInnerParts();
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        primaryStage.hide();
    }

    /**
     * Executes the command and returns the result.
     *
     * @see Logic#execute(String, CsTab)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException, IOException {

        try {
            CsTab currentSelectedTab = tabPanel.selectedTab();
            CommandResult commandResult = logic.execute(commandText, currentSelectedTab);

            // commands that can be executed will automatically switch tab to Coupons if it is not the current tab
            if (!currentSelectedTab.equals(CsTab.COUPONS)) {
                tabPanel.selectTab(CsTab.COUPONS);
            }

            logger.info("Result: " + commandResult.getFeedbackToUser());
            calendarResultPane.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isExit()) {
                handleExit();
            }

            // command involves expanding a coupon
            if (commandResult.getCouponToExpand().isPresent()) {
                handleExpand(commandResult.getCouponToExpand().get());
            }

            // command involves sharing a coupon
            if (commandResult.getCouponToShare().isPresent()) {
                String filePath = handleShare(commandResult.getCouponToShare().get());
                calendarResultPane.setFeedbackToUser(
                        String.format(ShareCommand.MESSAGE_SHARE_COUPON_SUCCESS, filePath)
                );
            }

            // Command involves opening help page
            if (commandResult.isHelp()) {
                handleHelp();
                calendarResultPane.setFeedbackToUser(HelpCommand.BROWSER_OPEN_SUCCESS);
            }

            return commandResult;
        } catch (CommandException | ParseException | IOException e) {
            logger.info("Invalid command: " + commandText);
            calendarResultPane.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    /**
     * Expand a coupon in a new window. If another coupon was already expanded,
     * close that old window and open a new one.
     * @param couponToExpand coupon to open a new window for
     */
    public void handleExpand(Coupon couponToExpand) {
        if (expandedCouponWindow != null) {
            expandedCouponWindow.close();
        }
        expandedCouponWindow = new CouponWindow(
                couponToExpand,
                logic.getStashSettings().getMoneySymbol().toString()
        );
        expandedCouponWindow.show();
    }

    /**
     * Save coupon as an image.
     * @param couponToShare Coupon to be saved as an image
     * @return The path where the image was saved
     * @throws IOException When user closes save dialog or an error occurred when writing the image
     */
    public String handleShare(Coupon couponToShare) throws IOException {
        // Create new CouponCard and get the Region
        Region couponRegion = new CouponCard(
                couponToShare,
                1,
                logic.getStashSettings().getMoneySymbol().toString()
        ).getRoot();

        // Need to create a scene for the Region so CSS would work.
        Scene scene = new Scene(couponRegion);
        // Create a snapshot of the CouponRegion.
        WritableImage image = couponRegion.snapshot(new SnapshotParameters(), null);
        // Hacky way to ensure scene is freed from memory.
        scene = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(couponToShare.getName().toString() + "." + ShareCommand.FORMAT);
        File file = fileChooser.showSaveDialog(primaryStage);

        // If save dialog is closed without choosing a directory.
        if (file == null) {
            throw new IOException(ShareCommand.MESSAGE_DIALOG_CLOSED);
        }

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), ShareCommand.FORMAT, file);
        } catch (IOException e) {
            throw new IOException(ShareCommand.MESSAGE_WRITE_TO_IMAGE_ERROR);
        }

        return file.getAbsolutePath();
    }

    /**
     * Handle the opening of the help page in the system browser.
     * @throws IOException When a writing error occurs or if OS does not support this functionality
     */
    public void handleHelp() throws IOException {
        openBrowser(getHelpHtmlPath());
    }

    /**
     * Get URI of help.html. If help.html is not extracted from jar file yet,
     * extract it.
     * @return URI of help.html
     * @throws CommandException
     */
    private static URI getHelpHtmlPath() throws IOException {
        try {
            File file = new File(HelpCommand.HTML_NAME);
            if (file.exists()) {
                return file.toURI();
            }

            // If help.html is not extracted yet, extract HTML from jar file
            URI jarPath = MainWindow.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            JarFile jar = new JarFile(jarPath.getPath());

            InputStream inputStream = jar.getInputStream(new JarEntry(HelpCommand.HTML_NAME));
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            // Buffered writing for better performance
            byte[] buffer = new byte[4096];
            for (int bytesRead = 0; bytesRead != -1; bytesRead = inputStream.read(buffer)) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.close();
            inputStream.close();

            return file.toURI();
        } catch (URISyntaxException e) {
            throw new IOException(HelpCommand.UNSUPPORTED_OS);
        } catch (IOException e) {
            throw new IOException(HelpCommand.ERROR);
        }
    }

    /**
     * Open path in system browser.
     *
     * @param path File path to open in browser
     * @throws IOException
     */
    private static void openBrowser(URI path) throws IOException {
        assert path != null;

        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("linux") || os.contains(("unix"))) {
            Runtime runtime = Runtime.getRuntime();

            try {
                if (runtime.exec("which xdg-open").getInputStream().read() != -1) {
                    runtime.exec("xdg-open " + path.toString());
                } else {
                    throw new IOException(HelpCommand.UNSUPPORTED_OS);
                }
            } catch (IOException e) {
                throw new IOException(HelpCommand.ERROR);
            }
        } else if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(path);
            } catch (IOException e) {
                throw new IOException(HelpCommand.ERROR);
            }
        } else {
            throw new IOException(HelpCommand.UNSUPPORTED_OS);
        }
    }
}
