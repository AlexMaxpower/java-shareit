package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.service.CheckConsistencyService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private CheckConsistencyService checkConsistencyService;
    private ItemRequestService itemRequestService;
    private ItemRequestMapper itemRequestMapper;

    private UserDto userDto = new UserDto(1L, "Alex", "alex@alex.ru");

    private ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "ItemRequest description",
            userDto, LocalDateTime.of(2022, 1, 2, 3, 4, 5), null);

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository,
                checkConsistencyService, null);
    }

    @Test
    void shouldExceptionWhenGetItemRequestWithWrongId() {
        when(checkConsistencyService.isExistUser(any(Long.class)))
                .thenReturn(true);
        when(mockItemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final ItemRequestNotFoundException exception = Assertions.assertThrows(
                ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(-1L, 1L));
        Assertions.assertEquals("Запрос с ID=-1 не найден!", exception.getMessage());
    }
}