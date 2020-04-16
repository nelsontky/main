package csdev.couponstash.ui;

import java.io.IOException;
import java.util.logging.Logger;

import csdev.couponstash.commons.core.LogsCenter;
import csdev.couponstash.logic.Logic;
import csdev.couponstash.logic.commands.CommandResult;
import csdev.couponstash.logic.commands.exceptions.CommandException;
import csdev.couponstash.logic.parser.exceptions.ParseException;
import csdev.couponstash.model.history.CommandTextHistory;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {
    public static final String ERROR_STYLE_CLASS = "error";

    private static final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private CommandTextHistory commandTextHistory;

    @FXML
    private TextField commandTextField;

    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        commandTextHistory = new CommandTextHistory();

        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());

        // If key pressed is UP or DOWN, grab from commandTextHistory the corresponding
        // commandText
        commandTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                String retrievedCommand = commandTextHistory.getUp();
                commandTextField.setText(retrievedCommand);
                logger.info(
                        String.format(
                                "UP arrow key pressed. Previous command text \"%s\" retrived.",
                                retrievedCommand
                        )
                );
            } else if (event.getCode() == KeyCode.DOWN) {
                String retrievedCommand = commandTextHistory.getDown();
                commandTextField.setText(retrievedCommand);
                logger.info(
                        String.format(
                                "DOWN arrow key pressed. Next command text \"%s\" retrived.",
                                retrievedCommand
                        )
                );
            }
        });
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        try {
            commandTextHistory.add(commandTextField.getText()); // Add commandText to history

            logger.info(
                    String.format(
                            "Command text \"%s\" added to command text history!",
                            commandTextField.getText()
                    )
            );

            commandExecutor.execute(commandTextField.getText());
            commandTextField.setText("");
        } catch (CommandException | ParseException | IOException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException, IOException;
    }

}
