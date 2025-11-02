package com.example.community.repository;

import com.example.community.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    // 이메일로 사용자 조회
    Optional<Users> findByEmail(String email);

    // 닉네임으로 사용자 조회
    Optional<Users> findByNickname(String nickname);

}
