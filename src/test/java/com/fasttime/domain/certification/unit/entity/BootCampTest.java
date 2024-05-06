package com.fasttime.domain.certification.unit.entity;

import com.fasttime.domain.certification.entity.BootCamp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BootCampTest {

    @DisplayName("부트캠프 정보를 등록할 수 있다.")
    @Test
    void create_bootcamp_willSuccess() {
        // given
        String name = "부트캠프 이름";
        String description = "부트캠프 설명";
        String image = "부트캠프 이미지 URL";
        boolean governmentFunded = true;
        String organizer = "주최회사";
        String website = "부트캠프 사이트 URL";
        String course = "코스 설명";

        // when
        BootCamp bootCamp = BootCamp.builder()
            .name(name)
            .description(description)
            .image(image)
            .governmentFunded(governmentFunded)
            .organizer(organizer)
            .website(website)
            .course(course)
            .build();

        // then
        assertThat(bootCamp).extracting("name", "description", "image", "governmentFunded",
                "organizer", "website", "course")
            .containsExactly(name, description, image, governmentFunded, organizer, website,
                course);
    }
}
