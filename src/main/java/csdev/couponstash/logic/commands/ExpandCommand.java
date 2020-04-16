package csdev.couponstash.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import csdev.couponstash.commons.core.Messages;
import csdev.couponstash.commons.core.index.Index;
import csdev.couponstash.logic.commands.exceptions.CommandException;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.coupon.Coupon;

/**
 * Expand a Coupon in the CouponStash.
 */
public class ExpandCommand extends IndexedCommand {

    public static final String COMMAND_WORD = "expand";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Expands the coupon identified by the "
            + "index number. Coupon will be expanded in a new window.\n\n"
            + "Parameters: INDEX (must be a positive integer)\n\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_EXPAND_COUPON_SUCCESS = "Coupon opened in new window: %s\n\n"
            + "To exit, press Ctrl + Q (for all OS)";

    /**
     * @param index of the coupon in the filtered coupon list to expand
     */
    public ExpandCommand(Index index) {
        super(index);
    }

    @Override
    public CommandResult execute(Model model, String commandText) throws CommandException {
        requireNonNull(model);
        List<Coupon> lastShownList = model.getFilteredCouponList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_COUPON_DISPLAYED_INDEX);
        }

        Coupon couponToExpand = lastShownList.get(targetIndex.getZeroBased());

        return new CommandResult(
                String.format(MESSAGE_EXPAND_COUPON_SUCCESS, couponToExpand.getName()),
                Optional.of(couponToExpand),
                Optional.empty(),
                false,
                false
        );
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof ExpandCommand
                && targetIndex.equals(((ExpandCommand) other).targetIndex));
    }
}
