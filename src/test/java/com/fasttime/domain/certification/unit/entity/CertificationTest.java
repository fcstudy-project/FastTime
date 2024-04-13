package com.fasttime.domain.certification.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasttime.domain.certification.entity.Certification;
import com.fasttime.domain.certification.entity.CertificationStatus;
import com.fasttime.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CertificationTest {

    @DisplayName("인증을 요청할 수 있다.")
    @Test
    void create_certification_willSuccess() {
        // given
        String bootcampName = "부트캠프 이름";
        String content = "내용";
        Member member = Mockito.mock(Member.class);

        // when
        Certification certification = Certification.builder()
            .bootcampName(bootcampName)
            .content(content)
            .member(member)
            .build();

        // then
        assertThat(certification).extracting("bootcampName", "content", "member")
            .containsExactly(bootcampName, content, member);
    }

    @DisplayName("인증서 상태를 설정할 수 있다.")
    @Test
    void set_status_willSuccess() {
        // given
        Certification certification = Certification.builder().build();
        CertificationStatus newStatus = CertificationStatus.APPROVED;

        // when
        certification.setStatus(newStatus);

        // then
        assertThat(certification.getStatus()).isEqualTo(newStatus);
    }

    @DisplayName("인증 요청을 철회할 수 있다.")
    @Test
    void delete_certification_willSuccess() {
        // given
        Certification certification = Certification.builder().build();

        // when
        certification.softDelete();

        // then
        assertTrue(certification.isDeleted());
    }
}
