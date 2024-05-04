package com.trial.VitaTest.Logic;

import com.trial.VitaTest.Repo.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequestService {
    @Autowired
    RequestRepository requestRepository;
    public List<Request> getRequestAll(){
        List<Request> requestAll = new ArrayList<>();
        requestRepository.findAll().forEach(requestAll::add);
        return requestAll;
    }
    public long count(){
        return requestRepository.count();
    }
}
