### 파일 설명
로그인한 “사용자 1명 정보” - CustomUserDetails
사용자 정보 조회 - CustomUserDetailsService (로그인 시 이메일로 DB에서 사용자 찾는 애)
비밀번호 암호화/검증 - PwdEncoderConfig (회원가입 시 암호 저장, 로그인 시 비교)
토큰 발급/검증 - JwtTokenUtil
인증 실패 처리 - JwtAuthenticationEntryPoint
요청 필터링 (토큰 검증) - JwtAuthenticationFilter
전체 보안 설정 - SecurityConfig

### JWT 로직
// 회원가입은 이미 했음 - 비밀번호를 PwdEncoderConfig로 암호화해서 DB 저장 (실제 비밀번호는 저장 안함)
1. 클라이언트에서 로그인 요청 (이메일, 비밀번호)
2. 서버에서 검증 
   1. CustomUserDetailsService가 DB에서 사용자 조회
   2. 비밀번호 비교 (PasswordEncoder.matches())
   -> 쉽게 말하자면 (이메일 - 비번) 맞는지 확인
3. (사용자 확인 되면) 토큰 발급 -> Access Token은 로그인할 때마다 새로 발급
   1. JwtTokenUtil이 Access Token 생성 (email + role + 만료시간 포함)
4. 클라이언트 저장 (브라우저에 저장)
5. 이후 요청마다 헤더에 Authorization: Bearer <token> 추가 (헤더에 토큰 실어서 요청 보냄)
6. 서버 요청 시 필터 통과
   1. JwtAuthenticationFilter가 요청 헤더에서 토큰 꺼내서 검증
   2. JwtTokenUtil로 토큰이 유효한지 검사
   3. 유효하면 SecurityContext에 로그인 정보 저장 (자동으로 관리됨)
7. Controller에서 인증 유저 사용 - @AuthenticationPrincipal (디비 조회 안해도 된다는 뜻!)
8. 만료 시 다시 로그인 아니면 refresh token 발급
9. 난 일단 엑세스 토큰.....

### 요약
로그인 할때 JWT를 만들고 이후 요청은 서버가 DB 보지 않고 JWT를 확인해서 인증 처리.
