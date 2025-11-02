package com.example.community.service;

import com.example.community.common.exception.custom.ResourceNotFoundException;
import com.example.community.common.exception.custom.UnauthenticatedException;
import com.example.community.dto.request.posts.PostUpdateRequest;
import com.example.community.dto.request.posts.PostWriteRequest;
import com.example.community.dto.response.posts.PostDetailsResponse;
import com.example.community.dto.response.posts.PostListResponse;
import com.example.community.dto.response.posts.PostWriteResponse;
import com.example.community.entity.Posts;
import com.example.community.entity.Users;
import com.example.community.repository.PostsRepository;
import com.example.community.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PostsService {

    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;

    // 게시글 목록 조회
    public Page<PostListResponse> getAllPosts(Pageable pageable) {
        Page<Posts> posts = postsRepository.findAllWithUsers(pageable);
        List<PostListResponse> list = posts.getContent().stream()
                .map(PostListResponse::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, posts.getTotalElements());
    }

    // 게시글 검색
    // ...


    // 게시글 등록
    public PostWriteResponse write(PostWriteRequest postWriteRequest, Users users) {

        // 1) 작성자 조회
        Users writer = usersRepository.findByEmail(users.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("Users"));

        // 2) DTO -> Entity
        Posts posts = PostWriteRequest.ofEntity(postWriteRequest);

        // 3) 작성자 매핑
        posts.setMappingUsers(writer);

        // 4) 저장
        Posts savedPosts = postsRepository.save(posts);

        // 5) 응답 변환
        return PostWriteResponse.fromEntity(savedPosts);
    }

    // 게시글 상세 조회
    public PostDetailsResponse detail(Long postId) {
        // 조회수 증가
        postsRepository.incrementViewCount(postId);

        // 게시글 조회 (최신 상태로...)
        Posts findPost = postsRepository.findByIdWithUsers(postId).orElseThrow(
                () -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
        return PostDetailsResponse.fromEntity(findPost);
    }

    // 마이페이지 - 사용자별 게시글 목록(조회 말고 목록!)
    public Page<PostListResponse> getMyPosts(Pageable pageable, Users users) {
        Page<Posts> findPost = postsRepository.findAllByUsers(pageable, users);
        List<PostListResponse> list = findPost.getContent().stream()
                .map(PostListResponse::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageable, findPost.getTotalElements());
    }

    // 게시글 수정
    public PostDetailsResponse update(Long postId, PostUpdateRequest postUpdateRequest, Users users) {
        Posts updatePost = postsRepository.findByIdWithUsers(postId).orElseThrow( // 일단... (게시글 + 작성자)만 조회
                () -> new ResourceNotFoundException("존재하지 않는 Posts"));

        if (!updatePost.getUsers().getId().equals(users.getId())) {
            throw new UnauthenticatedException("게시글 수정 권한이 없습니다.");
        }

        updatePost.update(postUpdateRequest.getTitle(), postUpdateRequest.getContent());
        postsRepository.saveAndFlush(updatePost ); // updated_at 채워지게
        return PostDetailsResponse.fromEntity(updatePost);
    }

    // 게시글 삭제
    public void delete(Long postId) {
        postsRepository.deleteById(postId);
    }

}