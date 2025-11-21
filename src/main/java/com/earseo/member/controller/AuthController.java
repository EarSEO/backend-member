package com.earseo.member.controller;

import com.earseo.member.common.BaseResponse;
import com.earseo.member.dto.request.SocialSignUpRequestDto;
import com.earseo.member.dto.request.LoginRequestDto;
import com.earseo.member.dto.request.SignUpRequestDto;
import com.earseo.member.dto.response.SocialLoginResponseDto;
import com.earseo.member.dto.response.LoginResponseDto;
import com.earseo.member.dto.response.SignUpResponseDto;
import com.earseo.member.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일/비밀번호 기반 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<SignUpResponseDto>> signup(@Valid @RequestBody SignUpRequestDto request) {
        SignUpResponseDto response = authService.signup(request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @Operation(summary = "로그인", description = "이메일/비밀번호 기반 로그인 및 JWT 토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @Operation(summary = "구글 로그인", description = "구글 OAuth2 로그인")
    @GetMapping("/oauth/google")
    public ResponseEntity<BaseResponse<SocialLoginResponseDto>> googleCallback(
            @RequestParam("code") String code) {
        SocialLoginResponseDto response = authService.googleLogin(code);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }


    @Operation(summary = "소셜 로그인 추가 정보 입력", description = "신규 소셜 회원 추가 정보 입력 및 회원가입 완료")
    @PostMapping("/oauth/additional-info")
    public ResponseEntity<BaseResponse<LoginResponseDto>> completeSocialSignUp(
            @Valid @RequestBody SocialSignUpRequestDto request) {
        LoginResponseDto response = authService.completeSocialSignUp(request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @Operation(summary = "로그아웃", description = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}
