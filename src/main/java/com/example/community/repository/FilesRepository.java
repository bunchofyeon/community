package com.example.community.repository;

import com.example.community.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilesRepository extends JpaRepository<Files, Long> {

    List<Files> findByPostsId(Long postsId);

}
