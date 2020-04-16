package csdev.couponstash.logic.parser;

import static csdev.couponstash.logic.commands.SetCurrencyCommand.MESSAGE_MISSING_VALUES;
import static java.util.Objects.requireNonNull;

import csdev.couponstash.logic.commands.SetCurrencyCommand;
import csdev.couponstash.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new SetCurrencyCommand object,
 * given that the input arguments are valid and there is at least
 * one preference to be changed
 */
public class SetCurrencyCommandParser implements Parser<SetCurrencyCommand> {
    // ensure that currency symbol does not contain numbers
    private static String invalidationRegex = ".*\\d.*";
    // limit for the length of string possible
    private static final int STRING_LENGTH_LIMIT = 5;

    /**
     * Parses the raw String from Coupon Stash's command line
     * into a SetCurrencyCommand.
     *
     * @param userInput String representing raw input from user
     *                  (without the command word).
     * @return Returns a new SetCurrencyCommand with the fields that
     *         need to be changed, as supplied by the user.
     * @throws ParseException If zero fields are supplied in
     *                        the SetCurrencyCommand.
     */
    @Override
    public SetCurrencyCommand parse(String userInput) throws ParseException {
        requireNonNull(userInput);
        ArgumentMultimap argMultimap =
             ArgumentTokenizer.tokenize(
                     userInput,
                     CliSyntax.PREFIX_MONEY_SYMBOL);
        ParseException emptySymbolException = new ParseException(MESSAGE_MISSING_VALUES);
        String newSymbol = argMultimap.getValue(CliSyntax.PREFIX_MONEY_SYMBOL)
                .map(String::trim)
                .orElseThrow(() -> emptySymbolException);

        if (newSymbol.isBlank()) {
            throw emptySymbolException;
        } else if (containsNumbers(newSymbol)) {
            throw new ParseException(SetCurrencyCommand.MESSAGE_CONSTRAINTS);
        }
        return new SetCurrencyCommand(isWithinLimit(newSymbol));
    }


    /**
     * Returns true if a given String has a number
     * (one of the limitations for a valid currency
     * symbol is the absence of numbers).
     *
     * @param currencySymbol The String value to be tested.
     */
    private static boolean containsNumbers(String currencySymbol) {
        return currencySymbol.matches(SetCurrencyCommandParser.invalidationRegex);
    }

    /**
     * Checks if a given String is within the
     * limit for length of a currency symbol.
     *
     * @param currencySymbol The String value to be tested.
     * @return The same String, if it passes the checks.
     * @throws ParseException If the String is not within
     *                        the limit for length.
     */
    private static String isWithinLimit(String currencySymbol) throws ParseException {
        return ParserUtil.checkStringLength(currencySymbol, SetCurrencyCommandParser.STRING_LENGTH_LIMIT);
    }
}
