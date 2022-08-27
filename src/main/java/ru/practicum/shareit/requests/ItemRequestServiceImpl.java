package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.service.CheckConsistencyService;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final CheckConsistencyService checker;
    private final ItemRequestMapper mapper;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository repository,
                                  CheckConsistencyService checkConsistencyService, ItemRequestMapper mapper) {
        this.repository = repository;
        this.checker = checkConsistencyService;
        this.mapper = mapper;
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId, LocalDateTime created) {
        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto, requestorId, created);
        return mapper.toItemRequestDto(repository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getItemRequestById(Long itemRequestId, Long userId) {
        checker.isExistUser(userId);
        ItemRequest itemRequest = repository.findById(itemRequestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос с ID=" + itemRequestId + " не найден!"));
        return mapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long requestorId) {
        checker.isExistUser(requestorId);
        return repository.findAllByRequestorId(requestorId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(mapper::toItemRequestDto)
                .collect(toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        checker.isExistUser(userId);
        List<ItemRequestDto> listItemRequestDto = new ArrayList<>();
        Pageable pageable;
        Page<ItemRequest> page;
        Pagination pager = new Pagination(from, size);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");

        if (size == null) {
            pageable =
                    PageRequest.of(pager.getIndex(), pager.getPageSize(), sort);
            do {
                page = repository.findAllByRequestorIdNot(userId, pageable);
                listItemRequestDto.addAll(page.stream().map(mapper::toItemRequestDto).collect(toList()));
                pageable = pageable.next();
            } while (page.hasNext());

        } else {
            for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
                pageable =
                        PageRequest.of(i, pager.getPageSize(), sort);
                page = repository.findAllByRequestorIdNot(userId, pageable);
                listItemRequestDto.addAll(page.stream().map(mapper::toItemRequestDto).collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            listItemRequestDto = listItemRequestDto.stream().limit(size).collect(toList());
        }
        return listItemRequestDto;
    }
}
