package csdev.couponstash.ui;

import static java.util.Objects.requireNonNull;

import csdev.couponstash.logic.Logic;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Combined Calendar and ResultDisplay displayed at the right.
 */
public class CalendarResultDisplayPane extends UiPart<Region> {
    private static final String FXML = "CalendarResultDisplayPane.fxml";

    // Independent Ui parts residing in this Ui container
    private CalendarPane calendarPane;
    private ResultDisplay resultDisplay;
    private Logic logic;

    @FXML
    private StackPane calendarPanePlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    public CalendarResultDisplayPane(Logic logic) {
        super(FXML);
        this.logic = logic;
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        calendarPane = new CalendarPane(logic);
        calendarPanePlaceholder.getChildren().add(calendarPane.getRoot());
        calendarPanePlaceholder.setAlignment(Pos.TOP_RIGHT);

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());
    }

    /**
     * Sets the result display to display feedback to user.
     *
     * @param feedbackToUser Feedback to user of {@String}.
     */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setFeedbackToUser(feedbackToUser);
    }
}
