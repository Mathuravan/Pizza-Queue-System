import java.util.Scanner;
import java.util.Arrays;


public class SnackFood {

    private static final int Max_Snack_Cashier_1 = 2;
    private static final int Max_Snack_Cashier_2 = 3;
    private static final int Max_Snack_Cashier_3 = 5;
    private static final int Pizza_Price = 1350;
    private static final SnackQueue[] queues = new SnackQueue[3];
    private static int stock = 100;
    private static final SnackQueue waitingList = new SnackQueue(5);

    public static void main(String[] args) {
        initializeQueues();

        Scanner function = new Scanner(System.in);

        String option;
        do {
            displayMenu();
            option = function.next().toUpperCase();
            function.nextLine();
            switch (option) {
                case "100", "VFQ" -> ViewAllQueues();
                case "101", "VEQ" -> ViewAllEmptyQueues();
                case "102", "ACQ" -> AddCustomerToQueue(function);
                case "103", "RCQ" -> RemoveCustomerFromQueue(function);
                case "104", "PCQ" -> RemoveServedCustomer();
                case "105", "VCS" -> ViewCustomersSortedAlphabetically();
                case "108", "STK" -> ViewRemainingPizzaStock();
                case "109", "AFS" -> AddPizzaToStock(function);
                case "110", "IFQ" -> DisplayIncomeOfEachQueue();
                case "999", "EXT" -> System.out.println("Exiting the program...");
                default -> System.out.println("Invalid option. Please try again.");
            }
        } while (!option.equals("999") && !option.equals("EXT"));

        function.close();
    }

    private static void initializeQueues() {
        queues[0] = new SnackQueue(Max_Snack_Cashier_1);
        queues[1] = new SnackQueue(Max_Snack_Cashier_2);
        queues[2] = new SnackQueue(Max_Snack_Cashier_3);
    }

    private static void displayMenu() {
        System.out.println("\nFoodies Fave Food Center");
        System.out.println("Menu Options:");
        System.out.println("(100 or VFQ) = View all Queues");
        System.out.println("(101 or VEQ) = View all Empty Queues");
        System.out.println("(102 or ACQ) = Add customer to a Queue");
        System.out.println("(103 or RCQ) = Remove a customer from a Queue (From a specific location)");
        System.out.println("(104 or PCQ) = Remove a served customer");
        System.out.println("(105 or VCS) = View Customers Sorted in alphabetical order(Do not use library sort routine");
        System.out.println("(108 or STK) = View Remaining pizza Stock");
        System.out.println("(109 or AFS) = Add pizza to Stock");
        System.out.println("(110 or IFQ) = You can take the price of a pizza as LKR 1350");
        System.out.println("(999 or EXT) = Exit the Program");
        System.out.print("Enter your option: ");
    }

    private static void ViewAllQueues() {
        System.out.println("*************** Cashiers ***************");

        for (int i = 0; i < queues.length; i++) {
            System.out.printf("Cashier %d: ", (i + 1));

            if (queues[i].isEmpty()) {
                System.out.println("Empty");
            } else {
                int queueSize = queues[i].getSize();
                int capacity = queues[i].getCapacity();

                for (int j = 0; j < capacity; j++) {
                    if (j < queueSize) {
                        System.out.print("X ");
                    } else {
                        System.out.print("O ");
                    }
                }
                System.out.println();
            }
        }
    }

    private static void ViewAllEmptyQueues() {
        System.out.println("\nEmpty Queues:");
        for (int i = 0; i < queues.length; i++) {
            if (queues[i].isEmpty()) {
                System.out.println("Cashier " + (i + 1) + ": Empty");
            }
        }
    }


