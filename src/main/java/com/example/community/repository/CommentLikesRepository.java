package com.example.community.repository;

import com.example.community.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {

    // 1. 유저가 해당 댓글에 이미 좋아요를 눌렀는지 확인
    Optional<CommentLikes> findByUsersAndComments(Users users, Comments comments);
    // boolean existsByUsersAndComments(Users users, Comments comments);

    // 2. 특정 댓글의 총 좋아요 수
    Long countByComments(Comments comments);

    // 3. 댓글 삭제 시 좋아요 정리
    void deleteByComments(Comments comments);

    // 4. 회원 탈퇴 시 유저가 누른 좋아요도 삭제
    void deleteByUsers(Users users);

}
