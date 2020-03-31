package csdev.couponstash.ui;

import csdev.couponstash.logic.Logic;
import javafx.fxml.FXML;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * The ui for the Tabs on the left half.
 */
public class TabsPanel extends UiPart<Region> {
    private static final String FXML = "TabsPanel.fxml";

    // Independent Ui parts residing in this Ui container
    private CouponListPanel couponListPanel;
    private SavedPane savedPane;
    private HelpPane helpPane;
    private Logic logic;

    @FXML
    private Tab couponTab;

    @FXML
    private Tab savedTab;

    @FXML
    private Tab helpTab;

    @FXML
    private StackPane couponListPanelPlaceholder;

    @FXML
    private StackPane expandedCouponPlaceholder;

    @FXML
    private StackPane savedPanePlaceholder;

    @FXML
    private StackPane helpPanePlaceholder;

    @FXML
    private TabPane tabPane;

    public TabsPanel(Logic logic) {
        super(FXML);
        this.logic = logic;
    }

    /**
     * Fills up all the placeholders of this window.
     */
    public void fillInnerParts() {
        couponListPanel = new CouponListPanel(
                logic.getFilteredCouponList(), logic.getStashSettings().getMoneySymbol());
        couponListPanelPlaceholder.getChildren().add(couponListPanel.getRoot());

        savedPane = new SavedPane(logic);
        savedPanePlaceholder.getChildren().add(savedPane.getRoot());

        helpPane = new HelpPane(logic);
        helpPanePlaceholder.getChildren().add(helpPane.getRoot());
    }

    /**
     * Returns the enum of the current selected tab.
     */
    public CsTab selectedTab() {
        if (couponTab.isSelected()) {
            return CsTab.COUPONS;
        } else if (savedTab.isSelected()) {
            return CsTab.SAVED;
        } else {
            return CsTab.HELP;
        }
    }

    /**
     * Selects {@code tab} to view.
     */
    public void selectTab(CsTab tab) {
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        switch (tab) {

        case COUPONS:
            selectionModel.select(couponTab);
            break;

        case SAVED:
            selectionModel.select(savedTab);
            break;

        case HELP:
            selectionModel.select(helpTab);
            break;

        default:
            break;
        }
    }
}
