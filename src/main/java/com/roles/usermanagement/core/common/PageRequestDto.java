package com.roles.usermanagement.core.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Standard DTO for pagination and sorting parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDto {

    @Builder.Default
    @Min(value = 0, message = "El número de página no puede ser menor a 0")
    private int page = 0;

    @Builder.Default
    @Min(value = 1, message = "El tamaño de página debe ser al menos 1")
    @Max(value = 100, message = "El tamaño de página no puede exceder 100 elementos")
    private int size = 10;

    @Builder.Default
    private String sortBy = "id";

    @Builder.Default
    private String direction = "DESC";

    public Pageable toPageable() {
        Sort sort = "ASC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }
}
