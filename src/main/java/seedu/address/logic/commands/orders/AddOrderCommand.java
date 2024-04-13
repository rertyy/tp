package seedu.address.logic.commands.orders;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_ORDERS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.order.Order;
import seedu.address.model.person.Person;

/**
 * Adds an order to an assigned person.
 */
public class AddOrderCommand extends Command {
    public static final String COMMAND_WORD = "order";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Creates an order that is associated to a client. "
            + "Multiple orders will be appended to each other, "
            + "and old orders will always be kept during this operation.\n"
            + "Parameters: INDEX (must be a positive integer), "
            + "DETAILS (in formation related to order), "
            + "DEADLINE (the date the order is due) \n"
            + "Example: " + COMMAND_WORD + " 1 d/1xRoses c/40 by/23-07-2024 00:00";

    public static final String MESSAGE_SUCCESS = "New Order added! %1$s";
    private final Order order;
    private final Index index;

    /**
     * Creates an AddOrderCommand to add the specified {@code Order}.
     */
    public AddOrderCommand(Index index, Order order) {
        requireAllNonNull(index, order);

        this.index = index;
        this.order = order;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = personToEdit.addOrder(order);
        order.setPerson(editedPerson);

        model.setPersonAndAddOrder(personToEdit, editedPerson, this.order);
        model.updateFilteredOrderList(PREDICATE_SHOW_ALL_ORDERS);
        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddOrderCommand)) {
            return false;
        }

        AddOrderCommand otherAddCommand = (AddOrderCommand) other;
        return order.equals(otherAddCommand.order) && index.equals(otherAddCommand.index);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .toString();
    }

    /**
     * Generates a command execution success message based on whether
     * the order is added to or removed from
     * {@code personToEdit}.
     */
    private String generateSuccessMessage(Person personToEdit) {
        return String.format(MESSAGE_SUCCESS, personToEdit.getName());
    }
}
