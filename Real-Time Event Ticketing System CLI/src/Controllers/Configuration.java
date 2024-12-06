package Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Logger;

public class Configuration implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());

    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;

    // Default constructor
    public Configuration() {}

    // Serialization method for .ser files
    public void saveConfigurationToSer(String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this);
            LOGGER.info("Configuration saved to .ser file: " + filename);
        } catch (IOException e) {
            LOGGER.severe("Error saving configuration to .ser file: " + e.getMessage());
        }
    }

    public static Configuration loadConfigurationFromSer(String filename) {
        try (FileInputStream fis = new FileInputStream(filename);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Configuration config = (Configuration) ois.readObject();
            LOGGER.info("Configuration loaded from .ser file: " + filename);
            return config;
        } catch (FileNotFoundException e) {
            LOGGER.warning("Serialization configuration file not found: " + filename);
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.severe("Error reading .ser configuration file: " + e.getMessage());
        }
        return null;
    }

    // Save configuration to JSON
    public void saveConfigurationToJson(String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(this, writer);
            writer.flush();
            LOGGER.info("Configuration saved to JSON file: " + filename);
        } catch (IOException e) {
            LOGGER.severe("Error saving configuration to JSON file: " + e.getMessage());
        }
    }

    public static Configuration loadConfigurationFromJson(String filename) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filename)) {
            Configuration config = gson.fromJson(reader, Configuration.class);
            LOGGER.info("Configuration loaded from JSON file: " + filename);
            return config;
        } catch (FileNotFoundException e) {
            LOGGER.warning("JSON configuration file not found: " + filename);
        } catch (IOException e) {
            LOGGER.severe("Error reading JSON configuration file: " + e.getMessage());
        }
        return null;
    }

    // Save configuration to text file
    public void saveConfigurationToTextFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Total Tickets= " + totalTickets);
            writer.println("Ticket Release Rate= " + ticketReleaseRate + "sec");
            writer.println("Customer Retrieval Rate= " + customerRetrievalRate + "sec");
            writer.println("Max Ticket Capacity= " + maxTicketCapacity);
        } catch (IOException e) {
            System.err.println("Error saving configuration to text file: " + e.getMessage());
        }
    }

    public static Configuration loadConfigurationFromTextFile(String filename) {
        Configuration config = new Configuration();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String[] setting = scanner.nextLine().split("=");
                switch (setting[0]) {
                    case "totalTickets":
                        config.setTotalTickets(Integer.parseInt(setting[1]));
                        break;
                    case "ticketReleaseRate":
                        config.setTicketReleaseRate(Integer.parseInt(setting[1]));
                        break;
                    case "customerRetrievalRate":
                        config.setCustomerRetrievalRate(Integer.parseInt(setting[1]));
                        break;
                    case "maxTicketCapacity":
                        config.setMaxTicketCapacity(Integer.parseInt(setting[1]));
                        break;
                    default:
                        System.err.println("Unknown configuration key: " + setting[0]);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Text configuration file not found: " + filename);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in configuration file: " + e.getMessage());
        }
        return config;
    }

    // CLI Configuration
    public static Configuration configureFromCLI() {
        Scanner scanner = new Scanner(System.in);
        Configuration config = new Configuration();

        config.setTotalTickets(getValidatedInput(scanner, "Enter total number of tickets: "));
        config.setTicketReleaseRate(getValidatedInput(scanner, "Enter ticket release rate(per sec): "));
        config.setCustomerRetrievalRate(getValidatedInput(scanner, "Enter customer retrieval rate(per sec): "));
        config.setMaxTicketCapacity(getValidatedInput(scanner, "Enter maximum ticket capacity: "));

        // Save configuration to different formats
        config.saveConfigurationToSer("Configuration.ser");
        config.saveConfigurationToJson("Configuration.json");
        config.saveConfigurationToTextFile("Configuration.txt");

        return config;
    }

    private static int getValidatedInput(Scanner scanner, String prompt) {
        int value = -1;
        while (value <= 0) {
            try {
                System.out.print(prompt);
                value = Integer.parseInt(scanner.nextLine());
                if (value <= 0) {
                    System.err.println("Invalid input. Please enter a positive integer.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a positive integer.");
            }
        }
        return value;
    }

    // Getters and Setters
    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(int ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }
}
