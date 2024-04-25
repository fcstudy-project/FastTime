package com.fasttime.domain.resume.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.resume.dto.LikeResumeRequest;
import com.fasttime.domain.resume.dto.ResumeDeleteServiceRequest;
import com.fasttime.domain.resume.dto.ResumeRequestDto;
import com.fasttime.domain.resume.dto.ResumeResponseDto;
import com.fasttime.domain.resume.dto.ResumeUpdateServiceRequest;
import com.fasttime.domain.resume.entity.Resume;
import com.fasttime.domain.resume.exception.AlreadyExistsResumeLikeException;
import com.fasttime.domain.resume.exception.NoResumeWriterException;
import com.fasttime.domain.resume.exception.ResumeNotFoundException;
import com.fasttime.domain.resume.exception.UnauthorizedAccessLikeException;
import com.fasttime.domain.resume.repository.LikeRepository;
import com.fasttime.domain.resume.repository.ResumeRepository;
import com.fasttime.domain.resume.service.ResumeService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ResumeServiceTest {

    public static final String MOCK_RESUME_TITLE = "Resume test1";
    public static final String MOCK_RESUME_CONTENT = "# This is Resume";
    public static final long MOCK_RESUME_ID = 1L;
    @Mock
    private MemberService memberService;
    @InjectMocks
    private ResumeService resumeService;
    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private LikeRepository likeRepository;

    @DisplayName("createResume()는")
    @Nested
    class Context_createResume {

        @DisplayName("자기소개서를 DB에 성공적으로 저장한다.")
        @Test
        void _willSuccess() {
            // given
            Member member = Member.builder().id(1L).nickname("testName").build();
            ResumeRequestDto requestDto = new ResumeRequestDto(MOCK_RESUME_TITLE,
                    MOCK_RESUME_CONTENT);
            Resume mockResume = createMockResume(member);

            given(memberService.getMember(1L)).willReturn(member);
            given(resumeRepository.save(any(Resume.class))).willReturn(mockResume);

            // when
            ResumeResponseDto response = resumeService.createResume(requestDto, 1L);
            // then
            assertThat(response).extracting("title", "content", "writer", "likeCount",
                            "viewCount")
                    .containsExactly(MOCK_RESUME_TITLE, MOCK_RESUME_CONTENT, "testName", 0, 0);
        }

    }

    @DisplayName("updateResume()는")
    @Nested
    class Context_updateResume {

        @DisplayName("자기소개서를 성공적으로 편집한다.")
        @Test
        void _willSuccess() {
            // given
            Member member = Member.builder().id(1L).nickname("testName").build();
            Resume mockResume = createMockResume(member);
            String updatedTitle = "updated title";
            String updatedContent = "updated content";
            ResumeUpdateServiceRequest updateRequest = new ResumeUpdateServiceRequest(
                    MOCK_RESUME_ID, 1L,
                    updatedTitle, updatedContent);

            given(memberService.getMember(1L)).willReturn(member);
            given(resumeRepository.findById(MOCK_RESUME_ID)).willReturn(Optional.of(mockResume));

            // when
            ResumeResponseDto response = resumeService.updateResume(updateRequest);

            // then
            assertThat(response).extracting("id", "title", "content", "writer", "likeCount",
                            "viewCount")
                    .containsExactly(1L, updatedTitle, updatedContent, "testName", 0, 0);
        }

        @DisplayName("자기소개서 작성자가 아닌 경우 NoResumeWriterException을 반환한다.")
        @Test
        void resume_validateFail_throwIllegalArgumentException() {
            // given
            Member writer = Member.builder().id(1L).nickname("testName").build();
            Member notAuthorizedMember = Member.builder().id(221L).build();
            Resume mockResume = createMockResume(writer);
            ResumeUpdateServiceRequest updateRequest = new ResumeUpdateServiceRequest(
                    MOCK_RESUME_ID, notAuthorizedMember.getId(),
                    "updateTitle", "updateContent");

            given(memberService.getMember(anyLong())).willReturn(notAuthorizedMember);
            given(resumeRepository.findById(anyLong())).willReturn(Optional.of(mockResume));

            // then
            assertThatThrownBy(() -> resumeService.updateResume(updateRequest))
                    .isInstanceOf(NoResumeWriterException.class);

        }

        @DisplayName("수정할 자기소개서가 없는 경우 ResumeNotFoundException을 반환한다.")
        @Test
        void resume_notExist_throwExceptoin() {
            // given
            ResumeUpdateServiceRequest request = new ResumeUpdateServiceRequest(MOCK_RESUME_ID, 1L,
                    "updateTitle", "updateContent");

            given(resumeRepository.findById(anyLong())).willReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> resumeService.updateResume(request))
                    .isInstanceOf(ResumeNotFoundException.class);
        }


    }

    @DisplayName("deleteResume()는")
    @Nested
    class Context_deleteResume {

        @DisplayName("deleteAt의 시간이 갱신된다")
        @Test
        void _Success() {
            // given
            Member member = Member.builder().id(1L).nickname("testName").build();
            Resume resumeInDb = createMockResume(member);

            given(memberService.getMember(anyLong())).willReturn(member);
            given(resumeRepository.findById(anyLong())).willReturn(Optional.of(resumeInDb));

            ResumeDeleteServiceRequest request = new ResumeDeleteServiceRequest(1L, 1L);

            // when
            resumeService.delete(request);

            // then
            verify(resumeRepository, times(1)).save(resumeInDb);
        }

    }

    @DisplayName("getResume()는")
    @Nested
    class Context_getResume {

        @DisplayName("자기소개서를 성공적으로 불러온다.")
        @Test
        void _willSuccess() {
            // given
            String testRemoteAddress = "test remote address";
            Member member = Member.builder().id(1L).nickname("testName").build();
            Resume resumeInDb = createMockResume(member);

            given(resumeRepository.findById(anyLong())).willReturn(Optional.of(resumeInDb));

            // when
            ResumeResponseDto response = resumeService.getResume(1L, testRemoteAddress);

            // then
            assertThat(response).extracting("id", "title", "content", "writer", "likeCount",
                            "viewCount")
                    .containsExactly(1L, MOCK_RESUME_TITLE, MOCK_RESUME_CONTENT, "testName", 0, 0);
        }

        @DisplayName("존재하지 않는 resumeId로 불러오면 ResumeNotFoundException을 반환한다.")
        @Test
        void resume_idNotExist_throwIllegalException() {
            // given
            given(resumeRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> resumeService.getResume(5L, "test remote address")).isInstanceOf(
                    ResumeNotFoundException.class);
        }
    }

    @DisplayName("likeResume()는")
    @Nested
    class Context_likeResume {

        @DisplayName("성공적으로 좋아요를 한다.")
        @Test
        void _willSuccess() {
            // given
            long memberId = 1L;
            Member member = Member.builder().id(memberId).nickname("testName").build();
            Resume mockResume = createMockResume(member);

            // when
            given(memberService.getMember(memberId)).willReturn(member);
            given(resumeRepository.findById(anyLong())).willReturn(Optional.of(mockResume));
            given(likeRepository.existsByMemberAndResume(any(Member.class),
                    any(Resume.class))).willReturn(Boolean.FALSE);

            resumeService.likeResume(new LikeResumeRequest(MOCK_RESUME_ID, memberId));

            assertThat(mockResume.getLikeCount()).isEqualTo(1);
        }

        @DisplayName("이미 좋아요를 한 경우 AlreadyExistsResumeLikeException을 반환한다.")
        @Test
        void like_alreadyExist_throwException() {
            // given
            long memberId = 1L;
            Member member = Member.builder().id(memberId).nickname("testName").build();
            Resume mockResume = createMockResume(member);

            // when
            given(memberService.getMember(memberId)).willReturn(member);
            given(resumeRepository.findById(anyLong())).willReturn(Optional.of(mockResume));
            given(likeRepository.existsByMemberAndResume(any(Member.class),
                    any(Resume.class))).willReturn(Boolean.TRUE);

            // then
            assertThatThrownBy(
                    () -> resumeService.likeResume(
                            new LikeResumeRequest(MOCK_RESUME_ID, memberId))).isInstanceOf(
                    AlreadyExistsResumeLikeException.class);

        }
    }

    @DisplayName("cancelLike()는")
    @Nested
    class Context_cancelLike {

        @DisplayName("정상적으로 좋아요를 삭제한다.")
        @Test
        void _willSuccess() {
            // given
            long memberId = 1L;
            Member member = Member.builder().id(memberId).nickname("testName").build();
            Resume resume = Resume.builder()
                    .id(MOCK_RESUME_ID)
                    .title(MOCK_RESUME_TITLE)
                    .content(MOCK_RESUME_CONTENT)
                    .likeCount(1)
                    .writer(member)
                    .build();

            // when
            given(memberService.getMember(memberId)).willReturn(member);
            given(resumeRepository.findById(anyLong())).willReturn(Optional.of(resume));
            given(likeRepository.existsByMemberAndResume(member, resume)).willReturn(true);
            resumeService.cancelLike(new LikeResumeRequest(MOCK_RESUME_ID, memberId));

            // then
            assertThat(resume.getLikeCount()).isEqualTo(0);
        }

        @DisplayName("좋아요 삭제 권한이 없는 경우 UnauthorizedAccessLikeException를 반환한다.")
        @Test
        void cancelLike_Unauthorized_throwException() {
            // given
            long memberId = 1L;
            Member member = Member.builder().id(memberId).nickname("testName").build();
            Resume resume = Resume.builder()
                    .id(MOCK_RESUME_ID)
                    .title(MOCK_RESUME_TITLE)
                    .content(MOCK_RESUME_CONTENT)
                    .likeCount(1)
                    .writer(member)
                    .build();
            Member notAuthorMember = Member.builder().id(321L).nickname("not Author").build();

            // when
            given(memberService.getMember(321L)).willReturn(notAuthorMember);
            given(resumeRepository.findById(anyLong())).willReturn(Optional.of(resume));
            given(likeRepository.existsByMemberAndResume(notAuthorMember, resume)).willReturn(
                    false);

            // then
            assertThatThrownBy(
                    () -> resumeService.cancelLike(new LikeResumeRequest(MOCK_RESUME_ID, 321L))).isInstanceOf(
                    UnauthorizedAccessLikeException.class);
        }

        @DisplayName("자기소개서 좋아요 count가 0 이하인 경우 -가 되지 않는다.")
        @Test
        void test2() {
            // given
            long memberId = 1L;
            Member member = Member.builder().id(memberId).nickname("testName").build();
            Resume resume = Resume.builder()
                    .id(MOCK_RESUME_ID)
                    .title(MOCK_RESUME_TITLE)
                    .content(MOCK_RESUME_CONTENT)
                    .likeCount(0)
                    .writer(member)
                    .build();

            assertThat(resume.getLikeCount()).isEqualTo(0);

            // when
            given(memberService.getMember(memberId)).willReturn(member);
            given(resumeRepository.findById(anyLong())).willReturn(Optional.of(resume));
            given(likeRepository.existsByMemberAndResume(member, resume)).willReturn(true);

            resumeService.cancelLike(new LikeResumeRequest(MOCK_RESUME_ID, memberId));

            // then
            assertThat(resume.getLikeCount()).isEqualTo(0);
        }
    }

    private static Resume createMockResume(Member member) {
        return Resume.builder()
                .id(MOCK_RESUME_ID)
                .title(MOCK_RESUME_TITLE)
                .content(MOCK_RESUME_CONTENT)
                .writer(member)
                .build();
    }
}
