package com.example.community.repository;

import com.example.community.entity.PostLikes;
import com.example.community.entity.Posts;
import com.example.community.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikesRepository  extends JpaRepository<PostLikes, Long> {

    // 1. 유저가 해당 게시글에 이미 좋아요를 눌렀는지 확인
    // boolean existsByUsersAndPosts(Users users, Posts posts); -> 이렇게 하는게 더 좋을까...??
    Optional<PostLikes> findByUsersAndPosts(Users users, Posts posts);

    // 2. 특정 게시글의 총 좋아요 수
    Long countByPosts(Posts posts);

    // 3. 게시글 삭제 시 좋아요 정리
    void deleteByPosts(Posts posts);

    // 4. 회원 탈퇴 시 유저가 누른 좋아요도 삭제
    void deleteByUsers(Users users);

    // 5. 좋아요 누른 유저 목록 -> 꼭 해야될까 .. ???

}
