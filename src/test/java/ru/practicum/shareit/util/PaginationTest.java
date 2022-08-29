package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaginationTest {
    @Test
    public void whenSizeNotPositiveThenValidateException() {
        Integer from = 0;
        Integer size = -1;
        assertThrows(ValidationException.class, () -> new Pagination(from, size));
    }

    @Test
    public void whenFromNotPositiveThenValidateException() {
        Integer from = -1;
        Integer size = 1;
        assertThrows(ValidationException.class, () -> new Pagination(from, size));
    }

    @Test
    public void whenSizeLessOneThenValidateException() {
        Integer from = 0;
        Integer size = 0;
        assertThrows(ValidationException.class, () -> new Pagination(from, size));
    }

    @Test
    public void whenSizeLessFromThenResultCorrectly() {
        Integer from = 10;
        Integer size = 2;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(10);
        assertThat(pager.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void whenSizeMoreFromThenResultCorrectly() {
        Integer from = 2;
        Integer size = 5;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(2);
        assertThat(pager.getTotalPages()).isEqualTo(4);
    }

    @Test
    public void whenSizeEqualsFromThenResultCorrectly() {
        Integer from = 5;
        Integer size = 5;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(5);
        assertThat(pager.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void whenSizeEqualsNotNullAndFromIsZeroThenResultCorrectly() {
        Integer from = 0;
        Integer size = 5;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(0);
        assertThat(pager.getPageSize()).isEqualTo(5);
        assertThat(pager.getTotalPages()).isEqualTo(1);
    }

    @Test
    public void whenFromEqualsZeroAndSizeNullThenResultCorrectly() {
        Integer from = 0;
        Integer size = null;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(0);
        assertThat(pager.getPageSize()).isEqualTo(1000);
        assertThat(pager.getTotalPages()).isEqualTo(0);
    }

}
