package com.earseo.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(length = 50, nullable = false, unique = true)
    private String nickname;

    @Column(length = 255)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column
    private LocalDate birthdate;

    @Column(length = 100)
    private String nationality;

    public void updateProfile(String nickname, Gender gender, LocalDate birthdate, String nationality) {
        this.nickname = nickname;
        this.gender = gender;
        this.birthdate = birthdate;
        this.nationality = nationality;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
