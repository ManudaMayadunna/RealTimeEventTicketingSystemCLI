package Models;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class TicketPool {
    private final int maximumTicketCapacity; // Maximum number of tickets the pool can hold
    private final List<Ticket> ticketList = new LinkedList<>();
    private static final Logger LOGGER = Logger.getLogger("TicketSystem");
    private int nextTicketId = 1;

    public TicketPool(int maximumTicketCapacity) {
        this.maximumTicketCapacity = maximumTicketCapacity;
    }

    public synchronized void addTicket(Ticket ticket) throws InterruptedException {
        while (ticketList.size() >= maximumTicketCapacity || ticket.getTicketId() != nextTicketId) {
            LOGGER.info(Thread.currentThread().getName() + " waiting to add ticket: " + ticket);
            wait(); // Vendors wait if the pool is full or the ticket ID is not the next expected one
        }

        // Add ticket to the pool
        ticketList.add(ticket);
        nextTicketId++;
        LOGGER.info("Ticket added: " + ticket + " | Pool size: " + ticketList.size());

        // Notify all waiting threads (customers and vendors)
        notifyAll();
    }

    public synchronized Ticket buyTicket() throws InterruptedException {
        while (ticketList.isEmpty()) {
            LOGGER.info(Thread.currentThread().getName() + " waiting to buy a ticket.");
            wait(); // Customers wait if the pool is empty
        }

        // Remove a ticket from the pool
        Ticket ticket = ticketList.remove(0);
        LOGGER.info("Ticket bought: " + ticket + " | Pool size: " + ticketList.size());

        // Notify all waiting threads (vendors and customers)
        notifyAll();
        return ticket;
    }

    public synchronized int getCurrentSize() {
        return ticketList.size();
    }

    public static class Ticket {
        private final int ticketId;

        public Ticket(int ticketId) {
            this.ticketId = ticketId;
        }

        public int getTicketId() {
            return ticketId;
        }

        @Override
        public String toString() {
            return "Ticket ID: " + ticketId;
        }
    }
}