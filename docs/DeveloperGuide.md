---
layout: page
title: Developer Guide
---

## Table of Contents

[About BookKeeper](#about-bookkeeper)

[Setting up, getting started](#setting-up-getting-started)

[Design](#design)

* [Architecture](#architecture)
* [UI Component](#ui-component)
* [Logic Component](#logic-component)
* [Model Component](#model-component)
* [Storage Component](#storage-component)
* [Common classes](#common-classes)

[Implementation](#implementation)

* [Adding the Order methods](#adding-the-order-methods)
* [Proposed Undo/redo feature](#proposed-undoredo-feature)
* [View Orders feature](#view-orders-feature)
* [Proposed Data archiving](#proposed-data-archiving)

[Documentation, logging, testing, configuration, dev-ops](#documentation-logging-testing-configuration-dev-ops)

[Appendix: Requirements](#appendix-requirements)

* [Product scope](#product-scope)
* [User stories](#user-stories)

[Use cases](#use-cases)

[Non-Functional Requirements](#non-functional-requirements)

[Glossary](#glossary)

[Appendix: Instructions for manual testing](#appendix-instructions-for-manual-testing)

[Appendix: Planned Enhancements](#appendix-planned-enhancements)


--------------------------------------------------------------------------------------------------------------------

## **About BookKeeper**

As the florist industry continues to evolve, aspiring florists like you might feel increasingly challenged with
managing complex business tasks while managing your florist business and crafting bouquets. This often leads to a need
for efficient and straightforward methods to handle sales, client interactions, and order fulfillment.

BookKeeper is a desktop application designed to support florists who are venturing into this bustling market. We focus
on alleviating the burden of many cumbersome responsibilities, from tracking client orders to optimising your
inventory management. This ensures that you can focus more on your floral designs and customer service.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of
classes [`Main`](https://github.com/AY2324S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/Main.java)
and [`MainApp`](https://github.com/AY2324S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is
in charge of the app launch and shut down.

* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues
the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding
  API `interface` mentioned in the previous point).

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using
the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component
through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the
implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified
in [`Ui.java`](https://github.com/AY2324S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts
e.g.`CommandBox`, `ResultDisplay`, `ClientListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`,
inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the
visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that
are in the `src/main/resources/view` folder. For example, the layout of
the [`MainWindow`](https://github.com/AY2324S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java)
is specified
in [`MainWindow.fxml`](https://github.com/AY2324S2-CS2103T-T09-2/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Client` object residing in the `Model`.

### Logic component

**API
** : [`Logic.java`](https://github.com/AY2324S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API
call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is invoked to execute a command, it delegates the command to an `BookKeeperParser` object. This object
   then
   creates a parser corresponding to the command type (e.g., `DeleteCommandParser`) and utilizes it to interpret the
   command.
2. This process generates a `Command` object (to be more specific, an instance of one of its subclasses, e.g.,
   DeleteCommand) which is then executed by the `LogicManager`.
3. During execution, the command can interact with the `Model` (e.g., to delete a `Client` or manage `Order`
   details).<br>
   While this interaction is depicted as a singular step in the above diagram for the sake of simplicity, the actual
   implementation may involve multiple interactions (between the command object and the `Model`) to accomplish the
   intended task.
4. The outcome of the command's execution is encapsulated within a `CommandResult` object, which is then returned from
   `Logic`. Additional classes in `Logic` (not shown in the class diagram above) that are utilized for parsing a user
   command:

<img src="images/ParserClasses.png" width="600"/>

### Model component

**API
** : [`Model.java`](https://github.com/AY2324S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="682" />


The `Model` component,

* stores bookkeeper data i.e., all `Client` objects (which are contained in a `UniqueClientList` object).
* stores the currently 'selected' `Client` objects (e.g., results of a search query) as a separate _filtered_ list which
  is exposed to outsiders as an unmodifiable `ObservableList<Client>` that can be 'observed' e.g. the UI can be bound to
  this list so that the UI automatically updates when the data in the list change.
* stores the currently 'selected' `Order` objects (e.g., results of a search query) as a separate _filtered_ list which
  is exposed to outsiders as an unmodifiable `ObservableList<Order>` that can be 'observed' e.g. the UI can be bound to
  this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as
  a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they
  should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `BookKeeper`, which `Client` references. This allows `BookKeeper` to only require one `Tag` object per unique tag, instead of each `Client` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>

### Storage component

**API
** : [`Storage.java`](https://github.com/AY2324S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

Below is an updated UML class diagram illustrating the relationships and interactions among the various storage-related
classes:

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component of our application is responsible for handling the reading and writing of data to and from
external storage sources.
This includes functionalities such as saving and retrieving bookkeeper data and user preferences.
In this section, we will delve into the recent enhancements made to the `Storage` component, focusing on the
implementation of storing clients and order details.

In this enhancement, the `Storage` component has been extended to support the storage and retrieval of both client and
order details.
Previously, the `Storage` component was primarily designed to handle bookkeeper data and user preferences. However,
with the growing requirements of our application, it becomes necessary to accommodate the storage of clients and orders.

--------------------------------------------------------------------------------------------------------------------

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Order class

![BetterOrderClassDiagram.png](images/BetterOrderClassDiagram.png)

Order is a new class added to encapsulate the logic of an Order. It has a composition relationship with the `Client`
class, and contains the following attributes:

1. OrderDate
2. Deadline
3. Price
4. Description
5. Status

The OrderDate is the time in which the order is created.  
The Deadline is the time in which the order is due, which is specificed by the user.  
The Price is a Double type where it represents the price for the order, and follows a numerical format of 2 decimal
places.  
The Description a String type which holds the description of the Order.
The Status is an enum String value consisting of either `pending`, `completed`, `canceled`.

### Storing an Order

To implement this feature, several modifications were made across different classes within the `Storage` package. The
key changes include:

1. New Classes: The introduction of `JsonAdaptedClient` and `JsonAdaptedOrder` classes to facilitate the conversion
   between JSON format and the corresponding model objects (`Client` and `Order`).

2. Updated Interfaces: The `BookKeeperStorage` interface was extended to included methods for reading and writing
   orders. Similarly, the `Storage` interface, which serves as an umbrella for all storage-related functionalities,
   was update to incorporate these changes.

3. Storage Manager: The `StorageManager` class, which orchestrates the storage operations, was modified to delegate the
   handling of client and order data to the appropriate storage classes.

4. Unit Tests: Unit tests were added or updated to ensure the correctness and robustness of the new functionalities.

#### Why it is implemented this way:

With the implementation of storing clients and orders details, the `Storage` component of our application has been
enhanced to better meet the evolving needs of our users. These changes not only improve the functionality of our
application but also lay the groundwork for future enhancements and features.

Future plans may involve further optimizing the storage mechanisms, exploring alternative storage formats, or
integrating additional data validation checks to ensure data integrity. Overall, the recent enhancements to
the `Storage` component
mark a significant step forward in enhancing the robustness and flexibility of our application.

### Adding an Order Feature

This feature allows users to add orders to our application for viewing and storage.

The sequence diagram below showcases the interactions within the application
![AddOrderSequenceDiagram.png](images%2FAddOrderSequenceDiagram.png)

An `ObservableList` has been added to the `ModelManager` for the sole purpose of displaying our `Order` objects.
Additionally, the following classes and methods have been added to support the implementation of this feature:

1. `AddOrderCommand`  
   Created a `AddOrderCommand` class to cater to order creation inputs by the user. This will get the
   referenced `Client` by their index in the `ObservableList` and check if the `Client` index is valid.
   Afterwards, it will override the `#execute` method & append the `Order` object into the `Client` object's respective
   orders list via the model interface.
   Upon execution of this command, it will return the `CommandResult` on for the output box to indicate if it was a
   successful command or not.
2. `AddOrderCommandParser`  
   Creating a `AddOrderCommandParser` class to create the respective `Command` object by parsing the user input. This
   flow is as intended, and will allow us to get index of the `Client` object in the `ObservableList` and get the
   required parameters typed by the user by parsing it with the `Prefix` objects in `CliSyntax` class.
   For this command, the prefixes used would be `d/` for description, `c/` for price and `by/` for the deadline.
   The respective `AddOrderCommand` will then be created to be executed by the `LogicManager`.

#### Why it is implemented this way

1. Separation of Concerns:   
   By delegating specific responsibilities to specialized classes (like `BookKeeperParser`,
   `AddOrderCommandParser`, etc.), the design adheres to the principle of separation of concerns. This means each part
   of
   the system has a clear responsibility, reducing complexity and making the codebase easier to understand and maintain.

### Deleting an Order Feature

This feature allows users to delete Orders from our application permanently.

The sequence diagram below showcases the interactions within the application:
![DeleteOrderSequenceDiagram.png](images%2FDeleteOrderSequenceDiagram.png)

This will delete both the `Order` in the `ObservableList` for orders and from the `Client` object as well.

The following classes and methods have been added to support the implementation of this feature:

1. `DeleteOrderCommand`  
   Created a `DeleteOrderCommand` class to cater to delete orders by their index in their `ObservableList` class.
   This will allow the users to delete by index instead of the UUID. The `DeleteOrderCommand` first checks
   the `ObservableList` by index to determine if the `Order` index is valid, then checks which `Client` the `Order`
   object belongs to. This allows the modification of both `Client`s and `Order`s at the same time.
   Upon execution of this command, it will return the `CommandResult` on for the output box to indicate if it was a
   successful command or not.
2. `DeleteOrderCommandParser`  
   Creating a `DeleteOrderCommandParser` class to create the respective `Command` object by parsing the user input.
   This flow is as intended, and will allow us to get index of the Order object in the `ObservableList`.
   This will create the respective `DeleteOrderCommand` to be executed by the `LogicManager`.

#### Why it is implemented this way

1. Separation of Concerns:   
   Again, by simply delegating specific responsibilities to specialized classes (like `DeleteOrderCommandParser` and
   `DeleteOrderCommand`), the design adheres to the principle of separation of concerns. This means each part
   of the system has a clear responsibility, reducing complexity and making the codebase easier to understand and
   maintain.

### Editing an Order Feature

This feature allows users to edit Orders objects in our application.

The following sequence diagram describes the flow:

The following classes and methods have been added to support the implementation of this feature:

1. `EditOrderCommand`  
   Created a `EditOrderCommand` class to cater to allow editing Orders by the user. It will first search the `Order`
   objects in the `ObservableList` to ensure that the index is valid, before finding the `Client` object that
   the `Order` object belongs to.
   Afterwards, the `Order` object is edited, and will be replaced in the `Client` object's orders list to update the
   details. This will be encapsulated and returned in a `Command` object that will be executed in the main logic.
2. `EditOrderCommandParser`  
   Creating a `EditOrderCommandParser` class to create the respective `Command` object by parsing the user input.
   This flow is as intended, and will allow us to get index of the Order object in the `ObservableList` and get the
   required parameters typed by the user by parsing it with
   the `Prefix` objects in `CliSyntax` class. For this command, the prefixes used would be `d/` for description, `c/`
   for price and `by/` for the deadline.
   These prefixes are optional, and not including them will use the current `Order` object details.
3. `EditOrderCommandParser#EditOrderDescriptor`  
   This is a nested static class within the `EditOrderCommand` class that manages the `Order` information.
   It's role is to temporarily hold the values of `Order` information that may or may not be updated. It acts as a data
   transfer object that contains the details to be edited by the user.
   Additionally, it helps to validate the fields to ensure that there are only valid values will be accepted, and to
   parse and apply the edits.

#### Why is it implemented this way:

It was done in this manner to adhere to the following design principles:

- Provide Extensibility: With a modular structure, adding new functionality (like future order implementations)
  involves creating new classes and modifying existing ones minimally. This approach makes the system more extensible,
  as seen with the introduction of new parser and command classes for handling orders.
- Enhances Frontend Integration:  By redefining how the `ObservableList` is managed within the `ModelManager` for
  Orders, we enhance our capability to directly manipulate the `OrderList` view in JavaFX. This adjustment in the
  ModelManager class creates a seamless and responsive interaction between the backend data structures and the frontend
  user interface.

### View Orders feature

#### Proposed Implementation

The proposed View Orders mechanism is facilitated by `ViewOrdersCommand`. It extends `Command` and implements the
displaying of all orders that belong to a client.

These operations are exposed in the `BookKeeperParser` class as `BookKeeperParser#parseCommand()`.

Given below is an example usage scenario and how the view orders mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedBookKeeper` will be initialized with the
initial bookkeeper state, and the `currentStatePointer` pointing to that single bookkeeper state.

Step 2. The user executes `viewOrders` command to view all the orders that they have in BookKeeper. The `viewOrders`
command calls `Model#updateFilteredOrderList()`, causing bookkeeper to show the list of orders
that are tracked in the storage of the application. The `viewOrders` command then returns a new `CommandResult`, which
displays the `MESSAGE_SUCCESS` message, which is "Here are all your orders: ".

#### Design considerations:

**Aspect: How view command executes:**

* **Alternative 1 (current choice):** Retrieves and displays all client orders from the filtered order list.
    * Pros: Simple and straightforward implementation.
    * Cons: May result in a slower performance and higher memory usage if the filtered order list is large.

* **Alternative 2:** Implement system for displaying orders and only load a subset of orders at a time.
    * Pros: Will use less memory (e.g. can use cache mechanisms to store recently accessed orders in memory).
    * Cons: More complex implementation of storage and memory access.

**Why is it implemented that way**

* This approach is chosen for its simplicity. By utilising the filtered order list maintained by the model, the
  `viewOrders` command provides a straightforward way to display all orders to the user. It also makes it easier to
  maintain.

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**: Klara is a florist and owner of Royal Bloom, a thriving floral shop in Singapore.
As her business grows, managing the increasing volume of orders and client information becomes more complex and
time-consuming.

Recently, the demand for personalised and expedited flower arrangements has risen, leading to challenges in keeping up
with client requests and order specifics. This has resulted in Klara needing a more efficient way to manage her shop’s
operations to maintain customer satisfaction and business growth. In response, Klara's florist mentor, Jenna, suggests
implementing a more convenient system to streamline these processes.

Therefore, Jenna has recommended BookKeeper, a desktop application designed to assist florists like Klara. BookKeeper
will help Klara manage her growing list of clients and orders.

Klara is tech-savvy and prefers tools that enhance productivity while streamlining her workflow. She values
desktop applications for their reliability and performance and is accustomed to managing digital tools that save time
and reduce manual efforts.

Klara is a small business owner of a florist shop, specifically a freelance hobbyist in the floral industry. She has
the requirements of:

* Prefers efficient ways to manage their clients and their information.
* Prefers an efficient way to keep track of client's orders.
* Values productivity and time-saving solutions.

(Note: Klara and Jenna are fictional characters, created based on research of the needs of real florist business owners)

**Value proposition**:

* Manage contacts faster than a typical mouse/GUI driven app.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …  | I want to …                                                                                   | So that I can…                                                                                                  |
|----------|---------|-----------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| `* * *`  | Florist | be abe to easily add new clients to my client tracker                                         | keep track of all my clients' information in one place.                                                         |
| `* * *`  | Florist | have a client management system that has a find function                                      | quickly find specific customers when I need to reference their details.                                         |
| `* * *`  | Florist | be able to easily key in commands via a command-line interface                                | efficiently manage my customer list without navigating through complex menus.                                   |
| `* * *`  | Florist | have an application that is cost-effective and easy to use                                    | maximize productivity without investing in expensive CRM systems.                                               |
| `* * *`  | Florist | be able to add a new order to my existing clients                                             | record all of my clients' orders and take note of their respective deadlines.                                   | 
| `* * *`  | Florist | record the received date of each order                                                        | track order history and prioritize tasks effectively.                                                           | 
| `* * *`  | Florist | edit client information                                                                       | keep client details up-to-date.                                                                                 |
| `* * *`  | Florist | automatically sort orders by deadline                                                         | prioritize and manage them efficiently without manual sorting.                                                  |
| `* * *`  | Florist | edit existing orders                                                                          | accommodate changes requested by clients or correct any inaccuracies, ensuring order accuracy and satisfaction. |
| `* * *`  | Florist | delete orders from the system in case of duplicate entries or cancellations                   | maintain accurate records and clutter the order list.                                                           |
| `* * *`  | Florist | delete client from the client list                                                            | keep my client list updated.                                                                                    |
| `* * *`  | Florist | link each order to its corresponding client                                                   | easily access client information associated with each other, facilitating personalized services.                |
| `* *`    | Florist | offer customizable tags or labels for customers                                               | segment my audience and target specific groups with tailored marketing campaigns.                               |
| `* *`    | Florist | track the status of each order                                                                | provide updates to clients and manage expectations.                                                             |
| `* *`    | Florist | assign a price to each order                                                                  | have accurate record-keeping and financial management.                                                          |
| `* *`    | Florist | view a list of all clients along with their contact information and assigned tag in one place | access and reference to client details quickly.                                                                 |
| `*`      | Florist | have a good out of the box experience                                                         | immediately use the application without needing to configure it for my own needs.                               |

## Use cases

(For all use cases below, the **System** is the `BookKeeper` and the **Actor** is the `user`, unless specified
otherwise)

### **Use case: Delete a client**

**MSS**

1. User requests to list clients.
2. BookKeeper shows a list of clients.
3. User requests to delete a specific client in the list.
4. BookKeeper deletes the client.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. BookKeeper shows an error message.

      Use case resumes at step 2.

### **Use case: Add a client**

**MSS**

1. User requests to add a client.
2. BookKeeper adds a client.
3. BookKeeper shows the added client.

   Use case ends.

**Extensions**

* 2a. The client already exists.

    * 2a1. BookKeeper shows an error message.

      Use case resumes at step 1.

* 2b. The client details are invalid.

    * 2b1. Bookkeeper shows an error message.

      Use case resumes at step 1.

### **Use case: Edit a client**

**MSS**

1. User requests to edit a client.
2. BookKeeper shows the client to be edited.
3. User edits the client.
4. BookKeeper shows the edited client.

   Use case ends.

**Extensions**

* 2a. The client does not exist.

    * 2a1. BookKeeper shows an error message.
    * 2a2. BookKeeper shows a list of clients with similar names.

      Use case resumes at step 1.
* 3a. The client details are invalid.

    * 3a1. BookKeeper shows an error message.

      Use case resumes at step 2.
* 3b. The client details are unchanged.

    * 3b1. BookKeeper shows a message indicating no changes are made.

      Use case resumes at step 2.

### **Use case: Find a client**

**MSS**

1. User requests to find a client by name.
2. BookKeeper shows a list of clients whose names contain the given keyword.

   Use case ends.

**Extensions**

* 2a. No client is found.

    * 2a1. BookKeeper shows a message indicating no client is found.
    * 2a2. BookKeeper shows the list of clients with similar names.

  Use case ends.

### **Use case: Show help**

**MSS**

1. User requests to show help.
2. BookKeeper shows a help page.

   Use case ends.

### **Use case: Clear all entries**

**MSS**

1. User requests to clear all entries.
2. BookKeeper clears all entries.

   Use case ends.

### **Use case: Exit the program**

**MSS**

1. User requests to exit the program.
2. BookKeeper exits.

   Use case ends.

### **Use case: Add order**

**MSS**

1. User requests to add an order.
2. BookKeeper adds an order.

   Use case ends.

**Extensions**

* 2a. The order details are invalid.

    * 2a1. BookKeeper shows an error message.

      Use case resumes at step 1.

### **Use case: Edit order**

**MSS**

1. User requests to edit an order.
2. BookKeeper shows the order to be edited.
3. User edits the order.
4. BookKeeper shows the edited order.

   Use case ends.

**Extensions**

* 2a. The order does not exist.

    * 2a1. BookKeeper shows an error message.

      Use case resumes at step 1.
* 3a. The order details are invalid.

    * 3a1. BookKeeper shows an error message.

      Use case resumes at step 2.

### **Use case: Delete order**

**MSS**

1. User requests to delete an order.
2. BookKeeper deletes the order.

   Use case ends.

**Extensions**

* 2a. The order does not exist.

    * 2a1. BookKeeper shows an error message.

      Use case resumes at step 1.

## Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2. Should be able to hold up to 1000 clients' information without a noticeable sluggishness in performance for typical
   usage.
3. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be
   able to accomplish most of the tasks faster using commands than using the mouse.
4. Should provide clear and informative error messages to users in case of unexpected errors. Additionally, detailed
   logs should be maintained for system administrators to troubleshoot issues effectively.
5. Application architecture should be scalable to accommodate future growth in terms of users and data volume, without
   compromising performance.
6. Automated backups of critical data should be performed, and there should be a documented and tested procedure for
   data recovery in case of system failures or data loss.

## Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Application architecture**: Describes the patterns and techniques used to design and build an application
* **System administrators**: Professionals responsible for managing, configuring, and ensuring the proper operation of
  computer systems and servers
* **Detailed logs**: Records that track events, operations, errors, and other significant activities that occur within a
  software system or application.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Ensure you have Java `11` and above installed in your system.
    * You may check if you have Java installed by opening your command prompt or terminal, and typing:  
      `java --version`
        * If Java is installed, you should ensure that it is currently running on version "11.x.xx".
        * ![img_2.png](images/JavaVersionScreenshot.png)
        * If you encounter an error, or if your version does not match our specified requirements, you may visit the
          [Official Oracle website](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html) to
          download the Java JDK required to run this project.

2. Initial launch

    1. Download the latest `bookkeeper.jar` release from [here](https://github.com/AY2324S2-CS2103T-T09-2/tp/releases)
    2. Copy the file to the folder you want to use as the _home folder_ for your BookKeeper.
    3. Open your terminal or command prompt in your system.
    4. `cd` into the folder you put the jar file in, and use the `java -jar bookkeeper.jar`
       command to run the application.<br>
       Expected: A GUI similar to the below should appear in a few seconds.

       Note how the app contains some sample data.<br>
       ![Ui](images/Ui.png)

### Viewing Help

Pre-requisite:

* None

Command: `help`

Expected Output:

* A help window should open up with instructions to
  `Refer to the user guide: https://ay2324s2-cs2103t-t09-2.github.io/tp/UserGuide.html#5-main-features`

Expected Output in the Command Output Box:

* `Opened help window.`

### Clearing BookKeeper

Pre-requisite:

* There is at least one ("1") client and/or order stored in the BookKeeper application.

Command: `clear`

Expected Output:

* All clients and orders will be cleared.

Expected Output in the Command Output Box:

* `BookKeeper has been cleared!`

### Exiting the Program

Pre-requisite:

* There is at least one ("1") client and/or order stored in the BookKeeper application.

Command: `clear`

Expected Output:

* Exits the program.

Expected Output in the Command Output Box:

* `BookKeeper has been cleared!`

### Adding a client

Pre-requisite:

* There should not exist another client with the exact same name (names are case-sensitive) and spacing,
  i.e. the names "Betsy Crowe" and "Betsy crowe" are considered as different names.

Command: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

* Example: `add n/Betsy Crowe e/betsycrowe@example.com a/Beauty World p/1234567 t/VIP`
* Note:
    * Name must be unique, clients with exact same name is not allowed.
    * A client can have any number of tags (including 0).
    * Tags do not accept whitespaces (e.g. “VIP 2” is not accepted, “VIP2” is accepted).
    * Tags only accept 0-9 and a-z.
    * Names only accept 0-9 and a-z and are case-sensitive.
    * Two persons with the same name are not allowed, but two persons with the same name but different cases are
      allowed.
    * Phone number must be numeric and at least 3 numbers. It must not contain spaces “ “, brackets `()` or hyphens
      `-`, plus `+` , or other symbols.
    * Emails must not have consecutive special characters. E.g. “john..doe@example.com” is not accepted.

Expected Output:

* All the client information with their respective fields will be displayed on the left side column.

Expected Output in the Command Output Box:

* `New client added: Betsy Crowe; Phone: 1234567; Email: betsycrowe@example.com; Address: Beauty World; Tags: [VIP]`
* The message should echo and show correctly the information that you have entered for your client.

### Editing a client

Pre-requisite:

* You must know the index of the client you want to edit.
* A client must exist at the index you want to edit at i.e. you cannot edit a client at index 3 if you only have two
  clients in the application.
* There must at least be one client in the application.

Command: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Example: `edit 1 n/Betsy Crower t/`
* This edits the name of the 1st client to be `Betsy Crower` and clears all existing tags.

Expected Output:

* The application will be updated with the edited client information.

Expected Output in the Command Output Box:

* `Edited Client: Betsy Crower; Phone: 1234567; Email: betsycrowe@example.com; Address: Beauty World; Tags: `

### Deleting a client

Pre-requisite:

* You must know the index of the client you want to delete.
* A client must exist at the index you want to delete i.e. you cannot delete a client at index 3 if you only have two
  clients in the application.
* There must at least be one client in the application.

Command: `delete INDEX`

* Examples:
    * `delete 1` deletes the 1st client listed in your application.
    * `find Betsy` followed by `delete 1` deletes the 1st client in the results of the find command.
* Note:
    * This deletes the client at the specified INDEX.
    * The index refers to the index number shown for the respective client in the displayed client list.
    * The index must be a positive integer 1, 2, 3, …​

Expected Output:

* Client is removed from the application.

Expected Output in the Command Output Box:

* `Deleted Client: Betsy Crower; Phone: 1234567; Email: betsycrowe@example.com; Address: Beauty World; Tags:`
* Note: The details of the client should correspond to the client that you have just deleted.

### Listing all clients

Pre-requisite:

* There must at least be one client in the application.

Command: `list`

Expected Output:

* Shows a list of all clients in BookKeeper.

Expected Output in the Command Output Box:

* `Listed all clients`

### Locating clients by name: find

Pre-requisite:

* There must at least be one client in the application.

Command: `find KEYWORD [MORE_KEYWORDS]…`

* Examples:
    * `find alex david` returns `Alex Yeoh`, `David Li`
    * `find John` returns `john` and `John Doe`

Expected Output:

* Finds clients whose names contain any of the given keywords.

Expected Output in the Command Output Box:

* `2 clients listed!`
* Note: the number of clients listed depends on the number of matching clients found by the application.

### Adding an order

Pre-requisite:

* There must at least be one client in the application.
* You must know the index of the client you want to add an order to.

Command: `order INDEX by/DEADLINE c/PRICE d/DESCRIPTION`

* Examples:
    * `order 1 d/1xRoses c/40 by/23-07-2024 00:00`
    * `order 3 by/07-07-2024 00:00 c/88.88 d/99xRoses`
    * `order 1 by/23-05-2024 16:00 c/58.90 d/1xLily`
* Note:
    * Adds an order to the client at the specified `INDEX`. The index must be a positive integer 1, 2, 3, …​,
      and the index must exist in the Client list.
    * All fields must be provided.
    * The order of the fields does not matter (e.g. both `order INDEX by/DEADLINE c/PRICE d/DESCRIPTION` and
      `order INDEX d/DESCRIPTION c/PRICE by/DEADLINE` are acceptable)
    * The status of new orders are automatically set to “PENDING”.
    * Please specify `by/DEADLINE` field in `DD-MM-YYYY HH:MM`.
        * Deadlines can be set to a date before a date before today to mean that an order is overdue. E.g. if your
          order was due yesterday, but you have not completed the order, you can put yesterday’s date.
          This lets you track if you have orders that are overdue.
    * For the `c/PRICE` field, do note that any decimal places after 2 will be rounded.
        * E.g. `2.999` will be rounded up to `3.00`.
    * The order list will be sorted according to their deadline.
      Meaning, if there are two orders, one due on `10-10-2025 10:00` and another due on `10-10-2025 10:30`, the order
      with the deadline of `10-10-2025 10:00` will have an index of `1`, and the other order will have an index of `2`.

Expected Output:

* Adds an order into BookKeeper.

Expected Output in the Command Output Box:

* `New Order added! John Doe`
* Note: The name of the client that you added the order to will appear after `New Order added!`

### Deleting an order

Pre-requisite:

* There must at least be one client and one order in the application.
* You must know the index of the order you want to delete.

Command: `deleteOrder INDEX`

* Example: `deleteOrder 2` deletes the 2nd order in the order list.
* Note:
    * Deletes the order at the specified `INDEX`.
    * The index refers to the index number shown in the displayed order list.
    * The index must be a positive integer 1, 2, 3, …​, and the index must exist in the Client list.

Expected Output:

* Deletes the specified order from BookKeeper.

Expected Output in the Command Output Box:

* `Deleted Order: Deadline: 23-07-2024 00:00; Date Received: 15-04-2024 12:52; Details: 1xRoses`
* Note: The details of the deleted order in the command output box will correspond to the details of the order deleted
  at the index.

### Editing an order

Pre-requisite:

* There must at least be one client and one order in the application.
* You must know the index of the order you want to edit.

Command: `editOrder INDEX [by/DEADLINE] [c/PRICE] [d/DESCRIPTION] [s/STATUS]`

* Examples:
    * `editOrder 1 by/05-05-2024 16:00 c/58.90 d/1xRoses s/PENDING` edits the Deadline and
      Description of the 1st Order list.
    * `editOrder 1 s/COMPLETED` edits 1st order status to “COMPLETED”.
* Note:
    * Edits the order at the specified `INDEX`.
      The index must be a positive integer 1, 2, 3, …​ and the index must exist in the Order list.
    * Command can work without any optional fields provided.
    * Existing values will be updated to the input values.
    * There are 3 possible statuses (they are all case-insensitive):
        * PENDING: All orders are automatically set to PENDING
        * COMPLETED: When the order is delivered successfully
        * CANCELED: When the order is canceled

Expected Output:

* Edits an existing order in BookKeeper.

Expected Output in the Command Output Box:

* `Edited Order: Deadline: 23-07-2024 00:33; Date Received: 15-04-2024 12:52; Details: 1xRoses`
* Note: The details of the edited order will correspond to the details specified in the command.

## **Appendix: Planned Future Enhancements (Beyond v1.4)**

Team size: 4

1. Enhanced Error Messaging

**Current**: Our edit command function's error messages for both client and order are too general, not specifying
the exact issue with the input command causing the error. They address potential errors rather than pinpointing the
specific one the user is facing.

**Future**: We plan to implement more specific error messages tailored to the respective errors encountered. This will
offer clearer guidance to users, helping them identify and resolve issues promptly.

2. Enhanced Error Messaging for Wrong Indices with Missing Fields

**Current**: Currently our error messages for wrong indices and missing fields in the edit command lack
specificity. We provide a generic message like "at least one field to edit must be provided," without indicating which
field is missing or if there is an issue with the index.

**Future**: We plan to refine these error messages to explicitly highlight the missing field or incorrect index,
enabling users to identify the problem immediately and take appropriate action.

3. Extended Tag Length and Error Refinement

**Current**: Our application now imposes limitations on tag length, restricting users from inputting longer tags,
which may hinder organization and labeling efforts.

**Future**: To address this, we aim to increase the maximum length of tags supported within the system, enabling users
to provide more descriptive labels and organize content effectively.

4. Resolution Support

**Current**: Our application is currently only optimised for the recommended screen resolution and screen scale
for your device. This may limit compatibility with different user preferences. It is usable but not optimal for a small
margin of error from your recommended screen resolutions and scale.

**Future**: We plan to expand our support for various screen resolutions and screen scales by including custom scaling,
which will enhance accessibility and improve the user experience.

5. Allow filtering of orders based on displayed customers.

**Current**: Our application now lacks the functionality to filter orders based on displayed clients,
which may lead to inefficiencies for users managing multiple clients.

**Future**: We plan to enable users to filter orders based on the displayed client, streamlining the experience
and improving efficiency for users managing multiple clients simultaneously.

6. Allow adding of multiple users with the same name.

**Current**: Currently, our application rejects the addition of multiple clients with the same name
unless they differ in case sensitivity (e.g., Jane Low and Jane low are acceptable).

**Future**: We intend to enhance our system to allow the addition of multiple clients with identical names,
recognizing that it is common for clients to share the same name. This improvement will enable users to manage
multiple clients with similar names more effectively.

7. Relax constraints on field data types

**Current**: Our application lacks constraints on the data types of input fields, allowing excessively long names
and unrealistically large values for certain fields like order price.

**Future**: We plan to implement constraints on field data types to prevent issues such as overly long fields
not displaying correctly and unrealistic values being entered. For example, we will limit the length of names and
constrain the size of numerical values like order prices to ensure data integrity and usability.

* E.g. do not input a name that is too long, as it may not be displayed correctly.
* E.g. do not input an Order Price that is unrealistically large for flower orders e.g. 9 billion (
  9,000,000,000).

## **Appendix: Effort**

Our project aims to develop a comprehensive solution tailored for florists to manage both client information and order
delivery tracking seamlessly. Unlike AB3, which focuses only on the person or client entity, our project extended its
scope to include orders, thus introducing complexity by requiring the seamless linking between client and order
functionalities.

### Challenges Faced

1. **Bidirectional Navigation** - Implementing bidirectional navigation between orders and clients posed a significant
   challenge. Ensuring that
   modifications to one entity reflected accurately in the other required meticulous attention to detail.
2. **Linking Orders to Clients** - Establishing a robust linkage between orders and clients presented difficulties,
   especially during operations
   like delete or edit. Coordinating changes on both client and order sides to maintain data correctness demanded
   careful
   planning and execution.
3. **Collaborative Development** - Each team member had to work on features and functions independently, necessitating
   close
   collaboration to ensure seamless integration. Communication was important to minimize merging conflicts and ensure
   that
   individual components linked are successfully done.

### Effort Required

The project demanded a considerable effort due to its expanded scope and the complexities associated with managing
multiple entity types. Coordinating development efforts, addressing integration challenges, and ensuring feature
completeness required dedicated time and resources.

### Achievements:

Despite the challenges, we successfully delivered a robust product that allow florists to manage client information
seamlessly while effectively tracking order deliveries.

Key achievements include:

* Implementing bidirectional navigation between client and order entities.
* Establishing robust linkages between orders and clients, ensuring data consistency throughout.
* Facilitating collaborative development and integration efforts to deliver a cohesive solution.

















