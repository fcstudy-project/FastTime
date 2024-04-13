package com.fasttime.domain.certification.unit.repository;

import com.fasttime.domain.certification.entity.BootCamp;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.certification.repository.BootCampRepository;
import com.fasttime.global.config.JpaTestConfig;
import com.fasttime.global.config.QueryDslTestConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({JpaTestConfig.class, QueryDslTestConfig.class})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BootCampRepositoryTest {

    @Autowired
    private BootCampRepository bootCampRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @DisplayName("부트캠프를 저장하고 조회할 수 있다.")
    @Test
    void saveAndFindBootCamp_willSuccess() {
        // given
        BootCamp bootCamp = BootCamp.builder()
            .name("테스트 부트캠프")
            .description("테스트 부트캠프 설명")
            .image("image_url")
            .governmentFunded(false)
            .organizer("테스트 주최자")
            .website("website_url")
            .course("테스트 코스")
            .build();

        // when
        bootCampRepository.save(bootCamp);

        // then
        BootCamp foundBootCamp = bootCampRepository.findById(bootCamp.getId()).orElse(null);
        assertThat(foundBootCamp).isNotNull();
        assertThat(foundBootCamp.getName()).isEqualTo("테스트 부트캠프");
    }

    @DisplayName("부트캠프 이름으로 존재 여부를 확인할 수 있다.")
    @Test
    void existsByName_willSuccess() {
        // given
        String name = "테스트 부트캠프";
        bootCampRepository.save(BootCamp.builder().name(name).build());

        // when
        boolean exists = bootCampRepository.existsByName(name);

        // then
        assertThat(exists).isTrue();
    }
}
