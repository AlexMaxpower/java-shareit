package ru.practicum.shareit.item;


import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Item delete(Long userId);

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> getItemsBySearchQuery(String text);

    void deleteItemsByOwner(Long ownderId);

    Item getItemById(Long itemId);
}
