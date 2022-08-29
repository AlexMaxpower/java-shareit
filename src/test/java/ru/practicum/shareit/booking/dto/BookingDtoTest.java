package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    private JacksonTester<BookingDto> json;
    private BookingDto bookingDto;
    private Validator validator;

    public BookingDtoTest(@Autowired JacksonTester<BookingDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2030, 12, 25, 12, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00),
                new ItemDto(1L, "FirstItem", "DescriptionOfFirstItem", true,
                        new User(1L, "FirstUser", "first@email.ru"), null, null,
                        null, null),
                new UserDto(2L, "SecondUser", "second@email.ru"), Status.WAITING);

    }

    @Test
    void testJsonBookingDto() throws Exception {
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2030-12-25T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2030-12-26T12:00:00");
        assertThat(result).extractingJsonPathValue("$.requestId").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}