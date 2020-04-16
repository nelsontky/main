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

class ShareCommandTest {

    private Model model = new ModelManager(TypicalCoupons.getTypicalCouponStash(), new UserPrefs());
    private Model expectedModel = new ModelManager(TypicalCoupons.getTypicalCouponStash(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Coupon couponToShare = model.getFilteredCouponList().get(TypicalIndexes.INDEX_FIRST_COUPON.getZeroBased());
        ShareCommand shareCommand = new ShareCommand(TypicalIndexes.INDEX_FIRST_COUPON);
        CommandResult expectedCommandResult = new CommandResult(
                String.format(ShareCommand.MESSAGE_SHARE, couponToShare.getName()),
                Optional.empty(),
                Optional.of(couponToShare),
                false,
                false
        );

        assertCommandSuccess(shareCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCouponList().size() + 1);
        ShareCommand shareCommand = new ShareCommand(outOfBoundIndex);
        assertCommandFailure(shareCommand, model, Messages.MESSAGE_INVALID_COUPON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ShareCommand shareFirstCommand = new ShareCommand(TypicalIndexes.INDEX_FIRST_COUPON);
        ShareCommand shareSecondCommand = new ShareCommand(TypicalIndexes.INDEX_SECOND_COUPON);

        // same object -> returns true
        assertTrue(shareFirstCommand.equals(shareFirstCommand));

        // same values -> returns true
        ShareCommand shareFirstCommandCopy = new ShareCommand(TypicalIndexes.INDEX_FIRST_COUPON);
        assertTrue(shareFirstCommand.equals(shareFirstCommandCopy));

        // different types -> returns false
        assertFalse(shareFirstCommand.equals(1));

        // null -> returns false
        assertFalse(shareFirstCommand.equals(null));

        // different command -> returns false
        assertFalse(shareFirstCommand.equals(shareSecondCommand));
    }
}
