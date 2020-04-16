package csdev.couponstash.logic.commands;

import static csdev.couponstash.logic.commands.CommandTestUtil.assertCommandSuccess;
import static csdev.couponstash.logic.commands.ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import csdev.couponstash.model.Model;
import csdev.couponstash.model.ModelManager;

public class ExitCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_exit_success() {
        CommandResult expectedCommandResult =
                new CommandResult(
                        MESSAGE_EXIT_ACKNOWLEDGEMENT,
                        Optional.empty(),
                        Optional.empty(),
                        false,
                        true
                );
        assertCommandSuccess(new ExitCommand(), model, expectedCommandResult, expectedModel);
    }
}
