package buldog.vote.controller.auth;

import buldog.vote.dto.auth.SignInRequestDto;
import buldog.vote.dto.auth.TokenReIssueResponseDto;
import buldog.vote.dto.auth.TokenResponseWithUserIdDto;
import buldog.vote.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "로그인 관련 용도 컨트롤러", description = "로그인과 관련된 활동을 모아두는 컨트롤러입니다.")
public class AuthController {

    private final AuthService authService;
    private static final String accessTokenHeader = "Authorization";

    private static final String refreshTokenHeader = "refreshToken";


    @PostMapping("/login")
    @Operation(summary = "ID, Password 기반 로그인",description = "ID 와 password 를 기반으로 로그인을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "로그인 성공"),
            @ApiResponse(responseCode = "400",description = "아이디 or 비밀번호 틀렸을 때")
    })
    public ResponseEntity<Void> login(@RequestBody @Valid final SignInRequestDto memberSignInRequestDto){

        TokenResponseWithUserIdDto tokenResponseWithUserIdDto = authService.login(memberSignInRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(accessTokenHeader,tokenResponseWithUserIdDto.getAccessToken())
                .header(refreshTokenHeader,tokenResponseWithUserIdDto.getRefreshToken())
                .build();
    }


    @PostMapping("/logout")
    @Operation(summary = "로그아웃",description = "로그아웃을 한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400",description = "accessToken 이 만료되거나 틀렸을 때")
    })
    public ResponseEntity<Void> logout(@RequestHeader("accessToken") final String accessToken
    ){
        authService.logout(accessToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발행",description = "accessToken 의 만료로 지정된 자원에 접근할 수 없을 떄 사용, refreshToken 을 기반으로 accessToken 재발행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "토큰 재발행 성공"),
            @ApiResponse(responseCode = "400",description = "refreshToken 이 만료되거나 틀렸을 때")
    })
    public ResponseEntity<Void> reIssue(@RequestHeader("accessToken") final String accessToken,
            @RequestHeader("refreshToken") final String refreshToken){

        TokenReIssueResponseDto tokenReIssueResponseDto = authService.reIssue(accessToken,refreshToken);


        return ResponseEntity.status(HttpStatus.OK)
                .header(accessTokenHeader,tokenReIssueResponseDto.getAccessToken())
                .header(refreshTokenHeader,tokenReIssueResponseDto.getRefreshToken())
                .build();

    }



}
