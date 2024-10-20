package co.edu.uniquindio.CRUD.dtos.usuario;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PageItemUsuarioDTO(
        List<ItemUsuarioDTO> content,
        Pageable pageable,
        int totalPages,
        long totalElements,
        boolean last,
        int size,
        int number,
        Sort sort,
        int numberOfElements,
        boolean first,
        boolean empty
) {
}