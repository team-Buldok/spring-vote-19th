package buldog.vote.service.auth;

import buldog.vote.common.jwt.TokenProvider;
import buldog.vote.domain.User;
import buldog.vote.dto.auth.SignInRequestDto;
import buldog.vote.dto.auth.TokenDto;
import buldog.vote.dto.auth.TokenReIssueResponseDto;
import buldog.vote.dto.auth.TokenResponseWithUserIdDto;
import buldog.vote.exception.AppException;
import buldog.vote.exception.ErrorCode;
import buldog.vote.repository.UserRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String,String> redisTemplate;





    @Transactional
    public TokenResponseWithUserIdDto login(final SignInRequestDto signInRequestDto){

        System.out.println(signInRequestDto.getUserID());

        User findUser  = userRepository.findByUsername(signInRequestDto.getUserID()).orElseThrow(()->new AppException(
                ErrorCode.ID_NOT_MATCH));

        if(!passwordEncoder.matches(signInRequestDto.getPassword(),findUser.getPassword())){
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        UsernamePasswordAuthenticationToken authenticationToken= signInRequestDto.getAuthenticationToken();
        Authentication authentication= authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDto tokenDto=tokenProvider.createToken(authentication);

        saveLoginProcessAtRedis(authentication.getName(),tokenDto);

        return new TokenResponseWithUserIdDto(tokenDto.getType(),tokenDto.getAccessToken(),
                makeResponseCookie(tokenDto.getRefreshToken(),tokenDto.getRefreshTokenValidationTime()),
                tokenDto.getAccessTokenValidationTime(),findUser.getUsername());

    }


    @Transactional
    public TokenReIssueResponseDto reIssue(final String accessToken, final String refreshToken){

        Authentication authentication= tokenProvider.getAuthentication(accessToken);

        if(!redisTemplate.opsForValue().get(authentication.getName()).equals(refreshToken)){
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }

        TokenDto tokenDto=tokenProvider.createToken(authentication);
        saveLoginProcessAtRedis(authentication.getName(),tokenDto);

        return new TokenReIssueResponseDto(tokenDto.getType(),tokenDto.getAccessToken(),
                makeResponseCookie(tokenDto.getRefreshToken(),tokenDto.getRefreshTokenValidationTime()),tokenDto.getAccessTokenValidationTime());
    }

    @Transactional
    public void logout(final String accessToken){

        if (!tokenProvider.validateToken(accessToken)){
            throw new AppException(ErrorCode.ACCESS_TOKEN_NOT_MATCH);
        }

        Authentication authentication = tokenProvider.getAuthentication(accessToken);

        if (redisTemplate.opsForValue().get(authentication.getName())!=null){
            redisTemplate.delete(authentication.getName());
        }


        Long expiration = tokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken,"logout",expiration, TimeUnit.MILLISECONDS);


    }

    private ResponseCookie makeResponseCookie(String refreshToken,Long refreshTokenValidationTime){
        return  ResponseCookie.from("refreshToken",refreshToken)
                .httpOnly(false)//   true 시 자바스크립트에서 쿠키 접근 불가 따라서 XSS 공격 방지
                .secure(true)//true 시 HTTPS 연결을 통해서만 전달 .
                .path("/")
                .maxAge(refreshTokenValidationTime)
                .build();
    }

    private void saveLoginProcessAtRedis(String key, TokenDto tokenDto){
        redisTemplate.opsForValue().set(key,tokenDto.getRefreshToken(),tokenDto.getRefreshTokenValidationTime(),TimeUnit.MILLISECONDS);
    }

}

