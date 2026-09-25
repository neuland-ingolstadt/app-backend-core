package app.neuland.model.food;

import app.neuland.model.shared.Language;
import java.util.List;
import java.util.Map;

public record MealVariation(
        String id,
        Map<Language, String> name,
        boolean additional,
        MealPrice price,
        List<String> allergens,
        List<String> flags,
        Nutrition nutrition,
        Language originalLanguage,
        boolean isStatic,
        Restaurant restaurant,
        String parentMealId
) {}
