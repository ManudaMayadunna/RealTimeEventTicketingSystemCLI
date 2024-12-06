package Models;

import java.util.logging.Logger;

public class Vendor implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("TicketSystem");
    private static int globalTicketIdCounter = 1; // Shared among all vendors
    private final TicketPool ticketPool;
    private final int ticketReleaseRate; // Time in seconds to release a ticket

    public Vendor(TicketPool ticketPool, int ticketReleaseRate) {
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = ticketReleaseRate;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int ticketId;
                synchronized (Vendor.class) {
                    if (globalTicketIdCounter > 30) { // Stop adding tickets after reaching the limit
                        LOGGER.info(Thread.currentThread().getName() + " finished releasing all tickets.");
                        break;
                    }
                    ticketId = globalTicketIdCounter++;
                }

                // Create and add a new ticket to the pool
                TicketPool.Ticket ticket = new TicketPool.Ticket(ticketId);
                ticketPool.addTicket(ticket);
                LOGGER.info(Thread.currentThread().getName() + " added ticket: " + ticket);

                // Simulate time taken to release a ticket
                Thread.sleep(ticketReleaseRate * 1000);

            } catch (InterruptedException e) {
                LOGGER.warning(Thread.currentThread().getName() + " interrupted while adding tickets.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}