package com.earseo.member.controller;

import com.earseo.member.common.BaseResponse;
import com.earseo.member.dto.request.PasswordUpdateRequestDto;
import com.earseo.member.dto.request.ProfileUpdateRequestDto;
import com.earseo.member.dto.response.ProfileResponseDto;
import com.earseo.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "프로필 조회", description = "현재 로그인한 회원의 프로필 정보 조회")
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<ProfileResponseDto>> getProfile() {
        // 추후에 Gateway로 검증 후 헤더에 memberId 전달 (임시로 1L 사용)
        Long memberId = 1L;
        ProfileResponseDto response = memberService.getProfile(memberId);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @Operation(summary = "프로필 수정", description = "회원 정보 수정 (프로필 사진 제외)")
    @PutMapping("/profile")
    public ResponseEntity<BaseResponse<ProfileResponseDto>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequestDto request) {
        // 추후에 Gateway로 검증 후 헤더에 memberId 전달 (임시로 1L 사용)
        Long memberId = 1L;
        ProfileResponseDto response = memberService.updateProfile(memberId, request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경")
    @PutMapping("/password")
    public ResponseEntity<BaseResponse<Void>> updatePassword(
            @Valid @RequestBody PasswordUpdateRequestDto request) {
        // 추후에 Gateway로 검증 후 헤더에 memberId 전달 (임시로 1L 사용)
        Long memberId = 1L;
        memberService.updatePassword(memberId, request);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }


    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 처리")
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> deleteMember() {
        // 추후에 Gateway로 검증 후 헤더에 memberId 전달 (임시로 1L 사용)
        Long memberId = 1L;
        memberService.deleteMember(memberId);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}
