package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.service.CheckConsistencyService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    BookingRepository mockBookingRepository;
    @Mock
    CheckConsistencyService checkConsistencyService;

    @Test
    void shouldExceptionWhenGetBookingWithWrongId() {
        BookingService bookingService = new BookingServiceImpl(mockBookingRepository, null,
                checkConsistencyService);
        when(checkConsistencyService.isExistUser(any(Long.class)))
                .thenReturn(true);
        when(mockBookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getBookingById(-1L, 1L));
        Assertions.assertEquals("Бронирование с ID=-1 не найдено!", exception.getMessage());
    }
}
