package com.earseo.member.service.oauth;

import com.earseo.member.dto.request.AppleLoginRequestDto;
import com.earseo.member.dto.response.LoginResponseDto;
import com.earseo.member.dto.response.SocialLoginResponseDto;
import com.earseo.member.entity.Member;
import com.earseo.member.entity.Provider;
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
    private final SocialSignUpTempService socialSignUpTempService;

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

        String tempToken = socialSignUpTempService.createTempToken(providerId);

        return SocialLoginResponseDto.newMember(
                email,
                "APPLE",
                tempToken,
                request.fullName()
        );
    }
}