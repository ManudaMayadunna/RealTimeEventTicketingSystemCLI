package Controllers;

import Models.Customer;
import Models.TicketPool;
import Models.Vendor;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static final Logger LOGGER = Logger.getLogger("RealTimeTicketingSystem");
    private static Thread vendor1Thread;
    private static Thread vendor2Thread;
    private static Thread customer1Thread;
    private static Thread customer2Thread;
    private static Thread customer3Thread;
    private static final Object lock = new Object();
    private static boolean continueRunning = true;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LOGGER.info("Real-Time Event Ticketing System Started");
        System.out.println();
        System.out.println("===========Welcome to Manuda's Real-Time Event Ticketing System===========");
        System.out.println();

        Configuration config = loadConfiguration(scanner);

        // Start the system immediately
        LOGGER.info("Starting the system...");
        startSystem(config);

        while (continueRunning) {
            // Main thread can perform other tasks or simply wait
            try {
                Thread.sleep(1000); // Adjust as needed
            } catch (InterruptedException e) {
                LOGGER.warning("Main thread interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        scanner.close();
    }

    private static Configuration loadConfiguration(Scanner scanner) {
        while (true) {
            System.out.print("Do you want to load an existing configuration from .ser file (yes/no)? ");
            String loadConfig = scanner.nextLine().trim().toLowerCase();
            if (loadConfig.equals("yes")) {
                LOGGER.info("Attempting to load configuration from .ser file...");
                Configuration config = Configuration.loadConfigurationFromSer("Configuration.ser");
                if (config == null) {
                    LOGGER.severe("Failed to load configuration. Exiting...");
                    System.exit(1);
                }
                return config;
            }

            if (loadConfig.equals("no")) {
                Configuration config = Configuration.configureFromCLI();
                LOGGER.info("Configuration saved to .ser file: Configuration.ser");
                LOGGER.info("Configuration saved to JSON file: Configuration.json");
                LOGGER.info("Configuration saved to text file: Configuration.txt" + "\n");
                System.out.println();

                return config;
            }
            LOGGER.warning("Invalid input. Please enter 'yes' or 'no'.");
        }
    }

    private static void startSystem(Configuration config) {
        TicketPool ticketPool = new TicketPool(config.getMaxTicketCapacity());
        Vendor vendor1 = new Vendor(ticketPool, config.getTicketReleaseRate());
        Vendor vendor2 = new Vendor(ticketPool, config.getTicketReleaseRate());

        // Three customers with their ticket-buying quotas
        Customer customer1 = new Customer(ticketPool, config.getCustomerRetrievalRate(), config.getTotalTickets() / 3);
        Customer customer2 = new Customer(ticketPool, config.getCustomerRetrievalRate(), config.getTotalTickets() / 3);
        Customer customer3 = new Customer(ticketPool, config.getCustomerRetrievalRate(), config.getTotalTickets() / 3);

        vendor1Thread = new Thread(vendor1, "Vendor1");
        vendor2Thread = new Thread(vendor2, "Vendor2");

        customer1Thread = new Thread(customer1, "Customer1");
        customer2Thread = new Thread(customer2, "Customer2");
        customer3Thread = new Thread(customer3, "Customer3");

        // Start vendor threads
        vendor1Thread.start();
        vendor2Thread.start();

        // Start customer threads
        customer1Thread.start();
        customer2Thread.start();
        customer3Thread.start();
    }

    private static void stopSystem() {
        if (vendor1Thread != null) {
            vendor1Thread.interrupt();
        }

        if (vendor2Thread != null) {
            vendor2Thread.interrupt();
        }

        if (customer1Thread != null) {
            customer1Thread.interrupt();
        }

        if (customer2Thread != null) {
            customer2Thread.interrupt();
        }

        if (customer3Thread != null) {
            customer3Thread.interrupt();
        }

        try {
            if (vendor1Thread != null) {
                vendor1Thread.join();
            }

            if (vendor2Thread != null) {
                vendor2Thread.join();
            }

            if (customer1Thread != null) {
                customer1Thread.join();
            }

            if (customer2Thread != null) {
                customer2Thread.join();
            }

            if (customer3Thread != null) {
                customer3Thread.join();
            }
        } catch (InterruptedException e) {
            LOGGER.warning("Thread interrupted during shutdown.");
        }
    }

    static {
        try {
            FileHandler fileHandler = new FileHandler("system.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(true);
        } catch (IOException e) {
            System.err.println("Failed to configure logger: " + e.getMessage());
        }
    }
}