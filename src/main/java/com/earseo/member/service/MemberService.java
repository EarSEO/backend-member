package com.earseo.member.service;

import com.earseo.member.common.exception.BaseException;
import com.earseo.member.common.exception.MemberErrorCode;
import com.earseo.member.dto.event.StoryReportEvent;
import com.earseo.member.dto.request.PasswordUpdateRequestDto;
import com.earseo.member.dto.request.ProfileUpdateRequestDto;
import com.earseo.member.dto.response.*;
import com.earseo.member.entity.EventType;
import com.earseo.member.entity.Member;
import com.earseo.member.entity.Provider;
import com.earseo.member.entity.Status;
import com.earseo.member.repository.EventTypeRepository;
import com.earseo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final EventTypeRepository eventTypeRepository;

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(MemberErrorCode.MEMBER_NOT_FOUND));

        return new ProfileResponseDto(
                member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImage(),
                member.getGender(),
                member.getBirthdate(),
                member.getNationality()
        );
    }

    @Transactional
    public ProfileResponseDto updateProfile(Long memberId, ProfileUpdateRequestDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (!member.getNickname().equals(request.nickname())) {
            if (memberRepository.existsByNickname(request.nickname())) {
                throw new BaseException(MemberErrorCode.DUPLICATE_NICKNAME);
            }
        }

        member.updateProfile(request.nickname(), request.gender(), request.birthdate(), request.nationality());

        return new ProfileResponseDto(
                member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImage(),
                member.getGender(),
                member.getBirthdate(),
                member.getNationality()
        );
    }

    @Transactional
    public void updatePassword(Long memberId, PasswordUpdateRequestDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 소셜 로그인 회원은 비밀번호 변경 불가
        if (!member.getProvider().equals(Provider.LOCAL)) {
            throw new BaseException(MemberErrorCode.SOCIAL_MEMBER_CANNOT_CHANGE_PASSWORD);
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
            throw new BaseException(MemberErrorCode.INVALID_CURRENT_PASSWORD);
        }

        // 새 비밀번호 확인
        if (!request.newPassword().equals(request.newPasswordConfirm())) {
            throw new BaseException(MemberErrorCode.PASSWORD_MISMATCH);
        }

        member.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(MemberErrorCode.MEMBER_NOT_FOUND));

        memberRepository.delete(member);
    }

    @Transactional
    public String updateProfileImage(Long memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 기존 이미지 삭제
        if (member.getProfileImage() != null) {
            s3Service.deleteFile(member.getProfileImage());
        }

        // 새 이미지 업로드
        String imageUrl = s3Service.uploadFile(file, "member");
        member.updateProfileImage(imageUrl);

        return imageUrl;
    }

    @Transactional
    public void memberReported(StoryReportEvent data) {
        Long memberId = data.reportedId();
        Long storyId = data.storyId();
        String type = String.valueOf(memberId) + String.valueOf(storyId);
        if(eventTypeRepository.existsByType(type)){
            return;
        }

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BaseException(MemberErrorCode.MEMBER_NOT_FOUND));

        if(member.getStatus().equals(Status.BANNED)) return;

        member.updateReportCount();

        Long count = member.getReportCount();

        if(count >= 20) {
            member.ban();
        } else if(count >= 15) {
            member.suspend(30);
        } else if(count >= 10) {
            member.suspend(7);
        } else if(count >= 5) {
            member.suspend(3);
        }

        eventTypeRepository.save(EventType.builder()
                        .type(type)
                        .build());
    }
}
