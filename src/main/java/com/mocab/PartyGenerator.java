package com.mocab;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by mattias on 2015-04-20.
 */
@Component
public class PartyGenerator {

    @Value("${service.date}")
    private String date;

    @Value("${service.contact.name}")
    private String contactName;

    @Value("${service.contact.phone}")
    private String contactPhone;

    private final Map<Dish, List<Couple>> assignedDishes = new HashMap<>();
    private final Map<Dish, Integer> foodRepository = new HashMap<>();
    private final Map<Dish, List<Sitting>> sittingsMap = new HashMap<>();

    public void doTheDance(List<Couple> couples) {
        setupFoodRepository(couples.size() / 3);

        // Check for decided ones
        Iterator<Couple> iterator = couples.iterator();
        while (iterator.hasNext()) {
            Couple couple = iterator.next();
            if (couple.decided()) {
                Dish dish = couple.getDish();
                List<Couple> couplesForDish = assignedDishes.get(dish);
                if (couplesForDish == null) {
                    couplesForDish = new ArrayList<>();
                }
                couplesForDish.add(couple);
                assignedDishes.put(dish, couplesForDish);
                iterator.remove();
                decreaseFoodRepo(dish);
            }
        }

        Collections.shuffle(couples);
        for (Couple couple : couples) {
            assignDish(couple);
        }
        printResult();
    }

    private void decreaseFoodRepo(Dish dish) {
        foodRepository.put(dish, foodRepository.get(dish) - 1);
    }

    private void setupFoodRepository(int dishCount) {
        for (Dish dish : Dish.values()) {
            foodRepository.put(dish, dishCount);
        }
    }

    private void assignDish(Couple couple) {
        Dish dish = getNextDish();
        List<Couple> couplesForDish = assignedDishes.get(dish);
        if (couplesForDish == null) {
            couplesForDish = new ArrayList<>();
        }
        couple.setDish(dish);
        couplesForDish.add(couple);
        assignedDishes.put(dish, couplesForDish);
    }

    private Dish getNextDish() {
        for (Dish dish : foodRepository.keySet()) {
            int available = foodRepository.get(dish);
            if (available > 0) {
                decreaseFoodRepo(dish);
                return dish;
            }
        }
        throw new IllegalStateException("Out of dishes!");
    }

    private void printResult() {
        int dishIndex = 0;
        List<Dish> dishes = new ArrayList<>(assignedDishes.keySet());
        Collections.sort(dishes);
        System.out.println();
        System.out.println("=== RESULTAT ===========================");
        for (Dish dish : dishes) {
            List<Couple> hosts = assignedDishes.get(dish);
            int hostIndex = 0;
            for (Couple host : hosts) {
                List<Couple> guests = getGuests(dish, dishIndex, hostIndex++);
                System.out.println(host.getDish() + " - " + host.getName() + " Gäster: " + guests);
                addSitting(dish, host, guests);
            }
            dishIndex++;
        }
        System.out.println();
        System.out.println("=== NI SKA LAGA ========================");
        printDishAssignment();
        System.out.println();
        System.out.println("=== FÖRRÄTTER ==========================");
        printLocationForStarter();
        System.out.println();
        printNextLocationForCouplesBySitting(Dish.STARTER);
        System.out.println();
        printNextLocationForCouplesBySitting(Dish.MAIN);
    }

    private List<Couple> getGuests(Dish selectedDish, int dishIndex, int hostIndex) {
        List<Couple> guests = new ArrayList<>();

        Dish dishForFirstGuest = Dish.circularNext(selectedDish);
        List<Couple> couples = assignedDishes.get(dishForFirstGuest);
        guests.add(couples.get((hostIndex + dishIndex + 1) % couples.size()));

        Dish dishForSecondGuest = Dish.circularNext(dishForFirstGuest);
        couples = assignedDishes.get(dishForSecondGuest);
        guests.add(couples.get((hostIndex + dishIndex + 2 + selectedDish.offset()) % couples.size()));

        return guests;
    }

    private void addSitting(Dish dish, Couple host, List<Couple> guests) {
        List<Sitting> sittings = sittingsMap.computeIfAbsent(dish, k -> new ArrayList<>());
        sittings.add(new Sitting(host, guests));
    }

