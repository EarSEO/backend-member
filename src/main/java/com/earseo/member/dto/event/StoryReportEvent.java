package com.earseo.member.dto.event;

public record StoryReportEvent(Long storyId, Long reportedId, String description) {
}
