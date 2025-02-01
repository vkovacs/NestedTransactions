package com.example.nestedtransactions.app.persistence;

import com.example.nestedtransactions.app.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    private Long id;

    private String name;

    public User asUser() {
        return new User(id, name);
    }
}
