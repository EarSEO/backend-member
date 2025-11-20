package com.earseo.member.service;

import com.earseo.member.common.exception.BaseException;
import com.earseo.member.common.exception.MemberErrorCode;
import com.earseo.member.dto.request.SocialSignUpRequestDto;
import com.earseo.member.dto.request.LoginRequestDto;
import com.earseo.member.dto.request.SignUpRequestDto;
import com.earseo.member.dto.response.SocialLoginResponseDto;
import com.earseo.member.dto.response.GoogleUserInfoResponse;
import com.earseo.member.dto.response.LoginResponseDto;
import com.earseo.member.dto.response.SignUpResponseDto;
import com.earseo.member.entity.Member;
import com.earseo.member.entity.Provider;
import com.earseo.member.entity.Role;
import com.earseo.member.repository.MemberRepository;
import com.earseo.member.service.oauth.GoogleOAuthService;
import com.earseo.member.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final GoogleOAuthService googleOAuthService;

    @Transactional
    public SignUpResponseDto signup(SignUpRequestDto request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new BaseException(MemberErrorCode.DUPLICATE_EMAIL);
        }

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
                .build();

        Member savedMember = memberRepository.save(member);

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

        return new LoginResponseDto(
                accessToken,
                refreshToken,
                member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getRole()
        );
    }

    @Transactional(readOnly = true)
    public SocialLoginResponseDto googleLogin(String code) {
        // 구글에서 사용자 정보 조회
        var tokenResponse = googleOAuthService.getAccessToken(code);
        GoogleUserInfoResponse googleUser = googleOAuthService.getUserInfo(tokenResponse.accessToken());

        // DB에서 회원 확인 (email + provider)
        Optional<Member> memberOpt = memberRepository
                .findByEmailAndProvider(googleUser.email(), Provider.GOOGLE);

        if (memberOpt.isPresent()) {
            // 기존 회원 - 로그인 처리
            Member member = memberOpt.get();

            String accessToken = jwtUtil.generateAccessToken(
                    member.getMemberId(),
                    member.getEmail(),
                    member.getRole()
            );
            String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId());

            LoginResponseDto loginResponse = new LoginResponseDto(
                    accessToken,
                    refreshToken,
                    member.getMemberId(),
                    member.getEmail(),
                    member.getNickname(),
                    member.getRole()
            );

            return SocialLoginResponseDto.existing(loginResponse);
        } else {
            // 신규 회원 - 추가 정보 입력 필요
            return SocialLoginResponseDto.newMember(googleUser.email());
        }
    }


    @Transactional
    public LoginResponseDto completeSocialSignUp(SocialSignUpRequestDto request) {
        // 이미 가입된 회원인지 확인
        if (memberRepository.findByEmailAndProvider(request.email(), request.provider()).isPresent()) {
            throw new BaseException(MemberErrorCode.DUPLICATE_EMAIL);
        }

        // 닉네임 중복 확인
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BaseException(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        Member member = Member.builder()
                .email(request.email())
                .provider(request.provider())
                .nickname(request.nickname())
                .gender(request.gender())
                .birthdate(request.birthdate())
                .nationality(request.nationality())
                .role(Role.USER)
                .build();

        Member savedMember = memberRepository.save(member);

        String accessToken = jwtUtil.generateAccessToken(
                savedMember.getMemberId(),
                savedMember.getEmail(),
                savedMember.getRole()
        );
        String refreshToken = jwtUtil.generateRefreshToken(savedMember.getMemberId());

        return new LoginResponseDto(
                accessToken,
                refreshToken,
                savedMember.getMemberId(),
                savedMember.getEmail(),
                savedMember.getNickname(),
                savedMember.getRole()
        );
    }
}