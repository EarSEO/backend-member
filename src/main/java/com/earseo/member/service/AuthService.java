package com.earseo.member.service;

import com.earseo.member.common.exception.BaseException;
import com.earseo.member.common.exception.MemberErrorCode;
import com.earseo.member.dto.request.*;
import com.earseo.member.dto.response.*;
import com.earseo.member.entity.Member;
import com.earseo.member.entity.Provider;
import com.earseo.member.entity.Role;
import com.earseo.member.repository.MemberRepository;
import com.earseo.member.service.oauth.GoogleOAuthService;
import com.earseo.member.service.oauth.SocialSignUpTempService;
import com.earseo.member.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final GoogleOAuthService googleOAuthService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;
    private final SocialSignUpTempService socialSignUpTempService;

    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${member.default-profile-image}")
    private String defaultProfileImage;

    @Transactional
    public SignUpResponseDto signup(SignUpRequestDto request) {
        // 이메일 인증 완료 여부 확인
        if (!emailVerificationService.isVerified(request.email())) {
            throw new BaseException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }

        validateDuplicateMember(request.email(), Provider.LOCAL);

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BaseException(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .nickname(request.nickname())
                .gender(request.gender())
                .birthdate(request.birthdate())
                .nationality(request.nationality())
                .provider(Provider.LOCAL)
                .profileImage(defaultProfileImage)
                .build();

        Member savedMember = memberRepository.save(member);

        // 회원가입 완료 후 인증 완료 상태 삭제
        emailVerificationService.deleteVerified(request.email());

        return new SignUpResponseDto(
                savedMember.getMemberId(),
                savedMember.getEmail(),
                savedMember.getNickname(),
                savedMember.getRole()
        );
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(()-> new BaseException(MemberErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BaseException(MemberErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtUtil.generateAccessToken(
                member.getMemberId(),
                member.getEmail(),
                member.getRole()
        );

        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId());

        refreshTokenService.saveRefreshToken(
                member.getMemberId(),
                refreshToken,
                jwtUtil.getRefreshTokenExpiration()
        );

        return new LoginResponseDto(
                accessToken,
                refreshToken,
                member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getRole()
        );
    }

    @Transactional
    public SocialLoginResponseDto googleLogin(GoogleLoginRequestDto request) {
        GoogleIdToken.Payload payload = verifyGoogleIdToken(request.idToken());

        String providerId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        Optional<Member> existingMember = memberRepository
                .findByProviderAndProviderId(Provider.GOOGLE, providerId);

        if (existingMember.isPresent()) {
            Member member = existingMember.get();

            String accessToken = jwtUtil.generateAccessToken(
                    member.getMemberId(), member.getEmail(), member.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId());

            refreshTokenService.saveRefreshToken(
                    member.getMemberId(), refreshToken, jwtUtil.getRefreshTokenExpiration());

            return SocialLoginResponseDto.existing(new LoginResponseDto(
                    accessToken, refreshToken, member.getMemberId(),
                    member.getEmail(), member.getNickname(), member.getRole()));
        }

        String tempToken = socialSignUpTempService.createTempToken(providerId);
        return SocialLoginResponseDto.newMember(email, "GOOGLE", tempToken, name);
    }

    private GoogleIdToken.Payload verifyGoogleIdToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new BaseException(MemberErrorCode.INVALID_GOOGLE_TOKEN);
            }
            return idToken.getPayload();
        } catch (Exception e) {
            throw new BaseException(MemberErrorCode.INVALID_GOOGLE_TOKEN);
        }
    }


    @Transactional
    public LoginResponseDto completeSocialSignUp(SocialSignUpRequestDto request) {

        // tempToken으로 providerId 조회
        String providerId = socialSignUpTempService.getProviderId(request.tempToken());
        if (providerId == null) {
            throw new BaseException(MemberErrorCode.INVALID_TEMP_TOKEN);
        }

        // 이미 가입된 회원인지 확인
        validateDuplicateMember(request.email(), request.provider());

        // 닉네임 중복 확인
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BaseException(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        Member member = Member.builder()
                .email(request.email())
                .provider(request.provider())
                .providerId(providerId)
                .nickname(request.nickname())
                .gender(request.gender())
                .birthdate(request.birthdate())
                .nationality(request.nationality())
                .role(Role.USER)
                .profileImage(defaultProfileImage)
                .build();

        Member savedMember = memberRepository.save(member);

        // tempToken 삭제
        socialSignUpTempService.deleteTempToken(request.tempToken());

        String accessToken = jwtUtil.generateAccessToken(
                savedMember.getMemberId(),
                savedMember.getEmail(),
                savedMember.getRole()
        );
        String refreshToken = jwtUtil.generateRefreshToken(savedMember.getMemberId());

        refreshTokenService.saveRefreshToken(
                savedMember.getMemberId(),
                refreshToken,
                jwtUtil.getRefreshTokenExpiration()
        );

        return new LoginResponseDto(
                accessToken,
                refreshToken,
                savedMember.getMemberId(),
                savedMember.getEmail(),
                savedMember.getNickname(),
                savedMember.getRole()
        );
    }

    /**
     * 닉네임 중복 확인
     */
    public NicknameCheckResponseDto checkNickname(String nickname) {
        boolean exists = memberRepository.existsByNickname(nickname);

        if (exists) {
            return new NicknameCheckResponseDto(false, "이미 사용중인 닉네임입니다.");
        }
        return new NicknameCheckResponseDto(true, "사용가능한 닉네임입니다.");
    }

    public void logout(Long memberId) {
        refreshTokenService.deleteRefreshToken(memberId);
    }

    private void validateDuplicateMember(String email, Provider provider) {
        // 같은 이메일, 같은 Provider 확인
        if (memberRepository.findByEmailAndProvider(email, provider).isPresent()) {
            throw new BaseException(MemberErrorCode.DUPLICATE_EMAIL);
        }

        // 같은 이메일로 다른 Provider 가입 여부 확인
        Optional<Member> existingMember = memberRepository.findByEmail(email);
        if (existingMember.isPresent()) {
            throw new BaseException(MemberErrorCode.ALREADY_REGISTERED_WITH_DIFFERENT_PROVIDER);
        }
    }

    @Transactional(readOnly = true)
    public TokenRefreshResponseDto reissue(TokenRefreshRequestDto request) {
        // Refresh Token에서 memberId 추출
        Long memberId;
        try {
            memberId = jwtUtil.getMemberIdFromToken(request.refreshToken());
        } catch (Exception e) {
            throw new BaseException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Redis에 저장된 Refresh Token과 비교
        if (!refreshTokenService.validateRefreshToken(memberId, request.refreshToken())) {
            throw new BaseException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 새 토큰 발급
        String newAccessToken = jwtUtil.generateAccessToken(
                member.getMemberId(),
                member.getEmail(),
                member.getRole()
        );
        String newRefreshToken = jwtUtil.generateRefreshToken(member.getMemberId());

        // 새 Refresh Token Redis 저장 (기존 토큰 덮어쓰기)
        refreshTokenService.saveRefreshToken(
                member.getMemberId(),
                newRefreshToken,
                jwtUtil.getRefreshTokenExpiration()
        );

        return new TokenRefreshResponseDto(newAccessToken, newRefreshToken);
    }

    /**
     * 이메일 인증코드 발송
     */
    public void sendSignupVerificationCode(EmailVerificationRequestDto request) {
        // 이미 가입된 이메일인지 확인
        if (memberRepository.findByEmailAndProvider(request.email(), Provider.LOCAL).isPresent()) {
            throw new BaseException(MemberErrorCode.DUPLICATE_EMAIL);
        }

        // 다른 Provider로 가입된 이메일인지 확인
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new BaseException(MemberErrorCode.ALREADY_REGISTERED_WITH_DIFFERENT_PROVIDER);
        }

        String code = emailVerificationService.generateCode();
        emailVerificationService.saveCode(request.email(), code);
        emailService.sendVerificationCode(request.email(), code);
    }


    public void sendPasswordResetCode(EmailVerificationRequestDto request) {
        // 이미 가입된 이메일인지 확인
        if (!memberRepository.findByEmailAndProvider(request.email(), Provider.LOCAL).isPresent()) {
            throw new BaseException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        // 다른 Provider로 가입된 이메일인지 확인
        if (!memberRepository.findByEmail(request.email()).isPresent()) {
            throw new BaseException(MemberErrorCode.ALREADY_REGISTERED_WITH_DIFFERENT_PROVIDER);
        }

        String code = emailVerificationService.generateCode();
        emailVerificationService.saveCode(request.email(), code);
        emailService.sendVerificationCode(request.email(), code);
    }

    /**
     * 이메일 인증코드 검증
     */
    public void verifyEmail(EmailVerificationConfirmDto request) {
        if (!emailVerificationService.hasCode(request.email())) {
            throw new BaseException(MemberErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        if (!emailVerificationService.verifyCode(request.email(), request.code())) {
            throw new BaseException(MemberErrorCode.INVALID_VERIFICATION_CODE);
        }

        // 인증 성공 시 인증 완료 상태 저장
        emailVerificationService.saveVerified(request.email());

        // 인증코드 삭제
        emailVerificationService.deleteCode(request.email());
    }

    @Transactional
    public void resetPassword(PasswordResetRequestDto request) {
        if (!emailVerificationService.isVerified(request.email())) {
            throw new BaseException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }

        Member member = memberRepository.findByEmailAndProvider(request.email(), Provider.LOCAL)
                .orElseThrow(() -> new BaseException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (passwordEncoder.matches(request.newPassword(), member.getPassword())) {
            throw new BaseException(MemberErrorCode.SAME_AS_CURRENT_PASSWORD);
        }

        member.updatePassword(passwordEncoder.encode(request.newPassword()));
        memberRepository.save(member);

        emailVerificationService.deleteVerified(request.email());
    }
}