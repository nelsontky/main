package csdev.couponstash.logic.commands;

import java.util.Optional;

import csdev.couponstash.model.Model;

/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows program user guide in a browser window. "
            + "Will take some time if it is the first time running this command!\n\n"
            + "Example: " + COMMAND_WORD;

    public static final String SHOWING_HELP_MESSAGE =
            "Loading user guide. Loading may take some time on first run.";

    public static final String BROWSER_OPEN_SUCCESS = "User guide successfully opened in web browser!";

    public static final String UNSUPPORTED_OS =
            "Your operating system does not allow the opening of the browser automatically. "
                    + "Visit https://ay1920s2-cs2103t-f09-1.github.io/main/UserGuide.html with your web browser "
                    + "to view the user guide.";

    public static final String ERROR = "An error has occurred. Please try the help command again.";

    public static final String HTML_NAME = "help.html";

    @Override
    public CommandResult execute(Model model, String commandText) {
        return new CommandResult(SHOWING_HELP_MESSAGE, Optional.empty(), Optional.empty(), true, false);
    }
}


