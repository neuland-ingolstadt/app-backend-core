package app.neuland.implementation.food;

import app.neuland.model.food.Meal;
import app.neuland.model.food.MealDay;
import app.neuland.model.food.Restaurant;
import app.neuland.ports.inbound.FoodUseCase;
import app.neuland.ports.outbound.FoodServiceClient;

import java.time.LocalDate;
import java.util.List;

public class FoodService implements FoodUseCase {

    private final FoodServiceClient foodServiceClient;

    public FoodService(FoodServiceClient foodServiceClient) {
        this.foodServiceClient = foodServiceClient;
    }

    @Override
    public List<MealDay> list(List<Restaurant> restaurants, LocalDate mealDateFrom, LocalDate mealDateTo) {
        return foodServiceClient.findAll(restaurants, mealDateFrom, mealDateTo);
    }

    @Override
    public Meal get(String mealId) {
        return foodServiceClient
                .findById(mealId)
                .orElseThrow(() -> new MealNotFoundException(mealId));
    }
}
