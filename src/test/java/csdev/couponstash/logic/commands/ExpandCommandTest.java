package csdev.couponstash.logic.commands;

import static csdev.couponstash.logic.commands.CommandTestUtil.assertCommandFailure;
import static csdev.couponstash.logic.commands.CommandTestUtil.assertCommandSuccess;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import csdev.couponstash.commons.core.Messages;
import csdev.couponstash.commons.core.index.Index;
import csdev.couponstash.model.Model;
import csdev.couponstash.model.ModelManager;
import csdev.couponstash.model.UserPrefs;
import csdev.couponstash.model.coupon.Coupon;
import csdev.couponstash.testutil.TypicalCoupons;
import csdev.couponstash.testutil.TypicalIndexes;

class ExpandCommandTest {

    private Model model = new ModelManager(TypicalCoupons.getTypicalCouponStash(), new UserPrefs());
    private Model expectedModel = new ModelManager(TypicalCoupons.getTypicalCouponStash(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Coupon couponToExpand = model.getFilteredCouponList().get(TypicalIndexes.INDEX_FIRST_COUPON.getZeroBased());
        ExpandCommand expandCommand = new ExpandCommand(TypicalIndexes.INDEX_FIRST_COUPON);

        CommandResult expectedCommandResult = new CommandResult(
                String.format(ExpandCommand.MESSAGE_EXPAND_COUPON_SUCCESS, couponToExpand.getName()),
                Optional.of(couponToExpand),
                Optional.empty(),
                false,
                false
        );

        assertCommandSuccess(expandCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCouponList().size() + 1);
        ExpandCommand expandCommand = new ExpandCommand(outOfBoundIndex);

        assertCommandFailure(expandCommand, model, Messages.MESSAGE_INVALID_COUPON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ExpandCommand expandFirstCommand = new ExpandCommand(TypicalIndexes.INDEX_FIRST_COUPON);
        ExpandCommand expandSecondCommand = new ExpandCommand(TypicalIndexes.INDEX_SECOND_COUPON);

        // same object -> returns true
        assertTrue(expandFirstCommand.equals(expandFirstCommand));

        // same values -> returns true
        ExpandCommand expandFirstCommandCopy = new ExpandCommand(TypicalIndexes.INDEX_FIRST_COUPON);
        assertTrue(expandFirstCommand.equals(expandFirstCommandCopy));

        // different types -> returns false
        assertFalse(expandFirstCommand.equals(1));

        // null -> returns false
        assertFalse(expandFirstCommand.equals(null));

        // different command -> returns false
        assertFalse(expandFirstCommand.equals(expandSecondCommand));
    }
}
