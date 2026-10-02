package app.neuland.adapters.inbound.rest.error;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MismatchedInputExceptionMapperTest {

    @Mock
    UriInfo uriInfo;

    private MismatchedInputExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MismatchedInputExceptionMapper();
        mapper.uriInfo = uriInfo;
        when(uriInfo.getRequestUri()).thenReturn(URI.create("http://localhost/room-reports"));
    }

    @Test
    void shouldUseMessageWhenOriginalMessageIsNull() {
        MismatchedInputException exception = mock(MismatchedInputException.class);
        when(exception.getOriginalMessage()).thenReturn(null);
        when(exception.getMessage()).thenReturn("fallback message");
        when(exception.getPath()).thenReturn(List.of());

        Response response = mapper.toResponse(exception);

        Problem problem = (Problem) response.getEntity();
        assertEquals(400, response.getStatus());
        assertEquals("fallback message", problem.detail());
    }

    @Test
    void shouldPrefixFieldNameWhenPathPresent() {
        MismatchedInputException exception = mock(MismatchedInputException.class);
        JsonMappingException.Reference reference = mock(JsonMappingException.Reference.class);
        when(exception.getOriginalMessage()).thenReturn("not a valid enum");
        when(exception.getPath()).thenReturn(List.of(reference));
        when(reference.getFieldName()).thenReturn("reason");

        Response response = mapper.toResponse(exception);

        Problem problem = (Problem) response.getEntity();
        assertEquals("reason: not a valid enum", problem.detail());
    }

    @Test
    void shouldSkipNullAndBlankFieldNamesInPath() {
        MismatchedInputException exception = mock(MismatchedInputException.class);
        JsonMappingException.Reference nullName = mock(JsonMappingException.Reference.class);
        JsonMappingException.Reference blankName = mock(JsonMappingException.Reference.class);
        JsonMappingException.Reference named = mock(JsonMappingException.Reference.class);
        when(exception.getOriginalMessage()).thenReturn("not a valid enum");
        when(exception.getPath()).thenReturn(List.of(nullName, blankName, named));
        when(nullName.getFieldName()).thenReturn(null);
        when(blankName.getFieldName()).thenReturn("  ");
        when(named.getFieldName()).thenReturn("reason");

        Response response = mapper.toResponse(exception);

        Problem problem = (Problem) response.getEntity();
        assertEquals("reason: not a valid enum", problem.detail());
    }

    @Test
    void shouldDefaultFieldToValueForInvalidFormatWithoutPath() {
        InvalidFormatException exception = mock(InvalidFormatException.class);
        when(exception.getOriginalMessage()).thenReturn("Cannot deserialize value");
        when(exception.getPath()).thenReturn(List.of());

        Response response = mapper.toResponse(exception);

        Problem problem = (Problem) response.getEntity();
        assertEquals("value: Cannot deserialize value", problem.detail());
    }

    @Test
    void shouldBuildMessageFromInvalidFormatValueWhenMessageBlank() {
        InvalidFormatException exception = mock(InvalidFormatException.class);
        when(exception.getOriginalMessage()).thenReturn("   ");
        when(exception.getPath()).thenReturn(List.of());
        when(exception.getValue()).thenReturn("NOPE");

        Response response = mapper.toResponse(exception);

        Problem problem = (Problem) response.getEntity();
        assertEquals("value: Invalid value: NOPE", problem.detail());
    }

    @Test
    void shouldBuildMessageFromInvalidFormatValueWhenMessageNull() {
        InvalidFormatException exception = mock(InvalidFormatException.class);
        when(exception.getOriginalMessage()).thenReturn(null);
        when(exception.getMessage()).thenReturn(null);
        when(exception.getPath()).thenReturn(List.of());
        when(exception.getValue()).thenReturn(42);

        Response response = mapper.toResponse(exception);

        Problem problem = (Problem) response.getEntity();
        assertEquals("value: Invalid value: 42", problem.detail());
    }

    @Test
    void shouldPreferNamedFieldFromInvalidFormatPathInsideFallback() {
        InvalidFormatException exception = mock(InvalidFormatException.class);
        JsonMappingException.Reference blankName = mock(JsonMappingException.Reference.class);
        JsonMappingException.Reference named = mock(JsonMappingException.Reference.class);
        when(exception.getOriginalMessage()).thenReturn("not a valid enum");
        // First path lookup finds nothing (enter InvalidFormat fallback);
        // second lookup (on the same exception) skips blanks then yields a field.
        when(exception.getPath())
                .thenReturn(List.of())
                .thenReturn(List.of(blankName, named));
        when(blankName.getFieldName()).thenReturn("  ");
        when(named.getFieldName()).thenReturn("reason");

        Response response = mapper.toResponse(exception);

        Problem problem = (Problem) response.getEntity();
        assertEquals("reason: not a valid enum", problem.detail());
    }
}
