package com.trial.VitaTest.Logic;

import com.trial.VitaTest.Repo.RequestUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final RequestUserRepository repository;

    public UserDetailsServiceImpl(RequestUserRepository repository) {
        this.repository = repository;
    }
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        RequestUser user = repository
                .findRequestUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new RequestUserAdapter(user);
    }
}
