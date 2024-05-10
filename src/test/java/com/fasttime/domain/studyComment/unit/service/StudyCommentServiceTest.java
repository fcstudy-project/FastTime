package com.fasttime.domain.studyComment.unit.service;

import com.fasttime.domain.comment.dto.request.CommentPageRequestDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.study.dto.StudyResponse;
import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.study.exception.StudyNotFoundException;
import com.fasttime.domain.study.repository.StudyRepository;
import com.fasttime.domain.studyComment.dto.request.CreateStudyCommentRequestDTO;
import com.fasttime.domain.studyComment.dto.request.GetStudyCommentsRequestDTO;
import com.fasttime.domain.studyComment.dto.request.StudyCommentPageRequestDTO;
import com.fasttime.domain.studyComment.dto.request.UpdateStudyCommentRequestDTO;
import com.fasttime.domain.studyComment.dto.response.StudyCommentListResponseDTO;
import com.fasttime.domain.studyComment.dto.response.StudyCommentResponseDTO;
import com.fasttime.domain.studyComment.entity.StudyComment;
import com.fasttime.domain.studyComment.exception.NotStudyCommentAuthorException;
import com.fasttime.domain.studyComment.exception.StudyCommentNotFoundException;
import com.fasttime.domain.studyComment.repository.StudyCommentRepository;
import com.fasttime.domain.studyComment.service.StudyCommentService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class StudyCommentServiceTest {

    @InjectMocks
    private StudyCommentService studyCommentService;

    @Mock
    private StudyCommentRepository studyCommentRepository;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private MemberService memberService;

    private StudyResponse newStudyResponse() {
        return StudyResponse.builder()
                .id(1L).nickname("부캠러")
                .title("Test 스터디 제목")
                .content("Test 스터디 본문")
                .skill("파이썬 자바")
                .recruitmentStart(LocalDate.now())
                .recruitmentEnd(LocalDate.now().plusMonths(1))
                .progressStart(LocalDate.now().plusMonths(1))
                .progressEnd(LocalDate.now().plusMonths(3))
                .total(10)
                .applicant(0)
                .current(0)
                .categories(List.of("면접 준비", "알고리즘"))
                .contact("010-0000-0000").build();
    }

    private Study newStudy() {
        return Study.builder()
                .id(1L)
                .title("Test 스터디 제목")
                .content("Test 스터디 본문")
                .skill("파이썬 자바")
                .recruitmentStart(LocalDate.now())
                .recruitmentEnd(LocalDate.now().plusMonths(1))
                .progressStart(LocalDate.now().plusMonths(1))
                .progressEnd(LocalDate.now().plusMonths(3))
                .total(10)
                .applicant(0)
                .current(0)
                .contact("010-0000-0000").build();

    }

    private Member newMember() {
        return Member.builder()
            .id(1L)
            .email("email")
            .password("password")
            .nickname("nickname")
            .image("imageUrl")
            .build();
    }

    @Nested
    @DisplayName("createComment()는 ")
    class Context_createComment {

        @Test
        @DisplayName("스터디 모집글에 댓글을 등록할 수 있다.")
        void anonymousComment_willSuccess() {
            // given
            CreateStudyCommentRequestDTO createCommentRequestDTO = CreateStudyCommentRequestDTO.builder().
                content("content")
                .parentStudyCommentId(null)
                .build();
            given(studyRepository.findById(anyLong())).willReturn(Optional.ofNullable(newStudy()));
            given(memberService.getMember(any(Long.class))).willReturn(newMember());
            given(studyCommentRepository.save(any(StudyComment.class))).willReturn(
                StudyComment.builder()
                    .id(1L)
                    .study(newStudy())
                    .member(newMember())
                    .content("content")
                    .parentStudyComment(null)
                    .build());

            // when
            StudyCommentResponseDTO commentResponseDTO = studyCommentService.createComment(1L, 1L,
                createCommentRequestDTO);

            // then
            assertThat(commentResponseDTO).extracting("commentId", "studyId", "memberId",
                    "nickname", "content", "parentStudyCommentId", "childStudyCommentCount")
                .containsExactly(1L, 1L, 1L, "nickname", "content", -1L, 0);
            verify(studyCommentRepository, never()).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(studyCommentRepository, times(1)).save(any(StudyComment.class));
        }

        @Test
        @DisplayName("스터디 모집글에 대댓글을 등록할 수 있다.")
        void nonAnonymousChildComment_willSuccess() {
            // given
            CreateStudyCommentRequestDTO createCommentRequestDTO = CreateStudyCommentRequestDTO.builder()
                .content("content2")
                .parentStudyCommentId(1L)
                .build();
            StudyComment parentComment = StudyComment.builder()
                .id(1L)
                .study(newStudy())
                .member(newMember())
                .content("content1")
                .parentStudyComment(null)
                .build();
            given(studyCommentRepository.findById(any(Long.class))).willReturn(
                Optional.of(parentComment));
            given(studyRepository.findById(anyLong())).willReturn(Optional.ofNullable(newStudy()));
            given(memberService.getMember(any(Long.class))).willReturn(newMember());
            given(studyCommentRepository.save(any(StudyComment.class))).willReturn(
                StudyComment.builder()
                    .id(2L)
                    .study(newStudy())
                    .member(newMember())
                    .content("content2")
                    .parentStudyComment(parentComment)
                    .build());

            // when
            StudyCommentResponseDTO commentResponseDTO = studyCommentService.createComment(1L, 1L,
                createCommentRequestDTO);

            // then
            assertThat(commentResponseDTO).extracting("commentId", "studyId", "memberId",
                    "nickname", "content", "parentStudyCommentId", "childStudyCommentCount")
                .containsExactly(2L, 1L, 1L, "nickname", "content2", 1L, 0);
            verify(studyCommentRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(studyCommentRepository, times(1)).save(any(StudyComment.class));
        }

        @Test
        @DisplayName("스터디 모집글을 찾을 수 없으면 댓글을 등록할 수 없다.")
        void articleNotFound_willFail() {
            // given
            CreateStudyCommentRequestDTO createCommentRequestDTO = CreateStudyCommentRequestDTO.builder()
                .content("content")
                .parentStudyCommentId(null)
                .build();
            given(studyRepository.findById(anyLong())).willThrow(new StudyNotFoundException());

            // when, then
            Throwable exception = assertThrows(StudyNotFoundException.class, () -> {
                studyCommentService.createComment(1L, 1L, createCommentRequestDTO);
            });
            assertEquals("존재하지 않는 스터디게시판입니다.", exception.getMessage());
            verify(studyCommentRepository, never()).findById(any(Long.class));
            verify(memberService, never()).getMember(any(Long.class));
            verify(studyCommentRepository, never()).save(any(StudyComment.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 대댓글을 등록할 수 없다.")
        void parentCommentNotFound_willFail() {
            // given
            CreateStudyCommentRequestDTO createCommentRequestDTO = CreateStudyCommentRequestDTO.builder()
                .content("test")
                .parentStudyCommentId(1L)
                .build();
            given(studyCommentRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when, then
            Throwable exception = assertThrows(StudyCommentNotFoundException.class, () -> {
                studyCommentService.createComment(1L, 1L, createCommentRequestDTO);
            });
            assertEquals("존재하지 않는 스터디 댓글입니다.", exception.getMessage());
            verify(studyCommentRepository, times(1)).findById(any(Long.class));
            verify(memberService, never()).getMember(any(Long.class));
            verify(studyCommentRepository, never()).save(any(StudyComment.class));
        }
    }

    @Nested
    @DisplayName("getComments()는 ")
    class Context_getComments {

        @Test
        @DisplayName("해당 스터디 모집글의 댓글들을 불러올 수 있다.")
        void getCommentsByArticle_willSuccess() {
            // given
            GetStudyCommentsRequestDTO getCommentsRequestDTO = GetStudyCommentsRequestDTO.builder()
                .studyId(1L)
                .build();
            Pageable pageable = StudyCommentPageRequestDTO.builder()
                .page(0)
                .size(10)
                .build().of();
            StudyComment comment1 = StudyComment.builder()
                .id(1L)
                .study(newStudy())
                .member(newMember())
                .content("content1")
                .parentStudyComment(null)
                .build();
            StudyComment comment2 = StudyComment.builder()
                .id(2L)
                .study(newStudy())
                .member(newMember())
                .content("content2")
                .parentStudyComment(comment1)
                .build();
            StudyComment comment3 = StudyComment.builder()
                .id(3L)
                .study(newStudy())
                .member(newMember())
                .content("content3")
                .parentStudyComment(null)
                .build();
            comment1.getChildStudyComments().add(comment2);
            given(studyCommentRepository.findAllBySearchCondition(
                any(GetStudyCommentsRequestDTO.class), any(Pageable.class))).willReturn(
                new PageImpl<>(List.of(comment1, comment3)));

            // when
            StudyCommentListResponseDTO result = studyCommentService.getComments(getCommentsRequestDTO,
                pageable);

            // then
            assertThat(result.getComments().get(0)).extracting("commentId", "studyId", "memberId",
                    "nickname", "content",  "parentStudyCommentId", "childStudyCommentCount")
                .containsExactly(1L, 1L, 1L, "nickname", "content1", -1L, 1);
            assertThat(result.getComments().get(1)).extracting("commentId", "studyId", "memberId",
                    "nickname", "content",  "parentStudyCommentId", "childStudyCommentCount")
                .containsExactly(3L, 1L, 1L, "nickname", "content3", -1L, 0);
            verify(studyCommentRepository, times(1)).findAllBySearchCondition(
                any(GetStudyCommentsRequestDTO.class), any(Pageable.class));
        }

        @Test
        @DisplayName("해당 회원의 댓글들을 불러올 수 있다.")
        void getCommentsByMember_willSuccess() {
            // given
            GetStudyCommentsRequestDTO getCommentsRequestDTO = GetStudyCommentsRequestDTO.builder()
                .memberId(1L)
                .build();
            Pageable pageable = StudyCommentPageRequestDTO.builder()
                .page(0)
                .size(10)
                .build().of();
            StudyComment comment1 = StudyComment.builder()
                .id(1L)
                .study(newStudy())
                .member(newMember())
                .content("content1")
                .parentStudyComment(null).build();
            StudyComment comment2 = StudyComment.builder()
                .id(2L)
                .study(newStudy())
                .member(newMember())
                .content("content2")
                .parentStudyComment(comment1)
                .build();
            StudyComment comment3 = StudyComment.builder()
                .id(3L)
                .study(newStudy())
                .member(newMember())
                .content("content3")
                .parentStudyComment(null)
                .build();
            comment1.getChildStudyComments().add(comment2);
            given(studyCommentRepository.findAllBySearchCondition(
                any(GetStudyCommentsRequestDTO.class), any(Pageable.class))).willReturn(
                new PageImpl<>(List.of(comment1, comment2, comment3)));

            // when
            StudyCommentListResponseDTO result = studyCommentService.getComments(getCommentsRequestDTO,
                pageable);

            // then
            assertThat(result.getComments().get(0)).extracting("commentId", "studyId", "memberId",
                    "nickname", "content", "parentStudyCommentId", "childStudyCommentCount")
                .containsExactly(1L, 1L, 1L, "nickname", "content1", -1L, 1);
            assertThat(result.getComments().get(1)).extracting("commentId", "studyId", "memberId",
                    "nickname", "content", "parentStudyCommentId", "childStudyCommentCount")
                .containsExactly(2L, 1L, 1L, "nickname", "content2", 1L, 0);
            assertThat(result.getComments().get(2)).extracting("commentId", "studyId", "memberId",
                    "nickname", "content",  "parentStudyCommentId", "childStudyCommentCount")
                .containsExactly(3L, 1L, 1L, "nickname", "content3", -1L, 0);
            verify(studyCommentRepository, times(1)).findAllBySearchCondition(
                any(GetStudyCommentsRequestDTO.class), any(Pageable.class));
        }

        @Test
        @DisplayName("해당 댓글의 대댓글들을 불러올 수 있다.")
        void getCommentsByParentComment_willSuccess() {
            // given
            GetStudyCommentsRequestDTO getCommentsRequestDTO = GetStudyCommentsRequestDTO.builder()
                .parentStudyCommentId(1L)
                .build();
            Pageable pageable = CommentPageRequestDTO.builder()
                .page(0)
                .size(10)
                .build().of();
            StudyComment comment1 = StudyComment.builder()
                .id(1L)
                .study(newStudy())
                .member(newMember())
                .content("content1")
                .parentStudyComment(null)
                .build();
            StudyComment comment2 = StudyComment.builder()
                .id(2L)
                .study(newStudy())
                .member(newMember())
                .content("content2")
                .parentStudyComment(comment1)
                .build();
            comment1.getChildStudyComments().add(comment2);
            given(studyCommentRepository.findAllBySearchCondition(
                any(GetStudyCommentsRequestDTO.class), any(Pageable.class))).willReturn(
                new PageImpl<>(List.of(comment2)));

            // when
            StudyCommentListResponseDTO result = studyCommentService.getComments(getCommentsRequestDTO,
                pageable);

            // then
            assertThat(result.getComments().get(0)).extracting("commentId", "studyId", "memberId",
                    "nickname", "content", "parentStudyCommentId", "childStudyCommentCount")
                .containsExactly(2L, 1L, 1L, "nickname", "content2", 1L, 0);
            verify(studyCommentRepository, times(1)).findAllBySearchCondition(
                any(GetStudyCommentsRequestDTO.class), any(Pageable.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 빈 리스트를 반환한다.")
        void CommentNotFound_willFail() {
            // given
            Pageable pageable = CommentPageRequestDTO.builder()
                .page(0)
                .size(10)
                .build().of();
            given(studyCommentRepository.findAllBySearchCondition(
                any(GetStudyCommentsRequestDTO.class), any(Pageable.class))).willReturn(
                new PageImpl<>(new ArrayList<>()));

            // when
            StudyCommentListResponseDTO result = studyCommentService.getComments(
                GetStudyCommentsRequestDTO.builder().studyId(1L).build(), pageable);

            // then
            assertThat(result.getComments()).isEmpty();
            verify(studyCommentRepository, times(1)).findAllBySearchCondition(
                any(GetStudyCommentsRequestDTO.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("updateComment()는 ")
    class Context_updateComment {

        @Test
        @DisplayName("댓글을 수정할 수 있다.")
        void _willSuccess() {
            // given
            UpdateStudyCommentRequestDTO updateCommentRequestDTO = UpdateStudyCommentRequestDTO.builder()
                .content("content2")
                .build();
            given(studyCommentRepository.findById(any(Long.class))).willReturn(Optional.of(
                StudyComment.builder()
                    .id(1L)
                    .study(newStudy())
                    .member(newMember())
                    .content("content1")
                    .parentStudyComment(null)
                    .build()));

            // when
            StudyCommentResponseDTO commentResponseDTO = studyCommentService.updateComment(1L,
                1L, updateCommentRequestDTO);

            // then
            assertThat(commentResponseDTO).extracting("commentId", "studyId", "memberId",
                    "nickname", "content", "parentStudyCommentId", "childStudyCommentCount")
                .containsExactly(1L, 1L, 1L, "nickname", "content2", -1L, 0);
            verify(studyCommentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글 작성자가 아니면 댓글을 수정할 수 없다.")
        void CommentUnauthorized_willFail() {
            // given
            UpdateStudyCommentRequestDTO updateCommentRequestDTO = UpdateStudyCommentRequestDTO.builder()
                .content("content2")
                .build();
            given(studyCommentRepository.findById(any(Long.class))).willReturn(Optional.of(
                StudyComment.builder()
                    .id(1L)
                    .study(newStudy())
                    .member(newMember())
                    .content("content1")
                    .parentStudyComment(null)
                    .build()));

            // when, then
            Throwable exception = assertThrows(NotStudyCommentAuthorException.class, () -> {
                studyCommentService.updateComment(1L, 2L, updateCommentRequestDTO);
            });
            assertEquals("스터디 댓글 작성자만 해당 스터디 댓글 수정/삭제가 가능합니다.", exception.getMessage());
            verify(studyCommentRepository, times(1)).findById(any(Long.class));
        }
    }

    @Nested
    @DisplayName("deleteComment()는 ")
    class Context_deleteComment {

        @Test
        @DisplayName("댓글을 삭제할 수 있다.")
        void _willSuccess() {
            // given
            given(studyCommentRepository.findById(any(Long.class))).willReturn(Optional.of(
                StudyComment.builder()
                    .id(1L)
                    .study(newStudy())
                    .member(newMember())
                    .content("content")
                    .parentStudyComment(null)
                    .build()));

            // when
            StudyCommentResponseDTO commentResponseDTO = studyCommentService.deleteComment(1L, 1L);

            // then
            assertThat(commentResponseDTO).extracting("commentId", "studyId", "memberId",
                    "nickname", "content","parentStudyCommentId", "childStudyCommentCount")
                .containsExactly(1L, 1L, 1L, "nickname", "content", -1L, 0);
            verify(studyCommentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글 작성자가 아니면 댓글을 삭제할 수 없다.")
        void CommentUnauthorized_willFail() {
            // given
            given(studyCommentRepository.findById(any(Long.class))).willReturn(Optional.of(
                StudyComment.builder()
                    .id(1L)
                    .study(newStudy())
                    .member(newMember())
                    .content("content1")
                    .parentStudyComment(null)
                    .build()));

            // when, then
            Throwable exception = assertThrows(NotStudyCommentAuthorException.class, () -> {
                studyCommentService.deleteComment(1L, 2L);
            });
            assertEquals("스터디 댓글 작성자만 해당 스터디 댓글 수정/삭제가 가능합니다.", exception.getMessage());
            verify(studyCommentRepository, times(1)).findById(any(Long.class));
        }
    }

    @Nested
    @DisplayName("getComment()는 ")
    class Context_getComment {

        @Test
        @DisplayName("댓글을 가져올 수 있다.")
        void _willSuccess() {
            // given
            Study study = newStudy();
            Member member = newMember();
            given(studyCommentRepository.findById(any(Long.class))).willReturn(Optional.of(
                StudyComment.builder().id(1L).study(study).member(member).content("test")
                    .parentStudyComment(null).build()));

            // when
            StudyComment result = studyCommentService.getComment(1L);

            // then
            assertThat(result).extracting("id", "study", "member", "content",
                    "parentStudyComment", "childStudyComments")
                .containsExactly(1L, study, member, "test", null,
                    new ArrayList<>());

            verify(studyCommentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 가져올 수 없다.")
        void CommentNotFound_willFail() {
            // given
            given(studyCommentRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when, then
            Throwable exception = assertThrows(StudyCommentNotFoundException.class, () -> {
                studyCommentService.getComment(1L);
            });
            assertEquals("존재하지 않는 스터디 댓글입니다.", exception.getMessage());
            verify(studyCommentRepository, times(1)).findById(any(Long.class));

        }
    }
}
