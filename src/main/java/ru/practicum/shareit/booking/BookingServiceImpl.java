package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.service.CheckConsistencyService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private BookingMapper mapper;
    private CheckConsistencyService checker;

    @Autowired
    @Lazy
    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper,
                              CheckConsistencyService checkConsistencyService) {
        this.repository = bookingRepository;
        this.mapper = bookingMapper;
        this.checker = checkConsistencyService;
    }

    @Override
    public BookingDto create(BookingInputDto bookingInputDto, Long bookerId) {

        checker.isExistUser(bookerId);

        if (!checker.isAvailableItem(bookingInputDto.getItemId())) {
            throw new ValidationException("Вещь с ID=" + bookingInputDto.getItemId() +
                    " недоступна для бронирования!");
        }
        Booking booking = mapper.toBooking(bookingInputDto, bookerId);
        if (bookerId.equals(booking.getItem().getOwner().getId())) {
            throw new BookingNotFoundException("Вещь с ID=" + bookingInputDto.getItemId() +
                    " недоступна для бронирования самим владельцем!");
        }
        return mapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID=" + bookingId + " не найдено!"));
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время бронирования уже истекло!");
        }

        if (booking.getBooker().getId().equals(userId)) {
            if (!approved) {
                booking.setStatus(Status.CANCELED);
                log.info("Пользователь с ID={} отменил бронирование с ID={}", userId, bookingId);
            } else {
                throw new UserNotFoundException("Подтвердить бронирование может только владелец вещи!");
            }
        } else if ((checker.isItemOwner(booking.getItem().getId(), userId)) &&
                (!booking.getStatus().equals(Status.CANCELED))) {
            if (!booking.getStatus().equals(Status.WAITING)) {
                throw new ValidationException("Решение по бронированию уже принято!");
            }
            if (approved) {
                booking.setStatus(Status.APPROVED);
                log.info("Пользователь с ID={} подтвердил бронирование с ID={}", userId, bookingId);
            } else {
                booking.setStatus(Status.REJECTED);
                log.info("Пользователь с ID={} отклонил бронирование с ID={}", userId, bookingId);
            }
        } else {
            if (booking.getStatus().equals(Status.CANCELED)) {
                throw new ValidationException("Бронирование было отменено!");
            } else {
                throw new ValidationException("Подтвердить бронирование может только владелец вещи!");
            }
        }

        return mapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID=" + bookingId + " не найдено!"));
        if (booking.getBooker().getId().equals(userId) || checker.isItemOwner(booking.getItem().getId(), userId)) {
            return mapper.toBookingDto(booking);
        } else {
            throw new UserNotFoundException("Посмотреть данные бронирования может только владелец вещи" +
                    " или бронирующий ее!");
        }
    }

    @Override
    public List<BookingDto> getBookings(String state, Long userId) {
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = repository.findByBookerId(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = repository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = repository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = repository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "WAITING":
                bookings = repository.findByBookerIdAndStatus(userId, Status.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = repository.findByBookerIdAndStatus(userId, Status.REJECTED, sortByStartDesc);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsOwner(String state, Long userId) {
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = repository.findByItem_Owner_Id(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = repository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = repository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = repository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(),
                        sortByStartDesc);
                break;
            case "WAITING":
                bookings = repository.findByItem_Owner_IdAndStatus(userId, Status.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = repository.findByItem_Owner_IdAndStatus(userId, Status.REJECTED, sortByStartDesc);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingShortDto getLastBooking(Long itemId) {
        BookingShortDto bookingShortDto =
                mapper.toBookingShortDto(repository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId,
                        LocalDateTime.now()));
        return bookingShortDto;
    }

    @Override
    public BookingShortDto getNextBooking(Long itemId) {
        BookingShortDto bookingShortDto =
                mapper.toBookingShortDto(repository.findFirstByItem_IdAndStartAfterOrderByStartAsc(itemId,
                        LocalDateTime.now()));
        return bookingShortDto;
    }

    @Override
    public Booking getBookingWithUserBookedItem(Long itemId, Long userId) {
        return repository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), Status.APPROVED);
    }
}
