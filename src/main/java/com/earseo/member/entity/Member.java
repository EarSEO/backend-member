package com.earseo.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(length = 255)
    private String providerId;

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
    private  String nationality;

    @Column
    @Builder.Default
    private Long reportCount = 0L;

    @Column
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Column
    private LocalDateTime suspendEndDate;



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

    public void updateReportCount(){
        this.reportCount = this.reportCount+1;
    }

    public void ban() {
        this.status = Status.BANNED;
        this.suspendEndDate = null;
    }

    public void suspend(int i) {
        this.status = Status.SUSPENDED;
        this.suspendEndDate = LocalDateTime.now().plusDays(i);
    }
}