    private void printDishAssignment() {
        List<Dish> dishes = new ArrayList<>(sittingsMap.keySet());
        Collections.sort(dishes);
        for (Dish dish : dishes) {
            List<Sitting> sittings = sittingsMap.get(dish);
            for (Sitting sitting : sittings) {
                StringBuilder allergies =  new StringBuilder();
                for (Couple couple : sitting.getGuests()) {
                    if (couple.getAllergies() != null) {
                        if (allergies.length() > 0) {
                            allergies.append(", ");
                        }
                        allergies.append(couple.getAllergies());
                    }
                }
                if (allergies.length() == 0) {
                    allergies.append("Inga");
                }

                System.out.println("Ludvigsborgs Cykelfest " + date + "\n" +
                        "\n" +
                        "Namn: " + sitting.getHost() +
                        "\n" +
                        "Ni ska vara värdpar för: " + dish.getText() +
                        "\n" +
                        "Ni kommer att få 2 par som gäster.\n" +
                        "\n" +
                        "Ev allergier: " + allergies +
                        "\n" +
                        "\n" +
                        "Det är jätteviktigt att alla tider respekteras för att festen ska flyta på.\n" +
                        "\n" +
                        "17:45 Förrätten startar.\n" +
                        "18:15 Ett kuvert öppnas där det står hos vem var och en skall äta huvudrätten.\n" +
                        "19:00 Dags att lämna för att cykla till huvudrätten (18.45 om man ska bjuda på huvudrätt).\n" +
                        "19:15 Huvudrätten börjar.\n" +
                        "20:00 Dags för nästa kuvert, så var och en vet var de skall äta dessert.\n" +
                        "20:30 Tid att cykla till desserten (20.15 om man ska bjuda på dessert).\n" +
                        "20:45 Desserten börjar.\n" +
                        "21:45 Dags att lämna för att cykla till den stora festen!\n" +
                        "22:00 Den gemensamma festen vid dansbanan börjar med bubbelmingel, sen blir det dans!\n" +
                        "\n" +
                        "Det finns några enkla tips på maten som har mejslats fram genom cykelfestens historia, som sägner och sagor som har gått från cykelkällare till cykelkällare…" +
                        "\u2028\u2028" +
                        "Laga något enkelt, som går att förbereda och värma. Ni kommer att anlända till ert hem något före gästerna, men det finns max 10 minuter att få färdigt maten på, så skippa sjötungan och välj något som kräver mindre precision." +
                        "\u2028\u2028" +
                        "Det är av YTTERSTA vikt att tidsschemat hålls, så att börja tända grillen när gästerna kraschar in med cyklarna är en mindre bra idé." +
                        "\u2028\u2028" +
                        "Eftersom alla par bekostar den rätt som de bjuder på så är det fiffigt om alla följer en vettig linje vad gäller tjusighetsgrad. Rysk caviar är trevligt, men om nästa par bjuder på makaronilåda så känner de sig kanske lite nedtryckta i cykelskorna. Det vore synd. Välj alltså något som är enkelt att laga och som är alldeles tillräckligt festligt utan att vara over-the-top!\n" +
                        "\n" +
                        "Vid frågor, ring " + contactName + " " + contactPhone + ".\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "// Festkommittén \f");
            }
        }
    }

    private void printLocationForStarter() {
        List<Sitting> sittings = sittingsMap.get(Dish.STARTER);
        for (Sitting sitting : sittings) {
            for (Couple couple : sitting.getGuests()) {
                System.out.println("\n\n\nLudvigsborgs Cykelfest " + date);
                System.out.println("\n\n\nNamn: " + couple.getName());
                System.out.println("\n" + Dish.STARTER.getText() + " äter ni på " + sitting.getHost().getAddress());
                System.out.println("\n\n\nRing om ni inte hittar!");
                System.out.println("\n" + contactName + " " + contactPhone + "\f");
            }
        }
    }

    private void printNextLocationForCouplesBySitting(Dish dish) {
        List<Sitting> sittings = sittingsMap.get(dish);
        for (Sitting sitting : sittings) {
            Dish nextDish = Dish.next(dish);
            List<Sitting> sittingsForNextDish = sittingsMap.get(nextDish);
            printLocationForNextSitting(sitting.getHost(), sittingsForNextDish, nextDish);
            for (Couple couple : sitting.getGuests()) {
                printLocationForNextSitting(couple, sittingsForNextDish, nextDish);
            }
        }
    }

    private void printLocationForNextSitting(Couple couple, List<Sitting> sittings, Dish dish) {
        System.out.println("\n\n\nLudvigsborgs Cykelfest " + date);
        System.out.println("\n\n\nNamn: " + couple.getName());
        System.out.println("\n" + dish.getText() + " äter ni på " + getSittingForNextDish(couple, sittings).getHost().getAddress());
        System.out.println("\n\n\nRing om ni inte hittar!");
        System.out.println("\n" + contactName + " " + contactPhone + "\f");
    }

    private Sitting getSittingForNextDish(Couple couple, List<Sitting> sittings) {
        return sittings.stream()
                .filter(sitting -> sitting.has(couple))
                .findFirst()
                .orElseThrow();
    }
}
