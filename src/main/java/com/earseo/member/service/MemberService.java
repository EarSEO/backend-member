package com.earseo.member.service;

import com.earseo.member.common.exception.BaseException;
import com.earseo.member.common.exception.MemberErrorCode;
import com.earseo.member.dto.request.LoginRequestDto;
import com.earseo.member.dto.request.SignUpRequestDto;
import com.earseo.member.dto.response.LoginResponseDto;
import com.earseo.member.dto.response.SignUpResponseDto;
import com.earseo.member.entity.Member;
import com.earseo.member.entity.Provider;
import com.earseo.member.entity.Role;
import com.earseo.member.repository.MemberRepository;
import com.earseo.member.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
}