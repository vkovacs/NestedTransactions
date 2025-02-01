package com.example.nestedtransactions.app;

import com.example.nestedtransactions.app.persistence.UserPersistenceService;
import com.example.nestedtransactions.app.persistence.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserPersistenceService userPersistenceService;

    @Transactional
    public void processUser(long id) {
        var userEntity = userRepository.findById(id).orElseThrow();
        System.out.println("User name: " + userEntity.getName());
        var renamedUser = userPersistenceService.renameUser(1, "Alice in Wonderland");
        System.out.println("User name: " + renamedUser.name());

    }

}
