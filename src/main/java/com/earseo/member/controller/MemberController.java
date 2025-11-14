package com.earseo.member.controller;

import com.earseo.member.common.BaseResponse;
import com.earseo.member.dto.request.SignUpRequestDto;
import com.earseo.member.dto.response.SignUpResponseDto;
import com.earseo.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "이메일/비밀번호 기반 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<SignUpResponseDto>> signup(
            @Valid @RequestBody SignUpRequestDto request) {
        SignUpResponseDto response = memberService.signup(request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }
}
