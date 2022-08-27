package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingShortDtoTest {
    private JacksonTester<BookingShortDto> json;
    private BookingShortDto bookingShortDto;
    private Validator validator;

    public BookingShortDtoTest(@Autowired JacksonTester<BookingShortDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        bookingShortDto = new BookingShortDto(
                1L, 2L,
                LocalDateTime.of(2030, 12, 25, 12, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00)
        );
    }

    @Test
    void testJsonBookingShortDto() throws Exception {
        JsonContent<BookingShortDto> result = json.write(bookingShortDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.startTime").isEqualTo("2030-12-25T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.endTime").isEqualTo("2030-12-26T12:00:00");
    }
}