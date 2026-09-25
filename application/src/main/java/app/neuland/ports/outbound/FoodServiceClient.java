package app.neuland.ports.outbound;

import app.neuland.model.food.Meal;
import app.neuland.model.food.MealDay;
import app.neuland.model.food.Restaurant;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FoodServiceClient {

    List<MealDay> findAll(List<Restaurant> restaurants, LocalDate mealDateFrom, LocalDate mealDateTo);

    Optional<Meal> findById(String mealId);
}
