package com.mynet.shared.response;

public class UpdateTicketResponse {
    public enum UpdateTicketReason {
        DEFAULT(0),
        PURCHASE(1);
        private final int value;
        public int getValue() { return value;}

        UpdateTicketReason(int value) {
            this.value = value;
        }
    }

    private long tickets;
    private UpdateTicketReason reason;

    public UpdateTicketResponse(long tickets, UpdateTicketReason reason) {
        this.tickets = tickets;
        this.reason = reason;
    }
}
