package com.mocab;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mattias on 2015-04-20.
 */
public class Couple {

    private static final String SPLIT_PATTERN = ",";

    private final String name;
    private final String address;
    private String allergies;
    private Dish assignedDish;
    private List<Dish> excludedDishes = new ArrayList<>();

    public Couple(List<String> properties) {
        name = properties.get(2) + " och " + properties.get(3);
        address = properties.get(5);
        if (properties.size() > 6) {
            allergies = properties.get(6);
        }
        if (properties.size() > 7) {
            excludedDishes = Stream.of(properties.get(7).split(SPLIT_PATTERN)).map(String::trim).map(Dish::valueOf).collect(Collectors.toList());
        }
    }

    public boolean decided() {
        return excludedDishes.size() == 2;
    }

    public Dish getDish() {
        if (assignedDish != null) {
            return assignedDish;
        } else if (decided()) {
            return Arrays.stream(Dish.values()).filter(dish -> !excludedDishes.contains(dish)).findFirst().orElseThrow();
        }
        return null;
    }

    public void setDish(Dish dish) {
        this.assignedDish = dish;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getAllergies() {
        return allergies;
    }

    @Override
    public String toString() {
        return name;
    }
}
