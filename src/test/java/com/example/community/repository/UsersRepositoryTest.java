package com.example.community.repository;

import com.example.community.config.JpaConfig;
import com.example.community.entity.Role;
import com.example.community.entity.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @Test
    @DisplayName("유저 저장")
    void saveUsers() {

        // given
        Users user1 = Users.builder()
                .email("test1@test.com")
                .password("1111")
                .nickname("test1")
                .role(Role.USER)
                .build();

        Users user2 = Users.builder()
                .email("test2@test.com")
                .password("2222")
                .nickname("test2")
                .role(Role.USER)
                .build();

        Users user3 = Users.builder()
                .email("test3@test.com")
                .password("3333")
                .nickname("test3")
                .role(Role.USER)
                .build();

        // when
        usersRepository.save(user1);
        usersRepository.save(user2);
        usersRepository.save(user3);

        // then

    }


    @Test
    @DisplayName("이메일로 유저 조회 - 존재하는 이메일")
    void findByEmail_success() {

        // given
        Users user = Users.builder()
                .email("test@test.com")
                .password("1234")
                .nickname("test")
                .role(Role.USER)
                .build();
        usersRepository.save(user);

        // when
        Optional<Users> findUser = usersRepository.findByEmail("test@test.com");

        // then
        assertThat(findUser).isPresent();
        assertThat(findUser.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("이메일로 유저 조회 - 존재하지 않는 이메일")
    void findByEmail_fail() {

        // given

        // when
        Optional<Users> findUser = usersRepository.findByEmail("none@none.com");

        // then
        assertThat(findUser).isNotPresent();
    }

    @Test
    @DisplayName("닉네임으로 유저 조회 - 존재하는 닉네임")
    void findByNickname_success() {

        // given
        Users user = Users.builder()
                .email("test@test.com")
                .password("1234")
                .nickname("test")
                .role(Role.USER)
                .build();
        usersRepository.save(user);

        // when
        Optional<Users> findUser = usersRepository.findByNickname("test");

        // then
        assertThat(findUser).isPresent();
        assertThat(findUser.get().getNickname()).isEqualTo("test");
    }

    @Test
    @DisplayName("닉네임으로 유저 조회 - 존재하는 닉네임")
    void findByNickname_fail() {

        // given

        // when
        Optional<Users> findUser = usersRepository.findByNickname("none");

        // then
        assertThat(findUser).isNotPresent();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재")
    void existsByEmail_exists() {

        // given
        Users user = Users.builder()
                .email("test@test.com")
                .password("1234")
                .nickname("test")
                .role(Role.USER)
                .build();
        usersRepository.save(user);

        // when
        boolean exists = usersRepository.existsByEmail("test@test.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하지 않음")
    void existsByEmail_notExists() {

        // given

        // when
        boolean notExists = usersRepository.existsByEmail("none@test.com");

        // then
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("닉네임 존재 여부 확인 - 존재")
    void existsByNickname_exists() {

        // given
        Users user = Users.builder()
                .email("test@test.com")
                .password("1234")
                .nickname("test")
                .role(Role.USER)
                .build();
        usersRepository.save(user);

        // when
        boolean exists = usersRepository.existsByNickname("test");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임 존재 여부 확인 - 존재하지 않음")
    void existsByNickname_notExists() {

        // given

        // when
        boolean notExists = usersRepository.existsByNickname("none");

        // then
        assertThat(notExists).isFalse();
    }
}