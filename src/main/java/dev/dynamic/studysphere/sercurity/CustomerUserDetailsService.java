package dev.dynamic.studysphere.sercurity;

import dev.dynamic.studysphere.entities.User;
import dev.dynamic.studysphere.entities.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(this::convertToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private UserDetails convertToUserDetails(User customer) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(customer.getUsername())
                .password(customer.getPassword())
                .roles(customer.getRole().name())
                .build();
    }

}
