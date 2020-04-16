package csdev.couponstash.logic.commands;

import java.util.Optional;

import csdev.couponstash.model.Model;

/**
 * Terminates the program.
 */
public class ExitCommand extends Command {

    public static final String COMMAND_WORD = "exit";

    public static final String MESSAGE_EXIT_ACKNOWLEDGEMENT = "Exiting CouponStash as requested ...";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Exits the program.\n\n"
            + "Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute(Model model, String commandText) {
        return new CommandResult(
                MESSAGE_EXIT_ACKNOWLEDGEMENT,
                Optional.empty(),
                Optional.empty(),
                false,
                true
        );
    }

}
