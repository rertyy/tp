package seedu.address.logic.commands.orders;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_ORDERS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.order.Order;
import seedu.address.model.client.Client;

/**
 * Adds an order to an assigned client.
 */
public class AddOrderCommand extends Command {
    public static final String COMMAND_WORD = "order";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Creates an order that is associated to a client."
            + "Multiple orders will be appended to each other, "
            + "and old orders will always be kept during this operation\n"
            + "Parameters: INDEX (must be a positive integer), "
            + "DETAILS (in formation related to order), "
            + "DEADLINE (the date the order is due"
            + "r/ [ORDER]\n"
            + "Example: " + COMMAND_WORD + " 1 d/1xRoses c/40 by/23-07-2024 00:00";

    public static final String MESSAGE_SUCCESS = "New Order added!";
    public static final String MESSAGE_FAILURE = "Failed to add new Order!";
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
        List<Client> lastShownList = model.getFilteredClientList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CLIENT_DISPLAYED_INDEX);
        }
        Client clientToEdit = lastShownList.get(index.getZeroBased());
        Set<Order> orders = clientToEdit.getOrders();
        orders = new HashSet<>(orders);
        orders.add(this.order);

        Client editedClient = new Client(
                clientToEdit.getName(), clientToEdit.getPhone(), clientToEdit.getEmail(),
                clientToEdit.getAddress(), clientToEdit.getTags(), orders);

        model.setClient(clientToEdit, editedClient, this.order);
        model.updateFilteredOrderList(PREDICATE_SHOW_ALL_ORDERS);
        return new CommandResult(generateSuccessMessage(editedClient));
    }

    /**
     * Generates a command execution success message based on whether
     * the order is added to or removed from
     * {@code clientToEdit}.
     */
    private String generateSuccessMessage(Client clientToEdit) {
        return String.format(MESSAGE_SUCCESS, clientToEdit);
    }
}
