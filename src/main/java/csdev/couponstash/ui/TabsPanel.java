package csdev.couponstash.ui;

import java.util.logging.Logger;

import csdev.couponstash.commons.core.LogsCenter;
import csdev.couponstash.commons.moneysymbol.MoneySymbol;
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
    private static final Logger logger = LogsCenter.getLogger(TabsPanel.class);
    private static final String FXML = "TabsPanel.fxml";

    // Independent Ui parts residing in this Ui container
    private CouponListPanel couponListPanel;
    private SummaryPane summaryPane;
    private HelpPane helpPane;
    private Logic logic;

    @FXML
    private Tab couponTab;

    @FXML
    private Tab summaryTab;

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
        tabPane.getSelectionModel().selectedItemProperty()
                .addListener((ov, oldTab, newTab) -> {
                    logger.info("Switching to " + newTab.getText() + " tab!");
                    if (newTab == summaryTab) {
                        summaryPane.updateView();
                    }
                });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    public void fillInnerParts() {
        MoneySymbol currentMoneySymbol = logic.getStashSettings().getMoneySymbol();

        couponListPanel = new CouponListPanel(
                logic.getFilteredCouponList(), currentMoneySymbol);
        couponListPanelPlaceholder.getChildren().add(couponListPanel.getRoot());

        summaryPane = new SummaryPane(logic.getAllCouponList(), currentMoneySymbol);
        savedPanePlaceholder.getChildren().add(summaryPane.getRoot());

        helpPane = new HelpPane();
        helpPanePlaceholder.getChildren().add(helpPane.getRoot());
    }

    /**
     * Returns the enum of the current selected tab.
     */
    public CsTab selectedTab() {
        if (couponTab.isSelected()) {
            return CsTab.COUPONS;
        } else if (summaryTab.isSelected()) {
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
            selectionModel.select(summaryTab);
            logger.info("Switched to summary tab!");

            break;

        case HELP:
            selectionModel.select(helpTab);
            break;

        default:
            break;
        }
    }
}
