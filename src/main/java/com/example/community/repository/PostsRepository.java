package com.example.community.repository;

import com.example.community.entity.Posts;
import com.example.community.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {

    // 1. 게시글 조회
    // 1-1. 게시글 전체 조회
    @Query(value = "SELECT p FROM Posts p JOIN FETCH p.users")
    Page<Posts> findAllWithUsers(Pageable pageable);

    // 1-2. 게시글 상세 조회
    @Query("select p from Posts p join fetch p.users where p.id = :postId")
    Optional<Posts> findByIdWithUsers(@Param("postId") Long postId);

    // 1-3. 사용자별 게시글 조회
    Page<Posts> findAllByUsers(Pageable pageable, Users users);

    // 2. 게시글 검색
    // 2-1. 게시글 제목 검색
    @Query(value = "SELECT p FROM Posts p JOIN FETCH p.users WHERE p.title LIKE %:title%")
    Page<Posts> findAllTitleContaining(String title, Pageable pageable);

    // 2-2. 게시글 내용 검색
    @Query(value = "SELECT p FROM Posts p JOIN FETCH p.users WHERE p.content LIKE %:content%")
    Page<Posts> findAllContentContaining(String content, Pageable pageable);

    // 2-3. 게시글 작성자 검색
    // 닉네임으로 하는게 맞을까...!!
    @Query(value = "SELECT p FROM Posts p JOIN FETCH p.users WHERE p.users.nickname LIKE %:nickname%")
    Page<Posts> findAllNicknameContaining(String nickname, Pageable pageable);

    // 3. 조회수 증가
    @Modifying(clearAutomatically = true) // 실행하고 나서 영속성 컨텍스트 초기화
    @Query("UPDATE Posts p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);
}