package ru.practicum.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUser {
    @Id
    Long id;
    String username;
    String password;
}
