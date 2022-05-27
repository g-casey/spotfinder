package com.gcasey2.spotfinder.data.user.security;

import com.gcasey2.spotfinder.data.user.User;
import com.gcasey2.spotfinder.data.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ModifiedUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username);

        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }

        return new ModifiedUserDetails(user);
    }
}