    private static void AddCustomerToQueue(Scanner scanner ) {
        System.out.print("Enter the cashier number (1, 2, or 3): ");
        int queueNumber = scanner.nextInt();
        scanner.nextLine();

        if (queueNumber >= 1 && queueNumber <= 3) {
            SnackQueue queue = queues[queueNumber - 1];
            if (queue.isFull()) {
                System.out.print("Enter the customer First name: ");
                String firstName = scanner.nextLine();

                System.out.print("Enter the customer Last name: ");
                String lastName = scanner.nextLine();
                System.out.print("Enter the  Number of Pizza required: ");
                int PizzaRequired = scanner.nextInt();
                scanner.nextLine();

                if (PizzaRequired > stock) {
                    System.out.println("Not enough Pizza in stock. Customer could not be added.");
                    return;
                }

                Customer customer = new Customer(firstName, lastName, PizzaRequired);
                queue.enqueue(customer);
                updateStock(-PizzaRequired);

                System.out.println("Customer added to Cashier Queue " + queueNumber + ": " + customer);
            } else {
                System.out.println("Selected cashier queue is full. Customer could not be added.");
                System.out.println("Adding customer to the Waiting List...");
                AddToWaitingList(scanner);
            }
        } else {
            System.out.println("Invalid queue number.");
        }
    }

    private static void AddToWaitingList(Scanner Queue) {
        if (waitingList.isFull()) {
            System.out.print("Enter customer first name: ");
            String firstName = Queue.nextLine();
            System.out.print("Enter customer last name: ");
            String lastName = Queue.nextLine();
            System.out.print("Enter number of Pizza required: ");
            int PizzaRequired = Queue.nextInt();
            Queue.nextLine();

            if (PizzaRequired > stock) {
                System.out.println("Not enough Pizza in stock. Customer could not be added to the Waiting List.");
                return;
            }

            Customer list = new Customer(firstName, lastName, PizzaRequired);
            waitingList.enqueue(list);
            updateStock(-PizzaRequired);

            System.out.println("Customer added to the Waiting List: " + list);
        } else {
            System.out.println("Waiting List is full. Customer could not be added.");
        }
    }


    private static void RemoveCustomerFromQueue(Scanner remove) {
        System.out.print("Enter the cashier number (1, 2, or 3): ");
        int queueNumber = remove.nextInt();
        remove.nextLine();
        int queueIndex = queueNumber - 1;

        if (queueIndex >= 0 && queueIndex < queues.length) {
            SnackQueue selectedQueue = queues[queueIndex];

            if (!selectedQueue.isEmpty()) {
                System.out.print("Enter the customer index to remove (0 to " + (selectedQueue.getSize() - 1) + "): ");
                int customerIndex = remove.nextInt();
                remove.nextLine();

                if (customerIndex >= 0 && customerIndex < selectedQueue.getSize()) {
                    Customer customer = selectedQueue.dequeue(customerIndex);
                    updateStock(customer.getPizzaRequired());

                    System.out.println("Customer removed from Cashier Queue " + queueNumber + ": " + customer);
                } else {
                    System.out.println("Invalid customer index.");
                }
            } else {
                System.out.println("The cashier queue is empty.");
            }
        } else {
            System.out.println("Invalid cashier number.");
        }
    }

    private static void RemoveServedCustomer() {
        for (int i = 0; i < queues.length; i++) {
            SnackQueue selectedQueue = queues[i];
            if (!selectedQueue.isEmpty()) {
                Customer customer = selectedQueue.dequeue(0);
                updateStock(customer.getPizzaRequired());

                if (!waitingList.isEmpty()) {
                    Customer nextCustomer = waitingList.dequeue();
                    selectedQueue.enqueue(nextCustomer);

                    System.out.println("Customer removed from Cashier " + (i + 1) + ": " + customer);
                    System.out.println("Next customer in the waiting list added to Cashier " + (i + 1) + ": " + nextCustomer);
                } else {
                    System.out.println("Customer removed from Cashier " + (i + 1) + ": " + customer);
                }

                return;
            }
        }

        System.out.println("No served customers in any cashier's queue.");
    }

    private static void ViewCustomersSortedAlphabetically() {
        System.out.println("Customers Sorted in alphabetical order:");

        for (int i = 0; i < queues.length; i++) {
            System.out.println("Cashier " + (i + 1) + ":");
            queues[i].displaySortedQueue();
        }
    }


    private static void ViewRemainingPizzaStock() {
        System.out.println("\nRemaining Pizza in stock: " + stock);
    }

    private static void AddPizzaToStock(Scanner quantity) {
        System.out.print("Enter the number of Pizza to add to the stock: ");
        int pizzaToAdd = quantity.nextInt();
        quantity.nextLine();
        updateStock(pizzaToAdd);
        System.out.println(pizzaToAdd + " Pizza added to the stock.");
    }

