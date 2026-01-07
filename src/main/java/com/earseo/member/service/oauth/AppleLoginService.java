package com.earseo.member.service.oauth;

import com.earseo.member.common.exception.BaseException;
import com.earseo.member.dto.request.AppleLoginRequestDto;
import com.earseo.member.dto.response.LoginResponseDto;
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
    public LoginResponseDto login(AppleLoginRequestDto request) {
        Claims claims = appleTokenVerifier.verifyAndGetClaims(request.identityToken());

        String providerId = claims.getSubject();
        String email = claims.get("email", String.class);

        // 이미 Apple로 가입한 사용자인지 확인
        Optional<Member> existingAppleMember = memberRepository
                .findByProviderAndProviderId(Provider.APPLE, providerId);

        if (existingAppleMember.isPresent()) {
            // 기존 Apple 사용자는 로그인 처리
            return generateLoginResponse(existingAppleMember.get());
        }

        if (email != null) {
            Optional<Member> existingEmailMember = memberRepository.findByEmail(email);

            if (existingEmailMember.isPresent() &&
                    existingEmailMember.get().getProvider() != Provider.APPLE) {
                throw new BaseException(MemberErrorCode.ALREADY_REGISTERED_WITH_DIFFERENT_PROVIDER);
            }
        }

        Member newMember = createAppleMember(providerId, email, request.fullName());
        Member savedMember = memberRepository.save(newMember);

        return generateLoginResponse(savedMember);
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