package csdev.couponstash.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import csdev.couponstash.model.Model;
import csdev.couponstash.model.ModelManager;
import csdev.couponstash.model.UserPrefs;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.testutil.TypicalCoupons;
import csdev.couponstash.testutil.TypicalIndexes;

public class CommandResultTest {
    private Model model = new ModelManager(TypicalCoupons.getTypicalCouponStash(), new UserPrefs());

    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult(
                "feedback", Optional.empty(), Optional.empty(), false, false))
        );

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult(
                "feedback", Optional.empty(), Optional.empty(), false, true))
        );

        // different help value -> returns false
        assertFalse(commandResult.equals(new CommandResult(
                "feedback", Optional.empty(), Optional.empty(), true, false))
        );

        // different coupon to share value -> returns false
        Coupon couponToShare = model.getFilteredCouponList()
                .get(TypicalIndexes.INDEX_FIRST_COUPON.getZeroBased());

        assertFalse(commandResult.equals(new CommandResult(
                    "feedback",
                    Optional.empty(),
                    Optional.of(couponToShare),
                        false,
                        true
                ))
        );

        // different coupon to expand value -> returns false
        Coupon couponToExpand = model.getFilteredCouponList()
                .get(TypicalIndexes.INDEX_FIRST_COUPON.getZeroBased());

        assertFalse(commandResult.equals(new CommandResult(
                        "feedback",
                        Optional.of(couponToExpand),
                        Optional.empty(),
                        false,
                        true
                ))
        );
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult(
                "feedback",
                Optional.empty(),
                Optional.empty(),
                false,
                true
        ).hashCode());
    }
}
