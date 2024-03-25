package seedu.address.logic.commands.orders;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.client.Client;
import seedu.address.model.order.Order;
import seedu.address.model.order.OrderId;


/**
 * Removes an existing order in the address book.
 */
public class DeleteOrderCommand extends Command {
    public static final String MESSAGE_ARGUMENTS = "Index: %1$d";
    public static final String MESSAGE_SUCCESS = "Deleted Order: %1$s";

    public static final String COMMAND_WORD = "deleteOrder";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the order identified by the index number used in the displayed order list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " id/<UUID>";

    public static final String MESSAGE_DELETE_ORDER_SUCCESS = "Deleted Order!";

    private final OrderId index;


    /**
     * Creates an DeleteOrderCommand to delete the specified {@code Order}.
     */
    public DeleteOrderCommand(OrderId index) {
        this.index = index;
    }


    /**
     * Retrieves the order from the client which matches the index.
     *
     * @param client Client to retrieve the order from.
     * @return Order if found, else null.
     */
    private Order getOrderFromClient(Client client) {
        return client.getOrders().stream()
                .filter(order -> order.checkId(this.index))
                .findFirst()
                .orElse(null);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);

        Order changeOrder = null;
        Client clientToEdit = null;
        List<Client> clientList = model.getAddressBook().getClientList();

        for (Client client : clientList) {
            Order order = getOrderFromClient(client);
            if (order != null) {
                changeOrder = order;
                clientToEdit = client;
                break;
            }
        }

        if (changeOrder == null) {
            throw new CommandException(Messages.MESSAGE_INVALID_ORDER_DISPLAYED_INDEX);
        }

        Client editedClient = new Client(
                clientToEdit.getName(), clientToEdit.getPhone(), clientToEdit.getEmail(),
                clientToEdit.getAddress(), clientToEdit.getTags(),
                removeOrder(this.index, clientToEdit.getOrders()));

        model.setClient(clientToEdit, editedClient);

        return new CommandResult(generateSuccessMessage(editedClient));
    }

    private Set<Order> removeOrder(OrderId orderId, Set<Order> orders) {
        orders = new HashSet<>(orders);
        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()) {
            Order currentOrder = iterator.next();
            if (currentOrder.getOrderId().equals(orderId)) {
                iterator.remove();
                break;
            }
        }
        return orders;
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
