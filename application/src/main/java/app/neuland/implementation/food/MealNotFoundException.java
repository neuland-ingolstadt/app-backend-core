package app.neuland.implementation.food;

public class MealNotFoundException extends RuntimeException {

    public MealNotFoundException(String mealId) {
        super("Meal not found: " + mealId);
    }
}
