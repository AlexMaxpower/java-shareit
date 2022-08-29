package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.service.CheckConsistencyServiceTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository mockItemRepository;

    private ItemMapper mapper;
    private CommentRepository commentRepository;
    private CheckConsistencyServiceTest checker;

    @Test
    void shouldExceptionWhenGetItemWithWrongId() {
        ItemService itemService = new ItemServiceImpl(mockItemRepository, null, null,
                null);
                when(mockItemRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getItemById(-1L, 1L));
        Assertions.assertEquals("Вещь с ID=-1 не найдена!", exception.getMessage());
    }
}

