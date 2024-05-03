package com.fasttime.domain.resume.unit.repository;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.resume.dto.ResumesSearchRequest;
import com.fasttime.domain.resume.entity.Resume;
import com.fasttime.domain.resume.repository.ResumeOrderBy;
import com.fasttime.domain.resume.repository.ResumeRepository;
import com.fasttime.global.config.JpaTestConfig;
import com.fasttime.global.config.QueryDslTestConfig;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Slf4j
@Import({JpaTestConfig.class, QueryDslTestConfig.class})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
public class ResumeRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ResumeRepository resumeRepository;

    @DisplayName("search()는 ")
    @Nested
    class Context_pageSearchByDate {

        @DisplayName("성공적으로 좋아요 순서대로 가져온다.")
        @Test
        void resumeOrderByLikeCount() {
            // given
            Member member = Member.builder().id(1L).nickname("testName").build();
            memberRepository.save(member);
            Resume resume1 = Resume.builder()
                    .title("test1")
                    .content("content11")
                    .likeCount(3)
                    .writer(member)
                    .build();

            Resume resume2 = Resume.builder()
                    .title("test2")
                    .content("content22")
                    .writer(member)
                    .build();

            resumeRepository.save(resume1);
            resumeRepository.save(resume2);

            log.info("get resume ID!");
            log.info(String.valueOf(resume1.getId()));
            log.info(String.valueOf(resume2.getId()));

            ResumesSearchRequest resumesSearchRequest = new ResumesSearchRequest(
                    ResumeOrderBy.LIKE_COUNT,
                    0, 10);

            // when
            List<Resume> resumes = resumeRepository.search(resumesSearchRequest);

            // then
            assertThat(resumes.size()).isEqualTo(2);
            assertThat(resumes.get(0)).isEqualTo(resume1);
            assertThat(resumes.get(1)).isEqualTo(resume2);
        }
    }

    @DisplayName("getRecentResumesBySize()는")
    @Nested
    class Context_getRecentResumeBySize{
        @DisplayName("성공적으로 size값으로 자기소개서를 가져온다.")
        @Test
        void _willSuccess(){
            // given
            Member member = Member.builder().id(1L).nickname("testName").build();
            memberRepository.save(member);
            Resume resume1 = Resume.builder()
                    .title("test1")
                    .content("content11")
                    .writer(member)
                    .build();

            Resume resume2 = Resume.builder()
                    .title("test2")
                    .content("content22")
                    .writer(member)
                    .build();


            resumeRepository.save(resume1);
            resumeRepository.save(resume2);
            List<Resume> resumes = resumeRepository.getRecentResumesBySizeExceptIds(1, List.of());

            assertThat(resumes.size()).isEqualTo(1);
            assertThat(resumes.getFirst()).extracting("title", "content")
                    .containsExactly("test2", "content22");
        }

        @DisplayName("성공적으로 id를 제외해서 자기소개서를 가져온다.")
        @Test
        void _willSuccess_ExceptIds(){
            // given
            Member member = Member.builder().id(1L).nickname("testName").build();
            memberRepository.save(member);
            Resume resume1 = Resume.builder()
                    .id(1L)
                    .title("test1")
                    .content("content11")
                    .writer(member)
                    .build();

            Resume resume2 = Resume.builder()
                    .id(2L)
                    .title("test2")
                    .content("content22")
                    .writer(member)
                    .build();


            resumeRepository.save(resume1);
            resumeRepository.save(resume2);
            List<Resume> resumes = resumeRepository.getRecentResumesBySizeExceptIds(1, List.of(2L));

            assertThat(resumes.size()).isEqualTo(1);
            assertThat(resumes.getFirst()).extracting("title", "content")
                    .containsExactly("test1", "content11");
        }

    }
}
