package com.trial.VitaTest.Logic;

import com.trial.VitaTest.Repo.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    @Autowired
    RequestRepository requestRepository;

}
