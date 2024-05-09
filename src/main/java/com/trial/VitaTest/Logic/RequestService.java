package com.trial.VitaTest.Logic;

import com.trial.VitaTest.Repo.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestService {
    @Autowired
    RequestRepository requestRepository;

    public void save(Request request) {
        Request savedRequest = requestRepository.save(request);
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

//    public Page<Request> findIdAsc(int id, Pageable page){
//        return requestRepository.findByUserIdOrderByDateAsc(id,page);
//    }

    public Optional<Request> findId(Long id){
        return requestRepository.findById(id);
    }

    public Page<Request> findRequests(int id, Pageable page, String order){
        switch (order){
            case ("asc"): return requestRepository.findByUserIdOrderByDateAsc(id, page);

            case ("desc"): return requestRepository.findByUserIdOrderByDateDesc(id, page);

            default: throw new RuntimeException();

        }
    }

    public Page<Request> findSentDesc(Pageable page){
        return requestRepository.findByRequestStatusOrderByDateDesc(RequestStatus.SENT.getStatus(), page);
    }
    public Page<Request> findSentAsc(Pageable page){
        return requestRepository.findByRequestStatusOrderByDateAsc(RequestStatus.SENT.getStatus(), page);
    }

    public Page<Request> findNameDesc(List<Integer> ids, Pageable page){
        return requestRepository.findByUserIdInAndRequestStatusOrderByDateDesc(ids, RequestStatus.SENT.getStatus(), page);
    }
    public Page<Request> findNameAsc(List<Integer> ids, Pageable page){
        return requestRepository.findByUserIdInAndRequestStatusOrderByDateAsc(ids, RequestStatus.SENT.getStatus(), page);
    }
    public long count(){
        return requestRepository.count();
    }

    public void decide(String decision, Long id) throws Exception {
        Request requestToUpdate = requestRepository.findById(id).orElseThrow();
        switch (decision) {
            case ("accept"):
                requestToUpdate.setRequestStatus(RequestStatus.ACCEPTED.getStatus());
            break;
            case ("deny"):
                requestToUpdate.setRequestStatus(RequestStatus.DENIED.getStatus());
            break;
            default: throw new Exception();
        }
        requestRepository.save(requestToUpdate);
    }
}
