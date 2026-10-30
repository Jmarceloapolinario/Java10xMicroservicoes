package com.java10x.user.service;

import com.java10x.user.domain.UserModel;
import com.java10x.user.exception.DuplicateEmailException;
import com.java10x.user.producer.UserProducer;
import com.java10x.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserProducer userProducer;

    /**
     * Salva o usuário no repositório e publica um evento relacionado ao usuário.
     * Este metodo garante que, após a persistência do usuário, um evento seja enviado para outros sistemas interessados.
     * @throws DuplicateEmailException se o email já existe no sistema
     */
    @Transactional
    public UserModel saveAndPublish(UserModel userModel){

        if (emailExists(userModel.getEmail())) {
            throw new DuplicateEmailException(userModel.getEmail());
        }

        userModel = repository.save(userModel);
        userProducer.publishEvent(userModel);

        return userModel;

    }
    public Optional<UserModel> getUserByEmail(String email){
        return repository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return repository.findByEmail(email).isPresent();
    }

    public List<UserModel> getAllUsers(){
        return repository.findAll();
    }
    public void deleteUser(UUID id) {
        repository.deleteById(id);
    }



}
