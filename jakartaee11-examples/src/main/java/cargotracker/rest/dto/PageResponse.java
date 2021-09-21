package cargotracker.rest.dto;

import jakarta.data.page.Page;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(List<T> content, long page, int size, long totalElements, long totalPages) {

    public static <E, T> PageResponse<T> of(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.content().stream().map(mapper).toList(),
                page.pageRequest().page(),
                page.pageRequest().size(),
                page.totalElements(),
                page.totalPages());
    }
}
