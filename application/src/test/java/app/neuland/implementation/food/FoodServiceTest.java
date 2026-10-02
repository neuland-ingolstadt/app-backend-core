package app.neuland.implementation.food;

import app.neuland.model.food.Meal;
import app.neuland.model.food.MealCategory;
import app.neuland.model.food.MealDay;
import app.neuland.model.food.MealPrice;
import app.neuland.model.food.MealVariation;
import app.neuland.model.food.Nutrition;
import app.neuland.model.food.Restaurant;
import app.neuland.model.shared.Language;
import app.neuland.model.user.User;
import app.neuland.ports.outbound.FoodServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    FoodServiceClient foodServiceClient;

    private FoodService service;

    @BeforeEach
    void setUp() {
        service = new FoodService(foodServiceClient);
    }

    @Test
    void shouldListMealDays() {
        Meal meal = sampleMeal(MealCategory.MAIN, Restaurant.INGOLSTADT_MENSA);
        MealDay day = new MealDay(LocalDate.of(2026, 1, 15), List.of(meal));
        when(foodServiceClient.findAll(
                List.of(Restaurant.INGOLSTADT_MENSA, Restaurant.NEUBURG_MENSA),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        )).thenReturn(List.of(day));

        List<MealDay> result = service.list(
                List.of(Restaurant.INGOLSTADT_MENSA, Restaurant.NEUBURG_MENSA),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );

        assertEquals(1, result.size());
        assertEquals(meal, result.getFirst().meals().getFirst());
        assertEquals(new User(1L, "alice"), new User(1L, "alice"));
    }

    @Test
    void shouldGetMeal() {
        Meal meal = sampleMeal(MealCategory.SOUP, Restaurant.REIMANNS);
        when(foodServiceClient.findById("meal-1")).thenReturn(Optional.of(meal));

        assertEquals(meal, service.get("meal-1"));
    }

    @Test
    void shouldThrowWhenMealMissing() {
        when(foodServiceClient.findById("missing")).thenReturn(Optional.empty());

        MealNotFoundException exception = assertThrows(
                MealNotFoundException.class,
                () -> service.get("missing")
        );
        assertEquals("Meal not found: missing", exception.getMessage());
    }

    @Test
    void shouldCoverRemainingFoodEnums() {
        assertEquals(MealCategory.SALAD, sampleMeal(MealCategory.SALAD, Restaurant.CANISIUS).category());
        assertEquals(MealCategory.OTHER, sampleMeal(MealCategory.OTHER, Restaurant.INGOLSTADT_MENSA).category());
    }

    private static Meal sampleMeal(MealCategory category, Restaurant restaurant) {
        Nutrition nutrition = new Nutrition(100, 24, 1, 0.5, 3, 1, 0.2, 4, 0.1);
        MealPrice price = new MealPrice(2.5, 3.5, 4.5);
        MealVariation variation = new MealVariation(
                "var-1",
                Map.of(Language.DE, "Variation", Language.EN, "Variation"),
                true,
                price,
                List.of("A"),
                List.of("vegan"),
                nutrition,
                Language.DE,
                false,
                restaurant,
                "meal-1"
        );

        return new Meal(
                "meal-1",
                Map.of(Language.DE, "Essen", Language.EN, "Meal"),
                category,
                price,
                List.of("A", "C"),
                List.of("vegetarian"),
                nutrition,
                Language.DE,
                false,
                restaurant,
                List.of(variation)
        );
    }
}
