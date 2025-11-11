package com.earseo.backend_member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 테스트용 예시 엔티티
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SampleEntity {
    private Long id;
    private String title;
    private String description;
    private int count;

}
