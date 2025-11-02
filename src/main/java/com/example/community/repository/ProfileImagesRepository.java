package com.example.community.repository;

import com.example.community.entity.ProfileImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImagesRepository extends JpaRepository<ProfileImages, Long> {
    Optional<ProfileImages> findByUsersId(Long usersId);
}
