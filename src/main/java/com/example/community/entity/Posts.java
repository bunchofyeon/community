package com.example.community.entity;

import com.example.community.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@SQLDelete(sql = "UPDATE posts SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Posts extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 26)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false, name = "like_count")
    private Long likeCount = 0L;

    @Column(nullable = false, name = "comment_count")
    private Long commentCount = 0L;

    @Column(nullable = false, name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 작성자 (필수니까 optional = false, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    // 게시글 삭제하면 댓글도 삭제 (orphanRemoval = true)
    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> comments = new ArrayList<>();

    // 게시글 삭제하면 파일도 삭제
    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Files> files = new ArrayList<>();

    public void addFile(Files file) {
        files.add(file);
        file.setMappingPosts(this); // 양방향 연결 유지
    }

    public void removeFile(Files file) {
        files.remove(file);
        file.setMappingPosts(null); // 끊어줌
    }

    @Builder
    private Posts(String title, String content, Users users) {
        this.title = title;
        this.content = content;
        this.users = users;
    }

    // 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 수정사항 Dirty Checking
    // dto.title 이런식으로 할지 고민
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 사용자 주입 (작성자를 알게 하기 위해)
    // public void setUsers(Users users)
    public void setMappingUsers(Users users) {
        this.users = users;
    }
}
