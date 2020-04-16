package csdev.couponstash.model.coupon;

import static csdev.couponstash.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

import csdev.couponstash.commons.util.DateUtil;

/**
 * Represents a Coupon's remind date in the CouponStash.
 */
public class RemindDate {

    public static final String MESSAGE_CONSTRAINTS =
            "Remind Dates should not be a date after the Expiry date "
                    + "(in the D-M-YYYY format).";

    private final LocalDate date;
    private final String value;

    /**
     * Constructs a {@code RemindDate}.
     *
     * @param remindDate A valid remind date.
     */
    public RemindDate(String remindDate) {
        requireNonNull(remindDate);
        // Check if date is valid
        checkArgument(DateUtil.isValidDate(remindDate), MESSAGE_CONSTRAINTS);
        value = DateUtil.parseStringToStandardDateString(remindDate);
        date = DateUtil.parseStringToDate(remindDate);
    }

    /**
     * Returns true if the {@RemindDate} is after the {@ExpiryDate}.
     * @param ed The ExpiryDate
     * @return True if remind date is after to the expiry date
     */
    public boolean isAfter(ExpiryDate ed) {
        return date.isAfter(ed.date);
    }

    /**
     * Returns true if the {@RemindDate} is today.
     * @return True if remind date is today
     */
    public boolean isToday() {
        return date.equals(LocalDate.now());
    }

    /**
     * Returns the remind date as a LocalDate.
     * @return Remind Date as a LocalDate
     */
    public LocalDate getDate() {
        return this.date;
    }


    @Override
    public String toString() {
        return value;
    }

    /**
     * Make a new copy of current RemindDate
     *
     * @return a copy of the current RemindDate
     */
    public RemindDate copy() {
        return new RemindDate(value);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RemindDate// instanceof handles nulls
                && date.equals(((RemindDate) other).date)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
