package com.example.nestedtransactions.app;

import com.example.nestedtransactions.app.persistence.UserEntity;
import com.example.nestedtransactions.app.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Main {
    private final UserService userService;
    private final UserRepository userRepository;

    @EventListener(ApplicationStartedEvent.class)
    private void applicationStarts() {
        userRepository.saveAll(List.of(
                new UserEntity(1L, "Alice"),
                new UserEntity(2L, "Bob"),
                new UserEntity(3L, "Charlie")
        ));

        userService.processUser(1L);
    }


}