    private static void DisplayIncomeOfEachQueue() {
        System.out.println("\nIncome of each queue (in LKR):");
        for (int i = 0; i < queues.length; i++) {
            SnackQueue queue = queues[i];
            int income = calculateQueueIncome(queue);
            System.out.println("Queue " + (i + 1) + ": LKR " + income);
        }
    }

    private static int calculateQueueIncome(SnackQueue queue) {
        int totalIncome = 0;
        Customer[] customers = queue.getSortedCustomers(); // Assuming you have a method to get sorted customers
        for (Customer customer : customers) {
            totalIncome += customer.getPizzaRequired() * Pizza_Price;
        }
        return totalIncome;
    }
    private static void updateStock(int quantity) {
        stock += quantity;
        if (stock <= 20) {
            System.out.println("Warning: The stock has reached a low level: " + stock + "Pizza");
        }
    }
}

class SnackQueue {
    private final Customer[] queue;
    private int size;
    private final int capacity;

    public SnackQueue(int capacity) {
        this.queue = new Customer[capacity];
        this.size = 0;
        this.capacity = capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {return size != capacity;}

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public Customer getCustomer(int index) {
        return queue[index];
    }

    public void enqueue(Customer add) {
        if (size < capacity) {
            queue[size] = add;
            size++;
        }
    }

    public void displaySortedQueue() {
        Customer[] sortedCustomers = getSortedCustomers();
        for (Customer customer : sortedCustomers) {
            if (customer != null) {
                System.out.println(customer);
            }
        }
    }

    public Customer[] getSortedCustomers() {
        Customer[] Array = new Customer[getSize()];
        for (int i = 0; i < getSize(); i++) {
            Array[i] = getCustomer(i);
        }
        sortCustomersByName(Array);
        return Array;
    }

    public void sortCustomersByName(Customer[] list) {
        for (int i = 0; i < list.length - 1; i++) {
            for (int j = 0; j < list.length - i - 1; j++) {
                if (compareCustomers(list[j], list[j + 1]) > 0) {
                    Customer temp = list[j];
                    list[j] = list[j + 1];
                    list[j + 1] = temp;
                }
            }
        }
    }

    private static int compareCustomers(Customer buyer1, Customer buyer2) {
        int lastNameComparison = buyer1.getLastName().compareTo(buyer2.getLastName());
        if (lastNameComparison != 0) {
            return lastNameComparison;
        }
        return buyer1.getFirstName().compareTo(buyer2.getFirstName());
    }


    public Customer dequeue() {
        if (size > 0) {
            Customer customer = queue[0];
            shiftQueueLeft();
            return customer;
        }
        return null;
    }

    public Customer dequeue(int index) {
        if (index >= 0 && index < size) {
            Customer customer = queue[index];
            shiftQueueLeft(index);
            return customer;
        }
        return null;
    }

    private void shiftQueueLeft() {
        for (int i = 0; i < size - 1; i++) {
            queue[i] = queue[i + 1];
        }
        queue[size - 1] = null;
        size--;
    }

    private void shiftQueueLeft(int startIndex) {
        for (int i = startIndex; i < size - 1; i++) {
            queue[i] = queue[i + 1];
        }
        queue[size - 1] = null;
        size--;
    }
}

class Customer implements Comparable<Customer> {
    private final String firstName;
    private final String lastName;
    private final int PizzaRequired;

    public Customer(String firstName, String lastName, int PizzaRequired) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.PizzaRequired = PizzaRequired;
    }

    public String getFirstName() {return firstName;}

    public String getLastName() {
        return lastName;
    }

    public int getPizzaRequired() {
        return PizzaRequired;
    }

    @Override
    public int compareTo(Customer other) {
        int lastNameComparison = this.lastName.compareTo(other.lastName);
        if (lastNameComparison != 0) {
            return lastNameComparison;
        }
        return this.firstName.compareTo(other.firstName);
    }

    @Override
    public String toString() {
        return (firstName + " " + lastName + " " + PizzaRequired + " Pizza");
    }
}