package com.earseo.member.repository;

import com.earseo.member.entity.Member;
import com.earseo.member.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 회원 조회
    Optional<Member> findByEmail(String email);

    // 이메일 중복 확인
    boolean existsByEmail(String email);

    // 닉네임 중복 확인
    boolean existsByNickname(String nickname);

    Optional<Member> findByEmailAndProvider(String email, Provider provider);

    // 애플 소셜 로그인용 - providerId로 조회
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);
}
