### CustomUserDetailsService -> CustomUserDetails

1. 로그인 요청
2. 우리는 이메일로 로그인하니까
--- CustomUserDetailsService ---
3. CustomUserDetailsService에서 usersRepository.findByEmail(email) - 이메일로 (디비에서) 유저 찾기
4. 찾은 유저를 감싸서 new CustomUserDetails(Users) 이렇게 리턴
--- CustomUserDetails ---
5. 비밀번호 비교
   // CustomUserDetails
   @Override
   public String getPassword() { // 디비에 있는거 가져옴(암호화 되어있음)
   return users.getPassword();
   }
이거랑 우리가 (1번에서) 입력한 비밀번호(를 PasswordEncoder로 암호화해서)랑 비교한다는 뜻
6. 인증 성공 → SecurityContext에 저장

### 요약
이메일로 로그인
디비에서 로그인한거랑 똑같은 이메일쓰는 유저 정보 가져오기 (CustomUserDetails로 감싸서)
비밀번호를 비교
비밀번호 맞네 -> 인증 성공!
-> 이 이후가 JWT 발급이 이어짐

이후 요청부터는 (게시글 쓰기, 댓글 쓰기 같은 API 요청)
DB 다시 안보고 요청 헤더에 있는 JWT 토큰만 보고 로그인된 사용자인거 인식!