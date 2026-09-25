package app.neuland.model.food;

public record Nutrition(
        double kj,
        double kcal,
        double fat,
        double fatSaturated,
        double carbs,
        double sugar,
        double fiber,
        double protein,
        double salt
) {}
