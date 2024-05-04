package com.trial.VitaTest.Repo;

import com.trial.VitaTest.Logic.Request;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {

}
