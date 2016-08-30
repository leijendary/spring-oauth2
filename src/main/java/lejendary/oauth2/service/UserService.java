package lejendary.oauth2.service;

import lejendary.oauth2.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 8:10 PM
 */

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Inject
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(userRepository.get(username)).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}