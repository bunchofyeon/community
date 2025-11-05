package com.example.community.service;

import com.example.community.repository.FilesRepository;
import com.example.community.repository.PostsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FilesService {

    private final PostsRepository postsRepository;
    private final FilesRepository filesRepository;

    // 1. 업로드


    // 2. 다운로드


    // 3. 삭제



}
