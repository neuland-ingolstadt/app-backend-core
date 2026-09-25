package app.neuland.model.food;

import java.time.LocalDate;
import java.util.List;

public record MealDay(
        LocalDate date,
        List<Meal> meals
) {}
