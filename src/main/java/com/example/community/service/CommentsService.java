package com.example.community.service;

import com.example.community.common.exception.custom.ResourceNotFoundException;
import com.example.community.common.exception.custom.UnauthenticatedException;
import com.example.community.dto.request.comments.CommentRequest;
import com.example.community.dto.response.comments.CommentResponse;
import com.example.community.dto.response.posts.PostListResponse;
import com.example.community.entity.Comments;
import com.example.community.entity.Posts;
import com.example.community.entity.Users;
import com.example.community.repository.CommentsRepository;
import com.example.community.repository.PostsRepository;
import com.example.community.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentsService {

    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;

    // 댓글 목록 조회 (게시글별, 페이징)
    public Page<CommentResponse> getAllComments(Pageable pageable, Long postId) {
        Page<Comments> comments = commentsRepository.findAllByPostIdWithUsers(postId, pageable);
        List<CommentResponse> list = comments.getContent().stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, comments.getTotalElements());
    }

    // 댓글 등록
    public CommentResponse write(Long postId, Users users, CommentRequest commentRequest) {

        // 1) 게시글 정보 조회
        Posts posts = postsRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Posts")
        );

        // 2) 댓글 작성자 정보 검색
        Users commentWriter = usersRepository.findById(users.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Users")
        );

        // Entity 변환, 연관관계 매핑
        Comments comments = CommentRequest.ofEntity(commentRequest);
        comments.assignToPost(posts); // comments.setPosts(posts);
        comments.writtenBy(commentWriter); // comments.setUsers(commentWriter);

        Comments saveComment = commentsRepository.save(comments);
        return CommentResponse.fromEntity(saveComment);
    }

    // 댓글 수정
    public CommentResponse update(Long commentId, CommentRequest commentRequest, Users user) {
        Comments comments = commentsRepository.findByIdWithUsers(commentId).orElseThrow(
                () -> new ResourceNotFoundException("존재하지 않는 Comments")
        );

        if (!comments.getUsers().getId().equals(user.getId())) {
            throw new UnauthenticatedException("댓글 수정 권한이 없습니다.");
        }

        comments.update(commentRequest.getContent());
        return CommentResponse.fromEntity(comments);
    }

    // 마이페이지 - 사용자별 댓글 조회
    public Page<CommentResponse> getMyComments(Pageable pageable, Users users) {
        Page<Comments> findComment = commentsRepository.findAllByUsers(pageable, users);
        List<CommentResponse> list = findComment.getContent().stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageable, findComment.getTotalElements());
    }

    public Page<PostListResponse> getMyPosts(Pageable pageable, Users users) {
        Page<Posts> findPost = postsRepository.findAllByUsers(pageable, users);
        List<PostListResponse> list = findPost.getContent().stream()
                .map(PostListResponse::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageable, findPost.getTotalElements());
    }

    // 댓글 삭제 (Soft Delete -> @SQLDelete 작동)
    public void delete(Long commentId) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comments"));
        commentsRepository.delete(comment); // deleted_at = NOW()
    }

}