package com.trial.VitaTest.Repo;

import com.trial.VitaTest.Logic.RequestUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RequestUserRepository extends CrudRepository<RequestUser, Long> {
    public Optional<RequestUser> findRequestUserByUsername(String username);
    public Optional<List<RequestUser>> findByUsernameIgnoreCaseContains(String username);
    @Override
    public List<RequestUser> findAll();
}
