package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String email;
}
