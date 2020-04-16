package csdev.couponstash.logic.commands;

import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_EXPIRY_DATE;
import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_NAME;
import static csdev.couponstash.logic.parser.CliSyntax.PREFIX_REMIND;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import csdev.couponstash.logic.parser.Prefix;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.coupon.Coupon;

/**
 * Creates an SortCommand to sort by specified prefix.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sorts the on screen coupons in CouponStash "
            + "by coupon name or expiry date. For example, sorting after finding "
            + "will sort all the found coupons, including archived ones if the are present on screen. "
            + "The order will persist throughout the runtime of the program.\n\n"
            + "Parameters: "
            + PREFIX_NAME + ", " + PREFIX_EXPIRY_DATE + " or " + PREFIX_REMIND + "\n\n"
            + "Examples:\n"
            + COMMAND_WORD + " " + PREFIX_NAME + " (sort by name)\n"
            + COMMAND_WORD + " " + PREFIX_EXPIRY_DATE + " (sort by expiry date)\n"
            + COMMAND_WORD + " " + PREFIX_REMIND + " (sort by reminder date)";

    public static final String MESSAGE_SUCCESS = "Successfully sorted by %s";

    public static final Comparator<Coupon> NAME_COMPARATOR = Comparator.comparing(x -> x
            .toString()
            .toLowerCase());
    public static final Comparator<Coupon> EXPIRY_COMPARATOR = Comparator.comparing(x -> x
            .getExpiryDate()
            .getDate());
    public static final Comparator<Coupon> REMINDER_COMPARATOR = Comparator.comparing(x -> x
            .getRemindDate()
            .getDate());

    private Prefix prefixToSortBy;

    /**
     * Creates a SortCommand that sorts the coupons according to the prefix specified
     * @param prefixToSortBy prefix to sort by
     */
    public SortCommand(Prefix prefixToSortBy) {
        requireNonNull(prefixToSortBy);

        this.prefixToSortBy = prefixToSortBy;
    }

    @Override
    public CommandResult execute(Model model, String commandText) {
        requireNonNull(model);

        assert prefixToSortBy.equals(PREFIX_NAME)
                || prefixToSortBy.equals(PREFIX_EXPIRY_DATE)
                || prefixToSortBy.equals(PREFIX_REMIND)
                : "Invalid prefixes should have been filtered out in SortCommandParser!";

        if (prefixToSortBy.equals(PREFIX_NAME)) {
            model.sortCoupons(NAME_COMPARATOR);
        } else if (prefixToSortBy.equals(PREFIX_EXPIRY_DATE)) {
            model.sortCoupons(EXPIRY_COMPARATOR);
        } else if (prefixToSortBy.equals(PREFIX_REMIND)) {
            model.sortCoupons(REMINDER_COMPARATOR);
        }

        // Put non-archived at the top.
        model.sortCoupons(Model.COMPARATOR_NON_ARCHIVED_FIRST);

        return new CommandResult(
                String.format(MESSAGE_SUCCESS, prefixToSortBy.toString())
        );
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SortCommand)) {
            return false;
        }

        // state check
        return prefixToSortBy.equals(((SortCommand) other).prefixToSortBy);
    }
}
