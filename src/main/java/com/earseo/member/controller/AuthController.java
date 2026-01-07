package com.earseo.member.controller;

import com.earseo.member.common.BaseResponse;
import com.earseo.member.dto.request.*;
import com.earseo.member.dto.response.*;
import com.earseo.member.service.AuthService;
import com.earseo.member.service.oauth.AppleLoginService;
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
    private final AppleLoginService appleLoginService;

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

    @Operation(summary = "닉네임 중복 확인", description = "닉네임 사용 가능 여부 확인")
    @GetMapping("/nickname/check")
    public ResponseEntity<BaseResponse<NicknameCheckResponseDto>> checkNickname(
            @RequestParam("nickname") String nickname
    ) {
        NicknameCheckResponseDto response = authService.checkNickname(nickname);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새 Access Token 발급")
    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<TokenRefreshResponseDto>> reissue(
            @Valid @RequestBody TokenRefreshRequestDto request
    ) {
        TokenRefreshResponseDto response = authService.reissue(request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @Operation(summary = "이메일 인증코드 발송", description = "회원가입용 이메일 인증코드 발송")
    @PostMapping("/email/signup/send")
    public ResponseEntity<BaseResponse<Void>> sendSignupVerificationCode(
            @Valid @RequestBody EmailVerificationRequestDto request
    ) {
        authService.sendSignupVerificationCode(request);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }

    @Operation(summary = "이메일 인증코드 발송", description = "비밀번호 찾기용 이메일 인증코드 발송")
    @PostMapping("/email/password/send")
    public ResponseEntity<BaseResponse<Void>> sendPasswordResetCode(
            @Valid @RequestBody EmailVerificationRequestDto request
    ) {
        authService.sendPasswordResetCode(request);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }

    @Operation(summary = "이메일 인증코드 검증", description = "이메일 인증코드 확인")
    @PostMapping("/email/verify")
    public ResponseEntity<BaseResponse<Void>> verifyEmail(
            @Valid @RequestBody EmailVerificationConfirmDto request
    ) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }

    @Operation(summary = "비밀번호 재설정", description = "이메일 인증 완료 후 비밀번호 재설정")
    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<Void>> resetPassword(
            @Valid @RequestBody PasswordResetRequestDto request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }

    @Operation(summary = "애플 로그인", description = "Apple identityToken으로 로그인/회원가입 처리")
    @PostMapping("/oauth/apple")
    public ResponseEntity<BaseResponse<LoginResponseDto>> appleLogin(
            @RequestBody AppleLoginRequestDto request) {
        LoginResponseDto response = appleLoginService.login(request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }
}
