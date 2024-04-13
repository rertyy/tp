package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.COST_DESC_ROSES;
import static seedu.address.logic.commands.CommandTestUtil.DEADLINE_DESC_ROSES;
import static seedu.address.logic.commands.CommandTestUtil.DESCRIPTION_DESC_ROSES;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_COST_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_DEADLINE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_DESCRIPTION_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_COST_ROSES;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DEADLINE_ROSES;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DESCRIPTION_ROSES;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ORDER_ID_ROSES;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.DateTimeUtil;
import seedu.address.logic.commands.orders.AddOrderCommand;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Order;
import seedu.address.model.order.Price;
import seedu.address.model.order.Remark;
import seedu.address.model.person.Person;
import seedu.address.testutil.OrderBuilder;
import seedu.address.testutil.PersonBuilder;

public class AddOrderCommandParserTest {
    private final AddOrderCommandParser parser = new AddOrderCommandParser();

    // valid:
    // order 1 d/RedRoses c/60 by/03-04-2024 12:00

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder().build();
        Order expectedOrder = new OrderBuilder().withOrderDate(DateTimeUtil.getCurrentTime())
                .withOrderId(VALID_ORDER_ID_ROSES).withRemark(VALID_DESCRIPTION_ROSES)
                .withPrice(VALID_COST_ROSES).withDeadline(VALID_DEADLINE_ROSES).build();
        expectedPerson.addOrder(expectedOrder);
        expectedOrder.setPerson(expectedPerson);

        // Correct command format
        assertParseSuccess(parser, "1" + DESCRIPTION_DESC_ROSES + COST_DESC_ROSES + DEADLINE_DESC_ROSES,
                new AddOrderCommand(INDEX_FIRST_PERSON, expectedOrder));
    }

    @Test
    public void parse_optionalFieldsMissing_failure() {
        // Missing cost field
        assertParseFailure(parser, INDEX_FIRST_PERSON + DESCRIPTION_DESC_ROSES + DEADLINE_DESC_ROSES,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddOrderCommand.MESSAGE_USAGE));

        // Missing deadline field
        assertParseFailure(parser, INDEX_FIRST_PERSON + DESCRIPTION_DESC_ROSES + COST_DESC_ROSES,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddOrderCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid description
        assertParseFailure(parser, INDEX_FIRST_PERSON + INVALID_DESCRIPTION_DESC
                        + COST_DESC_ROSES + DEADLINE_DESC_ROSES,
                Remark.MESSAGE_CONSTRAINTS);

        // invalid cost (non-numeric)
        assertParseFailure(parser, INDEX_FIRST_PERSON + DESCRIPTION_DESC_ROSES
                        + INVALID_COST_DESC + DEADLINE_DESC_ROSES,
                Price.MESSAGE_CONSTRAINTS);

        // invalid deadline format
        assertParseFailure(parser, INDEX_FIRST_PERSON + DESCRIPTION_DESC_ROSES
                        + COST_DESC_ROSES + INVALID_DEADLINE_DESC,
                Deadline.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        // missing description prefix
        assertParseFailure(parser, INDEX_FIRST_PERSON + COST_DESC_ROSES + DEADLINE_DESC_ROSES,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddOrderCommand.MESSAGE_USAGE));

        // missing cost prefix
        assertParseFailure(parser, INDEX_FIRST_PERSON + DESCRIPTION_DESC_ROSES + DEADLINE_DESC_ROSES,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddOrderCommand.MESSAGE_USAGE));

        // missing deadline prefix
        assertParseFailure(parser, INDEX_FIRST_PERSON + DESCRIPTION_DESC_ROSES + COST_DESC_ROSES,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddOrderCommand.MESSAGE_USAGE));
    }

    @Test
    public void check_sameCurrentTime_success() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        DateTimeUtil.getCurrentTime();
        assertEquals(DateTimeUtil.getCurrentTime(), now.format(formatter));


    }
}
