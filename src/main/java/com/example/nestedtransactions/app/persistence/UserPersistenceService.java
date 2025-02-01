package com.example.nestedtransactions.app.persistence;

import com.example.nestedtransactions.app.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPersistenceService {
    private final UserRepository userRepository;

    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    public User renameUser(long id, String newName) {
        var userEntity = userRepository.findById(id).orElseThrow();
        userEntity.setName(newName);
        return userRepository.save(userEntity)
                .asUser();
    }
}
