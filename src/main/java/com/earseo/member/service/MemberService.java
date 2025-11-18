package com.earseo.member.service;

import com.earseo.member.common.exception.BaseException;
import com.earseo.member.common.exception.MemberErrorCode;
import com.earseo.member.dto.request.SignUpRequestDto;
import com.earseo.member.dto.response.SignUpResponseDto;
import com.earseo.member.entity.Member;
import com.earseo.member.entity.Provider;
import com.earseo.member.entity.Role;
import com.earseo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignUpResponseDto signup(SignUpRequestDto request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new BaseException(MemberErrorCode.DUPLICATE_EMAIL);
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BaseException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
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
}