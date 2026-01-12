package com.earseo.member.service.oauth;

import com.earseo.member.common.exception.BaseException;
import com.earseo.member.dto.request.AppleLoginRequestDto;
import com.earseo.member.dto.response.LoginResponseDto;
import com.earseo.member.dto.response.SocialLoginResponseDto;
import com.earseo.member.entity.Member;
import com.earseo.member.entity.Provider;
import com.earseo.member.entity.Role;
import com.earseo.member.common.exception.MemberErrorCode;
import com.earseo.member.repository.MemberRepository;
import com.earseo.member.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppleLoginService {

    private final AppleTokenVerifier appleTokenVerifier;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public SocialLoginResponseDto login(AppleLoginRequestDto request) {
        Claims claims = appleTokenVerifier.verifyAndGetClaims(request.identityToken());

        String providerId = claims.getSubject();
        String email = claims.get("email", String.class);

        // 애플은 이메일 비공개일 경우 이메일이 안 올 수 있음 -> providerId로 대체
        if (email == null) {
            email = providerId + "@apple.private";
        }

        // 이미 Apple로 가입한 사용자인지 확인
        Optional<Member> existingMember = memberRepository
                .findByProviderAndProviderId(Provider.APPLE, providerId);

        if (existingMember.isPresent()) {
            Member member = existingMember.get();

            String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), member.getEmail(), member.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId());

            LoginResponseDto loginData = new LoginResponseDto(
                    accessToken, refreshToken, member.getMemberId(),
                    member.getEmail(), member.getNickname(), member.getRole()
            );

            return SocialLoginResponseDto.existing(loginData);
        }

        return SocialLoginResponseDto.newMember(
                email,
                "APPLE",
                providerId,
                request.fullName()
        );
    }

    private Member createAppleMember(String providerId, String email, String fullName) {
        String memberEmail = email != null ? email : providerId + "@apple.private";

        // 닉네임 중복 방지를 위해 항상 UUID 붙이기
        String baseNickname = (fullName != null && !fullName.isBlank())
                ? fullName
                : "User";
        String nickname = baseNickname + "_" + UUID.randomUUID().toString().substring(0, 8);

        // 혹시 중복이면 다시 생성
        while (memberRepository.existsByNickname(nickname)) {
            nickname = baseNickname + "_" + UUID.randomUUID().toString().substring(0, 8);
        }

        return Member.builder()
                .email(memberEmail)
                .provider(Provider.APPLE)
                .providerId(providerId)
                .nickname(nickname)
                .role(Role.USER)
                .password(null)
                .build();
    }

    private LoginResponseDto generateLoginResponse(Member member) {
        String accessToken = jwtUtil.generateAccessToken(
                member.getMemberId(),
                member.getEmail(),
                member.getRole()
        );
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId());

        return new LoginResponseDto(
                accessToken,
                refreshToken,
                member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getRole()
        );
    }
}