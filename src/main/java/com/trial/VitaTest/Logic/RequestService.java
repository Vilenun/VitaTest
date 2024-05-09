package com.trial.VitaTest.Logic;

import com.trial.VitaTest.Repo.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {
    @Autowired
    RequestRepository requestRepository;
    public List<Request> getRequestAll(){
        List<Request> requestAll = new ArrayList<>();
        requestRepository.findAll().forEach(requestAll::add);
        return requestAll;
    }
    public long save(Request request) {
        Request savedRequest = requestRepository.save(request);
        return savedRequest.getId();
    }
    public void update(long id,String request){
        Request requestToUpdate = requestRepository.findById(id).orElseThrow();
        requestToUpdate.setName(request);
        requestRepository.save(requestToUpdate);
    }
    public void send(long id){
        Request requestToUpdate = requestRepository.findById(id).orElseThrow();
        requestToUpdate.setRequestStatus(RequestStatus.SENT.getStatus());
        requestRepository.save(requestToUpdate);
    }

    public Page<Request> findIdAsc(int id, Pageable page){
        return requestRepository.findByUserIdOrderByDateAsc(id,page);
    }

    public Optional<Request> findId(Long id){
        return requestRepository.findById(id);
    }

    public Page<Request> findIdDesc(int id, Pageable page){
        return requestRepository.findByUserIdOrderByDateDesc(id, page);
    }
    public long count(){
        return requestRepository.count();
    }
}
