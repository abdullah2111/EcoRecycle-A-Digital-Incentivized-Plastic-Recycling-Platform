package com.example.ecorecycle.security;

import com.example.ecorecycle.entity.Recycler;
import com.example.ecorecycle.entity.Role;
import com.example.ecorecycle.entity.User;
import com.example.ecorecycle.repository.RecyclerRepository;
import com.example.ecorecycle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RecyclerRepository recyclerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities(mapRole(user.getRole()))
                    .accountLocked(!user.getIsActive())
                    .build();
        }

        Recycler recycler = recyclerRepository.findByEmail(username).orElse(null);
        if (recycler != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(recycler.getEmail())
                    .password(recycler.getPassword())
                    .authorities(mapRole(recycler.getRole()))
                    .accountLocked(!recycler.getIsActive())
                    .build();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }

    private Collection<? extends GrantedAuthority> mapRole(Role role) {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}

