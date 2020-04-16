package csdev.couponstash.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import csdev.couponstash.model.coupon.Coupon;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** The application should exit. */
    private final boolean exit;

    /** The application should open help. */
    private final boolean help;

    /** The application has a coupon to expand. */
    private final Optional<Coupon> couponToExpand;

    /** The application has a coupon to share. */
    private final Optional<Coupon> couponToShare;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(
            String feedbackToUser,
            Optional<Coupon> couponToExpand,
            Optional<Coupon> couponToShare,
            boolean help,
            boolean exit
    ) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.couponToExpand = couponToExpand;
        this.couponToShare = couponToShare;
        this.help = help;
        this.exit = exit;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, Optional.empty(), Optional.empty(), false, false);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isExit() {
        return exit;
    }

    public boolean isHelp() {
        return help;
    }

    public Optional<Coupon> getCouponToExpand() {
        return couponToExpand;
    }

    public Optional<Coupon> getCouponToShare() {
        return couponToShare;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult)) {
            return false;
        }

        CommandResult otherCommandResult = (CommandResult) other;

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && couponToShare.equals(otherCommandResult.couponToShare)
                && couponToExpand.equals(otherCommandResult.couponToExpand)
                && exit == otherCommandResult.exit
                && help == otherCommandResult.help;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, couponToExpand, couponToShare, help, exit);
    }

}
