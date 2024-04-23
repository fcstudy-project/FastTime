package com.fasttime.domain.resume.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.resume.dto.LikeResumeRequest;
import com.fasttime.domain.resume.dto.ResumeDeleteServiceRequest;
import com.fasttime.domain.resume.dto.ResumeRequestDto;
import com.fasttime.domain.resume.dto.ResumeResponseDto;
import com.fasttime.domain.resume.dto.ResumeUpdateServiceRequest;
import com.fasttime.domain.resume.dto.ResumesSearchRequest;
import com.fasttime.domain.resume.entity.Like;
import com.fasttime.domain.resume.entity.Resume;
import com.fasttime.domain.resume.exception.AlreadyExistsResumeLikeException;
import com.fasttime.domain.resume.exception.NoResumeWriterException;
import com.fasttime.domain.resume.exception.ResumeAlreadyDeletedException;
import com.fasttime.domain.resume.exception.ResumeNotFoundException;
import com.fasttime.domain.resume.exception.UnauthorizedAccessLikeException;
import com.fasttime.domain.resume.repository.LikeRepository;
import com.fasttime.domain.resume.repository.ResumeRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ResumeService {

    private final LikeRepository likeRepository;
    private final ResumeRepository resumeRepository;
    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplate;


    public ResumeResponseDto getResume(Long id, String remoteAddr) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeNotFoundException(id));
        isDeleted(resume);
        addViewCntToRedis(id, remoteAddr);
        return buildResumeResponse(resume);
    }

    public ResumeResponseDto createResume(ResumeRequestDto requestDto, Long memberId) {
        Member member = memberService.getMember(memberId);
        final Resume newResume = Resume.builder()
                .title(requestDto.title())
                .content(requestDto.content())
                .writer(member)
                .build();
        Resume createdResume = resumeRepository.save(newResume);

        return buildResumeResponse(createdResume);
    }

    public ResumeResponseDto updateResume(ResumeUpdateServiceRequest request) {
        final Member requestMember = memberService.getMember(
                request.memberId());
        Resume resume = findResumeById(request.resumeId());

        isDeleted(resume);
        isWriter(requestMember, resume);

        resume.updateResume(request.title(), request.content());
        resumeRepository.save(resume);

        return buildResumeResponse(resume);
    }

    public List<ResumeResponseDto> search(ResumesSearchRequest request) {
        List<Resume> resumes = resumeRepository.search(request);
        return resumes.stream()
                .map(this::buildResumeResponse)
                .collect(Collectors.toList());
    }

    public void delete(ResumeDeleteServiceRequest deleteRequest) {
        final Member deleteRequestMember = memberService.getMember(
                deleteRequest.requestUserId());
        final Resume resume = findResumeById(deleteRequest.resumeId());

        isDeleted(resume);
        isWriter(deleteRequestMember, resume);
        resume.delete(LocalDateTime.now());
        resumeRepository.save(resume);
    }

    public void likeResume(LikeResumeRequest likeResumeRequest) {
        Member member = memberService.getMember(likeResumeRequest.memberId());
        Resume resume = resumeRepository.findById(likeResumeRequest.resumeId())
                .orElseThrow(() -> new ResumeNotFoundException(likeResumeRequest.resumeId()));
        if (likeRepository.existsByMemberAndResume(member, resume)) {
            throw new AlreadyExistsResumeLikeException();
        }

        resume.like();
        likeRepository.save(Like.builder().resume(resume).member(member).build());
    }

    public void cancelLike(LikeResumeRequest likeResumeRequest) {
        Member member = memberService.getMember(likeResumeRequest.memberId());
        Resume resume = resumeRepository.findById(likeResumeRequest.resumeId())
                .orElseThrow(ResumeNotFoundException::new);

        if (!likeRepository.existsByMemberAndResume(member, resume)) {
            throw new UnauthorizedAccessLikeException();
        }
        resume.cancelLike();
        likeRepository.deleteByMemberAndResume(member, resume);
    }

    private void addViewCntToRedis(Long resumeId, String remoteAddr) {
        String key = "resumeId: " + resumeId;
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        if (Boolean.TRUE.equals(setOperations.isMember(key, remoteAddr))) {
            return;
        }
        setOperations.add(key, remoteAddr);
    }


    private void updateViewCntToDB() {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Set<String> redisKeys = redisTemplate.keys("resumeId*");
        for (String keyName : redisKeys) {
            Long resumeId = extractResumeId(keyName);
            Long viewCount = setOperations.size(keyName);
            resumeRepository.addViewCountFromRedis(resumeId, viewCount);
            setOperations.pop(keyName);
        }
    }

    @NotNull
    private static Long extractResumeId(String keyName) {
        String resumeIdStr = keyName.replaceAll("\\D", "");
        return Long.parseLong(resumeIdStr);
    }

    private void isDeleted(Resume resume) {
        if (resume.isDeleted()) {
            throw new ResumeAlreadyDeletedException();
        }
    }

    private void isWriter(Member requestMember, Resume resume) {
        if (!requestMember.getId().equals(resume.getWriter().getId())) {
            throw new NoResumeWriterException(
                    String.format("작성자가 아닙니다. request user id = %d, resume writer id = %d",
                            requestMember.getId(), resume.getWriter().getId()));
        }
    }

    private Resume findResumeById(Long resumeId) {
        return resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
    }

    private ResumeResponseDto buildResumeResponse(Resume resume) {
        return ResumeResponseDto.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .content(resume.getContent())
                .writer(resume.getWriter().getNickname())
                .likeCount(resume.getLikeCount())
                .viewCount(resume.getViewCount())
                .createdAt(resume.getCreatedAt())
                .lastModifiedAt(resume.getUpdatedAt())
                .deletedAt(resume.getDeletedAt())
                .build();
    }
}
