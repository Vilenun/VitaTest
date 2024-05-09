package com.trial.VitaTest.Repo;

import com.trial.VitaTest.Logic.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface RequestRepository extends PagingAndSortingRepository<Request, Long>,CrudRepository<Request, Long> {

    Page<Request> findByUserIdOrderByDateDesc(int id, Pageable paging);

    Page<Request> findByUserIdOrderByDateAsc(int id, Pageable paging);
}
