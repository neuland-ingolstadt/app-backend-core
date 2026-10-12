package app.neuland.adapters.inbound.rest;

import app.neuland.backend.core.api.v0.model.Announcement;
import app.neuland.backend.core.api.v0.model.AnnouncementContent;
import app.neuland.backend.core.api.v0.model.AnnouncementContents;
import app.neuland.backend.core.api.v0.model.AnnouncementCreateRequest;
import app.neuland.backend.core.api.v0.model.AnnouncementListResponse;
import app.neuland.backend.core.api.v0.model.AnnouncementPatchRequest;
import app.neuland.backend.core.api.v0.model.AnnouncementWrite;
import app.neuland.backend.core.api.v0.model.AuditLogEntry;
import app.neuland.backend.core.api.v0.model.AuditLogListResponse;
import app.neuland.backend.core.api.v0.model.AuditLogOperationEnum;
import app.neuland.backend.core.api.v0.model.CampusEnum;
import app.neuland.backend.core.api.v0.model.CreateRoomReportRequest;
import app.neuland.backend.core.api.v0.model.LanguageEnum;
import app.neuland.backend.core.api.v0.model.LocalizedString;
import app.neuland.backend.core.api.v0.model.Meal;
import app.neuland.backend.core.api.v0.model.MealCategory;
import app.neuland.backend.core.api.v0.model.MealDay;
import app.neuland.backend.core.api.v0.model.MealPrice;
import app.neuland.backend.core.api.v0.model.MealVariation;
import app.neuland.backend.core.api.v0.model.Nutrition;
import app.neuland.backend.core.api.v0.model.PlatformEnum;
import app.neuland.backend.core.api.v0.model.Problem;
import app.neuland.backend.core.api.v0.model.Restaurant;
import app.neuland.backend.core.api.v0.model.RoomReport;
import app.neuland.backend.core.api.v0.model.RoomReportCategoryEnum;
import app.neuland.backend.core.api.v0.model.RoomReportListResponse;
import app.neuland.backend.core.api.v0.model.RoomReportPatchRequest;
import app.neuland.backend.core.api.v0.model.RoomReportResponse;
import app.neuland.backend.core.api.v0.model.Sports;
import app.neuland.backend.core.api.v0.model.SportsCategoryEnum;
import app.neuland.backend.core.api.v0.model.SportsContent;
import app.neuland.backend.core.api.v0.model.SportsContents;
import app.neuland.backend.core.api.v0.model.SportsCreateRequest;
import app.neuland.backend.core.api.v0.model.SportsListResponse;
import app.neuland.backend.core.api.v0.model.SportsPatchRequest;
import app.neuland.backend.core.api.v0.model.SportsWrite;
import app.neuland.backend.core.api.v0.model.UserKindEnum;
import app.neuland.backend.core.api.v0.model.WeekdayEnum;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * OpenAPI resources return untyped {@link jakarta.ws.rs.core.Response}, so Quarkus
 * cannot infer Jackson model types for native image. Without this, GraalVM only
 * exposes a partial method set and optional JSON properties are dropped.
 *
 * @see <a href="https://quarkus.io/guides/rest-json#json-organisation">Quarkus REST JSON</a>
 */
@RegisterForReflection(targets = {
        Announcement.class,
        AnnouncementContent.class,
        AnnouncementContents.class,
        AnnouncementCreateRequest.class,
        AnnouncementListResponse.class,
        AnnouncementPatchRequest.class,
        AnnouncementWrite.class,
        AuditLogEntry.class,
        AuditLogListResponse.class,
        AuditLogOperationEnum.class,
        CampusEnum.class,
        CreateRoomReportRequest.class,
        LanguageEnum.class,
        LocalizedString.class,
        Meal.class,
        MealCategory.class,
        MealDay.class,
        MealPrice.class,
        MealVariation.class,
        Nutrition.class,
        PlatformEnum.class,
        Problem.class,
        Restaurant.class,
        RoomReport.class,
        RoomReportCategoryEnum.class,
        RoomReportListResponse.class,
        RoomReportPatchRequest.class,
        RoomReportResponse.class,
        Sports.class,
        SportsCategoryEnum.class,
        SportsContent.class,
        SportsContents.class,
        SportsCreateRequest.class,
        SportsListResponse.class,
        SportsPatchRequest.class,
        SportsWrite.class,
        UserKindEnum.class,
        WeekdayEnum.class
})
public class BackendCoreApiReflection {
}
