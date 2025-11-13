package com.earseo.member.controller;

import com.earseo.member.common.BaseResponse;
import com.earseo.member.entity.SampleEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/***
 * 기본 테스트 및 Swagger 적용 테스트용 Controller
 */

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    @Operation(summary = "테스트 API", description = "테스트용 api 입니다.")
    public ResponseEntity<BaseResponse<SampleEntity>> testApi(){
        SampleEntity sampleEntity = SampleEntity.builder()
                .id(1L)
                .count(5)
                .title("Hello World !!")
                .description("test api")
                .build();
        return ResponseEntity.ok(BaseResponse.ok(sampleEntity));
    }
}
