package Models;

import java.util.logging.Logger;

public class Customer implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("TicketSystem");
    private final TicketPool ticketPool;
    private final int customerRetrievalRate; // Time in seconds to buy a ticket
    private final int numberOfTicketsToBuy; // Total tickets this customer aims to buy

    public Customer(TicketPool ticketPool, int customerRetrievalRate, int numberOfTicketsToBuy) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.numberOfTicketsToBuy = numberOfTicketsToBuy;
    }

    @Override
    public void run() {
        int ticketsBought = 0;

        while (ticketsBought < numberOfTicketsToBuy) {
            try {
                // Attempt to buy a ticket
                TicketPool.Ticket ticket = ticketPool.buyTicket();
                ticketsBought++;
                LOGGER.info(Thread.currentThread().getName() + " bought ticket: " + ticket);

                // Simulate time taken to buy a ticket
                Thread.sleep(customerRetrievalRate * 1000);

            } catch (InterruptedException e) {
                LOGGER.warning(Thread.currentThread().getName() + " interrupted while buying tickets.");
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Log when a customer has finished buying their tickets
        LOGGER.info(Thread.currentThread().getName() + " finished buying " + ticketsBought + " tickets.");
    }
}