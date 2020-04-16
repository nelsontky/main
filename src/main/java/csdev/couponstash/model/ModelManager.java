package csdev.couponstash.model;

import static csdev.couponstash.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Logger;

import csdev.couponstash.commons.core.GuiSettings;
import csdev.couponstash.commons.core.LogsCenter;
import csdev.couponstash.commons.core.StashSettings;
import csdev.couponstash.model.coupon.Coupon;

import csdev.couponstash.model.element.ObservableMonthView;
import csdev.couponstash.model.history.HistoryManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

/**
 * Represents the in-memory model of the CouponStash data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final CouponStash couponStash;
    private final UserPrefs userPrefs;
    private final FilteredList<Coupon> filteredCoupons;
    private final ObservableMonthView monthView;
    private final SortedList<Coupon> sortedCoupons;
    private HistoryManager history;

    /**
     * Initializes a ModelManager with the given couponStash and userPrefs.
     */
    public ModelManager(ReadOnlyCouponStash couponStash, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(couponStash, userPrefs);

        logger.fine("Initializing with CouponStash: " + couponStash + " and user prefs " + userPrefs);

        this.couponStash = new CouponStash(couponStash).archiveExpiredCoupons();
        this.userPrefs = new UserPrefs(userPrefs);

        sortedCoupons = new SortedList<>(this.couponStash.getCouponList());
        filteredCoupons = new FilteredList<>(sortedCoupons,
                PREDICATE_SHOW_ALL_ACTIVE_COUPONS);

        monthView = new ObservableMonthView();
        history = new HistoryManager(this.couponStash.copy());
    }

    public ModelManager() {
        this(new CouponStash(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public StashSettings getStashSettings() {
        return userPrefs.getStashSettings();
    }

    @Override
    public void setStashSettings(StashSettings ss) {
        requireNonNull(ss);
        userPrefs.setStashSettings(ss);
    }

    @Override
    public Path getCouponStashFilePath() {
        return userPrefs.getCouponStashFilePath();
    }

    @Override
    public void setCouponStashFilePath(Path couponStashFilePath) {
        requireNonNull(couponStashFilePath);
        userPrefs.setCouponStashFilePath(couponStashFilePath);
    }

    @Override
    public String setMoneySymbol(String moneySymbol) {
        logger.info("Currency symbol changed to " + moneySymbol);
        String oldSymbol = this.getStashSettings().getMoneySymbol().setString(moneySymbol);
        // force refresh of the JavaFX list so Coupons will show the new symbol
        Predicate<? super Coupon> pred = this.filteredCoupons.getPredicate();
        updateFilteredCouponList(coupon -> false);
        // restore the old list after a short moment
        try {
            Platform.runLater(() -> {
                updateFilteredCouponList(pred);
                logger.info("Coupon list refreshed to show new symbol " + moneySymbol);
            });
        } catch (IllegalStateException e) {
            logger.warning(e.getMessage());
        }
        return oldSymbol;
    }

    //=========== CouponStash ================================================================================
    // Please run commitCouponStash() every time couponsStash is mutated.

    @Override
    public void setCouponStash(ReadOnlyCouponStash couponStash, String commandText) {
        this.couponStash.resetData(couponStash);
        commitCouponStash(commandText);
    }

    @Override
    public ReadOnlyCouponStash getCouponStash() {
        return couponStash;
    }

    @Override
    public boolean hasCoupon(Coupon coupon) {
        requireNonNull(coupon);
        return couponStash.hasCoupon(coupon);
    }

    @Override
    public void deleteCoupon(Coupon target, String commandText) {
        couponStash.removeCoupon(target);
        commitCouponStash(commandText);
    }

    @Override
    public void addCoupon(Coupon coupon, String commandText) {
        couponStash.addCoupon(coupon);
        updateFilteredCouponList(PREDICATE_SHOW_ALL_ACTIVE_COUPONS);
        sortCoupons(null);
        commitCouponStash(commandText);
    }

    @Override
    public void setCoupon(Coupon target, Coupon editedCoupon, String commandText) {
        requireAllNonNull(target, editedCoupon);

        couponStash.setCoupon(target, editedCoupon);
        updateFilteredCouponList(PREDICATE_SHOW_ALL_ACTIVE_COUPONS);
        sortCoupons(null);
        commitCouponStash(commandText);
    }

    @Override
    public void sortCoupons(Comparator<Coupon> cmp) {
        sortedCoupons.setComparator(cmp);
    }

    //=========== Filtered Coupon List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Coupon} backed by the internal list of
     * {@code versionedCouponStash}
     */
    @Override
    public ObservableList<Coupon> getFilteredCouponList() {
        return filteredCoupons;
    }

    /**
     * Returns every single Coupon present in Coupon Stash.
     *
     * @return An ObservableList (unmodifiable view) of all Coupons
     *         in the Coupon Stash, whether displayed or not
     */
    @Override
    public ObservableList<Coupon> getAllCouponList() {
        return couponStash.getCouponList();
    }

    @Override
    public void updateFilteredCouponList(Predicate<? super Coupon> predicate) {
        requireNonNull(predicate);
        filteredCoupons.setPredicate(predicate);
    }

    //=========== MonthView of Calendar Accessors =============================================================

    /**
     * Returns an unmodifiable MonthView of the Calendar.
     */
    @Override
    public ObservableMonthView getMonthView() {
        return monthView;
    }

    @Override
    public void updateMonthView(String yearMonth) {
        requireNonNull(monthView);
        monthView.setValue(yearMonth);
    }


    //=========== Undo/Redo functionality =============================================================
    @Override
    public void commitCouponStash(String commandText) {
        history.commitState(couponStash.copy(), commandText);
    }

    @Override
    public String undoCouponStash() {
        couponStash.resetData(history.undo());
        return history.getNextCommandText();
    }

    @Override
    public String redoCouponStash() {
        String nextCommand = history.getNextCommandText();
        couponStash.resetData(history.redo());
        return nextCommand;
    }

    @Override
    public boolean canUndoCouponStash() {
        return history.canUndo();
    }

    @Override
    public boolean canRedoCouponStash() {
        return history.canRedo();
    }

    //=========== End of Undo/Redo functionality =============================================================

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return couponStash.equals(other.couponStash)
                && userPrefs.equals(other.userPrefs)
                && filteredCoupons.equals(other.filteredCoupons);
    }

}
