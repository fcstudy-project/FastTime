package com.fasttime.domain.post.service;

import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.request.PostUpdateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.exception.NotPostWriterException;
import com.fasttime.domain.post.exception.PostNotFoundException;
import com.fasttime.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PostCommandService {

    private final PostRepository postRepository;

    public PostCommandService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostResponseDto writePost(PostCreateServiceDto serviceDto) {

        Post createdPost = Post.createNewPost(null, serviceDto.getTitle(), serviceDto.getContent(),
            false);

        Post savedPost = postRepository.save(createdPost);

        return PostResponseDto.of(savedPost);
    }

    public PostResponseDto updatePost(PostUpdateServiceDto serviceDto) {

        Post post = findPostById(serviceDto);

        validateMemberAuthority(serviceDto.getMemberId(), post.getMember().getId());

        post.update(serviceDto.getContent());

        return PostResponseDto.of(post);
    }

    private Post findPostById(PostUpdateServiceDto serviceDto) {
        return postRepository.findById(serviceDto.getPostId())
            .orElseThrow(PostNotFoundException::new);
    }

    private static void validateMemberAuthority(Long requesterId, Long writerId) {
        if (!requesterId.equals(writerId)) {
            throw new NotPostWriterException();
        }
    }
}
