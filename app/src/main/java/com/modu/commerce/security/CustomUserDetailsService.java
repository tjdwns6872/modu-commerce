package com.modu.commerce.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.modu.commerce.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {
    
    private final UserRepository userRepository;

    public UserDetails loadUserById(Long id) {
        return userRepository.findById(id)
                .map(CustomUserDetails::new) // 엔티티 → UserDetails 변환
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }
}
