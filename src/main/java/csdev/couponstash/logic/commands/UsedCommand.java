package csdev.couponstash.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import csdev.couponstash.commons.core.Messages;
import csdev.couponstash.commons.core.index.Index;
import csdev.couponstash.logic.commands.exceptions.CommandException;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.coupon.Archived;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.model.coupon.Limit;
import csdev.couponstash.model.coupon.StartDate;
import csdev.couponstash.model.coupon.Usage;
import csdev.couponstash.model.coupon.savings.MonetaryAmount;
import csdev.couponstash.model.coupon.savings.PureMonetarySavings;
import csdev.couponstash.model.coupon.savings.SavingsConversionUtil;

/**
 * Increases the usage of a coupon.
 */
public class UsedCommand extends IndexedCommand {
    public static final String COMMAND_WORD = "used";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Increases the usage of the coupon "
            + "identified by the index number used in the displayed coupon list. "
            + "This increases the value of its usage by one.\n\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[%s" + "(Original amount of purchase)]\n\n"
            + "Example: " + COMMAND_WORD + " 1\n\n"
            + "Example with Savings: " + COMMAND_WORD + " 1 " + "%s100";

    public static final String MESSAGE_USED_COUPON_SUCCESS = "Used Coupon: %1$s";
    public static final String MESSAGE_USAGE_LIMIT_REACHED = "Coupon usage limit has been reached! "
            + "You can only use it for a maximum of %s time(s).";
    public static final String MESSAGE_MISSING_ORIGINAL_AMOUNT = "Coupon has percentage savings "
            + "that requires the input of the original amount of purchase.\n"
            + "Example: " + COMMAND_WORD + " 1 %s100";
    public static final String MESSAGE_ARCHIVED_COUPON = "Coupon has been archived!\n"
            + "To use the coupon again, type the command `unarchive %s` first.";
    public static final String MESSAGE_COUPON_HAVENT_START = "Coupon's start date is after today! "
            + "You can edit the start date with the `edit` command if you still wish to use this coupon.";

    private final MonetaryAmount originalAmount;

    /**
     * Creates a UsedCommand to increase the usage of the specified {@code Coupon}.
     */
    public UsedCommand(Index targetIndex) {
        super(targetIndex);
        this.originalAmount = new MonetaryAmount(0, 0);
    }

    /**
     * Creates a UsedCommand to increase the usage of the specified {@code Coupon},
     * and also keep track of the amount saved by the usage of this coupon.
     */
    public UsedCommand(Index targetIndex, MonetaryAmount originalAmount) {
        super(targetIndex);

        requireNonNull(originalAmount);

        this.originalAmount = originalAmount;
    }

    @Override
    public CommandResult execute(Model model, String commandText) throws CommandException {
        requireNonNull(model);
        List<Coupon> lastShownList = model.getFilteredCouponList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_COUPON_DISPLAYED_INDEX);
        }

        Coupon couponToBeUsed = lastShownList.get(targetIndex.getZeroBased());
        StartDate startDate = couponToBeUsed.getStartDate();
        if (startDate.date.isAfter(LocalDate.now())) {
            throw new CommandException(String.format(MESSAGE_COUPON_HAVENT_START));
        }

        Archived archived = couponToBeUsed.getArchived();
        if (archived.state) {
            throw new CommandException(String.format(MESSAGE_ARCHIVED_COUPON, targetIndex.getOneBased()));
        }

        Usage currentUsage = couponToBeUsed.getUsage();
        Limit limit = couponToBeUsed.getLimit();
        if (currentUsage.isAtLimit(limit)) {
            throw new CommandException(String.format(MESSAGE_USAGE_LIMIT_REACHED, limit.getLimit()));
        }

        boolean hasPercentageSavings = couponToBeUsed.getSavingsForEachUse().hasPercentageAmount();
        String moneySymbol = model.getStashSettings().getMoneySymbol().getString();
        // checks if original amount of purchase is provided if the type of Savings is of percentage amount
        if (hasPercentageSavings && (originalAmount.equals(new MonetaryAmount(0, 0)))) {
            throw new CommandException(String.format(MESSAGE_MISSING_ORIGINAL_AMOUNT, moneySymbol));
        }

        Coupon usedCoupon;
        if (hasPercentageSavings) {
            usedCoupon = createUsedCouponPercentageValue(couponToBeUsed, originalAmount);
        } else {
            usedCoupon = createUsedCouponMonetaryValue(couponToBeUsed);
        }

        Optional<Coupon> archivedUsedCoupon = Optional.empty();
        Usage newUsage = usedCoupon.getUsage();
        if (newUsage.isAtLimit(limit)) {
            archivedUsedCoupon = Optional.of(usedCoupon.archive());
        }

        Coupon newCoupon = archivedUsedCoupon.orElse(usedCoupon);
        model.setCoupon(couponToBeUsed, newCoupon, commandText);
        model.updateFilteredCouponList(Model.PREDICATE_SHOW_ALL_ACTIVE_COUPONS);
        return new CommandResult((String.format(MESSAGE_USED_COUPON_SUCCESS, usedCoupon.getName())));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof UsedCommand
            && targetIndex.equals(((UsedCommand) other).targetIndex))
            && originalAmount.equals(((UsedCommand) other).originalAmount);
    }

    /**
     * Creates and returns a {@code Coupon} with an increase in usage by one.
     * Also adds to the total savings for the current date.
     * To be used for a Coupon with PercentageAmount savings (requires
     * the original price of the item bought as a MonetaryAmount).
     */
    private static Coupon createUsedCouponPercentageValue(Coupon couponToBeUsed, MonetaryAmount originalAmount) {
        PureMonetarySavings newTotalSavings = SavingsConversionUtil
                .convertToPure(couponToBeUsed.getSavingsForEachUse(), originalAmount);
        return couponToBeUsed.addToTotalSavings(LocalDate.now(), newTotalSavings)
                .increaseUsageByOne();
    }

    /**
     * Creates and returns a {@code Coupon} with an increase in usage by one.
     * Also adds to the total savings for the current date.
     * To be used for a Coupon with concrete MonetaryAmount savings.
     */
    private static Coupon createUsedCouponMonetaryValue(Coupon couponToBeUsed) {
        PureMonetarySavings newTotalSavings = SavingsConversionUtil
                .convertToPure(couponToBeUsed.getSavingsForEachUse());
        return couponToBeUsed.addToTotalSavings(LocalDate.now(), newTotalSavings)
                .increaseUsageByOne();
    }
}
