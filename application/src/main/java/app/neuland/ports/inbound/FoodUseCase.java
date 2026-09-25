package app.neuland.ports.inbound;

import app.neuland.model.food.Meal;
import app.neuland.model.food.MealDay;
import app.neuland.model.food.Restaurant;

import java.time.LocalDate;
import java.util.List;

public interface FoodUseCase {

    List<MealDay> list(List<Restaurant> restaurants, LocalDate mealDateFrom, LocalDate mealDateTo);

    Meal get(String mealId);
}
