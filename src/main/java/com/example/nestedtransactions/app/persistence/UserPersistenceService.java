package com.example.nestedtransactions.app.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPersistenceService {
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Problem 1: this prevents the renaming of the user confusingly
    public void renameUser(long id, String newName) {
        var userEntity = userRepository.findById(id).orElseThrow();
        userEntity.setName(newName);
        userRepository.save(userEntity);
    }
}
