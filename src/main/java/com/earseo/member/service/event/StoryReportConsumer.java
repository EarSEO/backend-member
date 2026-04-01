package com.earseo.member.service.event;

import com.earseo.member.common.KafkaEvent;
import com.earseo.member.common.KafkaEventType;
import com.earseo.member.dto.event.StoryReportEvent;
import com.earseo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoryReportConsumer {

    private final MemberService memberService;

    @KafkaListener(
            topics = "STORY-REPORTED-EVENT"
    )
    public void consume(KafkaEvent<StoryReportEvent> event) {

        StoryReportEvent data = event.getData();
        memberService.memberReported(data);

    }
}
