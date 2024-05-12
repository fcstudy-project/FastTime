package com.fasttime.domain.studyComment.service;

import com.fasttime.domain.comment.exception.MultipleSearchConditionException;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.study.exception.StudyNotFoundException;
import com.fasttime.domain.study.repository.StudyRepository;
import com.fasttime.domain.studyComment.dto.request.CreateStudyCommentRequestDTO;
import com.fasttime.domain.studyComment.dto.request.GetStudyCommentsRequestDTO;
import com.fasttime.domain.studyComment.dto.request.UpdateStudyCommentRequestDTO;
import com.fasttime.domain.studyComment.dto.response.StudyCommentListResponseDTO;
import com.fasttime.domain.studyComment.dto.response.StudyCommentResponseDTO;
import com.fasttime.domain.studyComment.entity.StudyComment;
import com.fasttime.domain.studyComment.exception.NotStudyCommentAuthorException;
import com.fasttime.domain.studyComment.exception.StudyCommentNotFoundException;
import com.fasttime.domain.studyComment.repository.StudyCommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyCommentService {

    private final StudyCommentRepository studyCommentRepository;
    private final StudyRepository studyRepository;
    private final MemberService memberService;

    public StudyCommentResponseDTO createComment(long studyId, long memberId,
        CreateStudyCommentRequestDTO createStudyCommentRequestDTO) {
        boolean isChildComment = createStudyCommentRequestDTO.getParentStudyCommentId() != null;
        StudyComment parentComment =
            isChildComment ? getComment(createStudyCommentRequestDTO.getParentStudyCommentId()) : null;

        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);

        StudyComment savedComment = studyCommentRepository.save(StudyComment.builder()
            .study(study)
            .member(memberService.getMember(memberId))
            .content(createStudyCommentRequestDTO.getContent())
            .parentStudyComment(parentComment)
            .build());

        return savedComment.toCommentResponseDTO();
    }

    public StudyCommentListResponseDTO getComments(GetStudyCommentsRequestDTO getStudyCommentsRequestDTO,
                                                   Pageable pageable) {
        checkDuplicateCondition(getStudyCommentsRequestDTO);
        List<StudyCommentResponseDTO> comments = new ArrayList<>();
        Page<StudyComment> commentsFromDB = studyCommentRepository.findAllBySearchCondition(
                getStudyCommentsRequestDTO, pageable);
        commentsFromDB.forEach(comment -> comments.add(comment.toCommentResponseDTO()));

        return StudyCommentListResponseDTO.builder()
            .totalPages(commentsFromDB.getTotalPages())
            .isLastPage(commentsFromDB.isLast())
            .totalComments(getTotalComments(getStudyCommentsRequestDTO, commentsFromDB))
            .comments(comments)
            .build();
    }

    public StudyCommentResponseDTO updateComment(long studyCommentId, long memberId,
        UpdateStudyCommentRequestDTO updateStudyCommentRequestDTO) {
        StudyComment comment = getComment(studyCommentId);
        if (memberId != comment.getMember().getId()) {
            throw new NotStudyCommentAuthorException();
        }
        comment.updateContent(updateStudyCommentRequestDTO.getContent());
        return comment.toCommentResponseDTO();
    }

    public StudyCommentResponseDTO deleteComment(long studyCommentId, long memberId) {
        StudyComment comment = getComment(studyCommentId);
        if (memberId != comment.getMember().getId()) {
            throw new NotStudyCommentAuthorException();
        }
        comment.delete(LocalDateTime.now());

        return comment.toCommentResponseDTO();
    }

    public StudyComment getComment(Long id) {
        return studyCommentRepository.findById(id).orElseThrow(StudyCommentNotFoundException::new);
    }

    private void checkDuplicateCondition(GetStudyCommentsRequestDTO getStudyCommentsRequestDTO) {
        int count = 0;
        if (isFindByArticleId(getStudyCommentsRequestDTO)) {
            count++;
        }
        if (isFindByMemberId(getStudyCommentsRequestDTO)) {
            count++;
        }
        if (isFindByParentCommentId(getStudyCommentsRequestDTO)) {
            count++;
        }
        if (count > 1) {
            throw new MultipleSearchConditionException();
        }
    }

    private long getTotalComments(GetStudyCommentsRequestDTO GetStudyCommentsRequestDTO,
        Page<StudyComment> commentsFromDB) {
        return isFindByArticleId(GetStudyCommentsRequestDTO) ? studyCommentRepository.countByStudyID(
                GetStudyCommentsRequestDTO.getStudyId()) : commentsFromDB.getTotalElements();
    }

    private boolean isFindByArticleId(GetStudyCommentsRequestDTO getStudyCommentsRequestDTO) {
        return getStudyCommentsRequestDTO.getStudyId() != null;
    }

    private boolean isFindByMemberId(GetStudyCommentsRequestDTO getStudyCommentsRequestDTO) {
        return getStudyCommentsRequestDTO.getMemberId() != null;
    }

    private boolean isFindByParentCommentId(GetStudyCommentsRequestDTO getStudyCommentsRequestDTO) {
        return getStudyCommentsRequestDTO.getParentStudyCommentId() != null;
    }
}
