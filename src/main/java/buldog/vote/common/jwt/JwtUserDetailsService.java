package buldog.vote.common.jwt;

import buldog.vote.domain.User;
import buldog.vote.exception.AppException;
import buldog.vote.exception.ErrorCode;
import buldog.vote.repository.UserRepository;
import buldog.vote.service.auth.CustomUserDetails;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;



    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(this::getUserDetails)
                .orElseThrow(()->new AppException(ErrorCode.ID_NOT_MATCH));
    }

    public CustomUserDetails getUserDetails(User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("임시");

        return new CustomUserDetails(user.getUsername(), user.getPassword(), Collections.singleton(authority),user);
    }
}