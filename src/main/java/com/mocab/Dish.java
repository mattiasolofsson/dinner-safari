package com.mocab;

/**
 * Created by mattias on 2015-04-20.
 */
public enum Dish {

    STARTER("Förrätt"),
    MAIN("Huvudrätt"),
    DESSERT("Dessert");

    private final String text;

    private Dish(String text) {
        this.text = text;
    }

    public static Dish dishForOrdinal(int ordinal) {
        for (Dish dish : values()) {
            if (dish.ordinal() == ordinal) {
                return dish;
            }
        }
        return null;
    }

    public static Dish next(Dish dish) {
        return dishForOrdinal(dish.ordinal() + 1);
    }

    public static Dish circularNext(Dish dish) {
        return dishForOrdinal((dish.ordinal() + 1) % Dish.values().length);
    }

    public String getText() {
        return text;
    }

    public int offset() {
        return this.equals(Dish.DESSERT) ? 1 : 0;
    }
}
