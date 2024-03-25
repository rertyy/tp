package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_CLIENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_CLIENT;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.orders.AddOrderCommand;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.client.Client;
import seedu.address.model.order.Order;
import seedu.address.testutil.ClientBuilder;
import seedu.address.testutil.OrderBuilder;

public class AddOrderCommandTest {

    @Test
    public void execute_orderAcceptedByModel_addSuccessful() throws Exception {
        ClientBuilder clientBuilder = new ClientBuilder();
        Client client = clientBuilder.build();
        OrderBuilder builder = new OrderBuilder();
        Order order = builder.build();
        ModelStubAcceptingOrderAdded modelStub = new ModelStubAcceptingOrderAdded(order, client);

        Index targetIndex = INDEX_FIRST_CLIENT;
        CommandResult commandResult = new AddOrderCommand(targetIndex, order).execute(modelStub);
        assertEquals(1, modelStub.getFilteredOrderList().size());
    }

    @Test
    public void execute_orderAcceptedByModel_indexError() throws Exception {
        ClientBuilder clientBuilder = new ClientBuilder();
        Client client = clientBuilder.build();
        OrderBuilder builder = new OrderBuilder();
        Order order = builder.build();
        ModelStubAcceptingOrderAdded modelStub = new ModelStubAcceptingOrderAdded(order, client);
        Index targetIndex = INDEX_THIRD_CLIENT;
        assertThrows(CommandException.class, () -> new AddOrderCommand(targetIndex, order).execute(modelStub));
    }


    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addClient(Client client) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasClient(Client client) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteClient(Client target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setClient(Client target, Client editedClient) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setClient(Client target, Client editedClient, Order order) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Client> getFilteredClientList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Order> getFilteredOrderList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredClientList(Predicate<Client> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredOrderList(Predicate<Order> predicate) {
            throw new AssertionError("This method should not be called.");
        }

    }

    /**
     * A Model stub that always accepts the order being added.
     */
    private class ModelStubAcceptingOrderAdded extends AddOrderCommandTest.ModelStub {
        private Order order;
        private Client client;

        ModelStubAcceptingOrderAdded(Order order, Client client) {
            requireNonNull(order);
            this.order = order;
            this.client = client;
        }

        @Override
        public void setClient(Client target, Client editedClient) {
            requireAllNonNull(target, editedClient);
            this.client = editedClient;
        }

        @Override
        public void setClient(Client target, Client editedClient, Order order) {
            requireAllNonNull(target, editedClient);
            this.client = editedClient;
        }

        @Override
        public ObservableList<Client> getFilteredClientList() {
            List<Client> sampleList = new ArrayList<>();
            sampleList.add(this.client);
            ObservableList<Client> clientList = FXCollections.observableArrayList(sampleList);
            return clientList;
        }

        @Override
        public ObservableList<Order> getFilteredOrderList() {
            ObservableList<Order> orderList = FXCollections.observableArrayList(this.client.getOrders());
            return orderList;
        }

        @Override
        public void updateFilteredOrderList(Predicate<Order> predicate) {
            requireNonNull(predicate);
        }
    }
}
