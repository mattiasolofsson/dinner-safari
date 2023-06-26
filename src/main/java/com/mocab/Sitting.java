package com.mocab;

import java.util.List;

public class Sitting {

    private final Couple host;
    private final List<Couple> guests;

    public Sitting(Couple host, List<Couple> guests) {
        this.host = host;
        this.guests = guests;
    }

    public Couple getHost() {
        return host;
    }

    public List<Couple> getGuests() {
        return guests;
    }

    public boolean has(Couple couple) {
        return couple.equals(host) || guests.contains(couple);
    }
}
