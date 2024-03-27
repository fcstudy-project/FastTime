package com.fasttime.domain.certification.unit.repository;

import com.fasttime.domain.certification.entity.Certification;
import com.fasttime.domain.certification.entity.CertificationStatus;
import com.fasttime.domain.certification.repository.CertificationRepository;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.entity.Role;
import com.fasttime.global.config.JpaTestConfig;
import com.fasttime.global.config.QueryDslTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaTestConfig.class, QueryDslTestConfig.class})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CertificationRepositoryTest {

    @Autowired
    private CertificationRepository certificationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Member testMember;

    private Certification createTestCertification(Member member, CertificationStatus status) {
        return Certification.builder()
            .member(member)
            .bootcampName("부트캠프 이름")
            .content("인증 내용")
            .image("이미지 URL")
            .status(status)
            .build();
    }

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
            .email("test@example.com")
            .nickname("TestUser")
            .password("password")
            .role(Role.ROLE_USER)
            .build();
        entityManager.persist(testMember);
        entityManager.flush();
    }

    @DisplayName("Member ID로 인증서 조회")
    @Test
    void findByMemberId() {
        // given
        Certification certification = createTestCertification(testMember,
            CertificationStatus.PENDING);
        certificationRepository.save(certification);

        // when
        List<Certification> results = certificationRepository.findByMemberId(testMember.getId());

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(certification);
    }

    @DisplayName("Status로 인증서 조회")
    @Test
    void findByStatus() {
        // given
        Certification certification = createTestCertification(testMember,
            CertificationStatus.PENDING);
        certificationRepository.save(certification);

        // when
        List<Certification> results = certificationRepository.findByStatus(
            CertificationStatus.PENDING);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getStatus()).isEqualTo(CertificationStatus.PENDING);
    }

    @DisplayName("삭제된 멤버의 인증서 삭제")
    @Test
    void deleteCertificationsForDeletedMembers() {
        // given
        Certification certification = createTestCertification(testMember,
            CertificationStatus.PENDING);
        certificationRepository.save(certification);

        certificationRepository.deleteAll();

        entityManager.remove(testMember);
        entityManager.flush();

        // when
        certificationRepository.deleteCertificationsForDeletedMembers();

        // then
        List<Certification> results = certificationRepository.findAll();
        assertThat(results).isEmpty();
    }
}
