package springboot.yongjunstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import springboot.yongjunstore.config.jwt.JwtDto;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.Role;
import springboot.yongjunstore.domain.room.Deposit;
import springboot.yongjunstore.domain.room.Images;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.domain.room.RoomStatus;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.repository.RoomPostRepository;
import springboot.yongjunstore.request.RoomPostRequest;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"fileUpload.upload.local.path=/uploads"})
class RoomPostControllerTest {

    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    private String email = "test@test.com";
    private Role role = Role.MEMBER;

    @Autowired private MemberRepository memberRepository;
    @Autowired private RoomPostRepository roomPostRepository;
    @Autowired private MockMvc mockMvc;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        memberRepository.deleteAll();
        roomPostRepository.deleteAll();
    }

    @Test
    @DisplayName("글 생성 성공")
    void roomPostCreate() throws Exception {

        // given
        Member member = createMember(email, role);

        JwtDto jwt = jwtDto();

        RoomPostRequest roomPostRequest = RoomPostRequest.builder()
                .title("제목")
                .roomOwner("방주인")
                .depositPrice("100")
                .roomStatus(RoomStatus.임대)
                .deposit(Deposit.전세)
                .content("내용입니다. 10글자 이상입니다...")
                .memberId(member.getId())
                .monthlyPrice("10000")
                .squareFootage("4")
                .address("주소")
                .detailAddress("상세 주소")
                .build();

        MockMultipartFile file1 = new MockMultipartFile(
                "uploadImages", "filename.jpg", "image/jpeg", "imageData".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "uploadImages", "filename2.jpg", "image/jpeg", "imageData".getBytes());

        List<MultipartFile> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);


        //expected
        mockMvc.perform(MockMvcRequestBuilders.multipart("/room-post/create")
                        .file(file1)
                        .file(file2)
                        .part(new MockPart("roomPostRequest", objectMapper.writeValueAsBytes(roomPostRequest))) // roomPostRequest를 멀티파트 요청으로 추가
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("글 생성 실패 : valid를 어긴 경우.")
    void roomPostCreateTitleBadRequest() throws Exception {

        // given
        createMember(email, role);

        JwtDto jwt = jwtDto();

        RoomPostRequest roomPostRequest = RoomPostRequest.builder()
                .title("제목")
                .roomOwner("방주인")
                .roomStatus(RoomStatus.임대)
                .deposit(Deposit.전세)
                .monthlyPrice("10000")
                .squareFootage("4")
                .address("주소")
                .content("")
                .depositPrice("")
                .memberId(null)
                .detailAddress("상세 주소")
                .build();

        MockMultipartFile file1 = new MockMultipartFile(
                "uploadImages", "filename.jpg", "image/jpeg", "imageData".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "uploadImages", "filename2.jpg", "image/jpeg", "imageData".getBytes());

        List<MultipartFile> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);


        //expected
        mockMvc.perform(MockMvcRequestBuilders.multipart("/room-post/create")
                        .file(file1)
                        .file(file2)
                        .part(new MockPart("roomPostRequest", objectMapper.writeValueAsBytes(roomPostRequest))) // roomPostRequest를 멀티파트 요청으로 추가
                        .header(HttpHeaders.AUTHORIZATION, jwtDto().getGrantType()+" "+jwt.getAccessToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("내용을 입력해 주세요.")) // 길이 정규식 테스트
                .andExpect(jsonPath("$.depositPrice").value("1만원 단위이며 억 단위를 넘을 수 없습니다.")) // 숫자 정규식 테스트
                .andExpect(jsonPath("$.memberId").value("/member/find/{email}을 호출하지 못해 id를 받아 오지못했습니다.")) // memberId가 없는 경우
                .andDo(print());
    }


    @Test
    @DisplayName("글 하나 조회 성공")
    void getRoomPost() throws Exception {

        // given
        Member member = createMember(email, role);

        RoomPost roomPost = RoomPost.builder()
                .title("제목")
                .roomOwner("방주인")
                .depositPrice("100")
                .roomStatus(RoomStatus.임대)
                .deposit(Deposit.전세)
                .content("내용입니다. 10글자 이상입니다...")
                .member(member)
                .monthlyPrice("10000")
                .squareFootage("4")
                .address("주소")
                .address("상세 주소")
                .build();

        RoomPost saveRoomPost = roomPostRepository.save(roomPost);

        Images images = Images.builder()
                .roomPost(saveRoomPost)
                .path("테스트 경로")
                .name("테스트 이름")
                .build();

        List<Images> imagesList = new ArrayList<>();
        imagesList.add(images);

        saveRoomPost.addImagesList(imagesList);

        RoomPost lastSaveRoomPost = roomPostRepository.save(roomPost);


        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/room-post/posts/{roomPostId}", lastSaveRoomPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saveRoomPost.getId()))
                .andExpect(jsonPath("$.member.id").value(member.getId()))
                .andExpect(jsonPath("$.member.email").value(member.getEmail()))
                .andExpect(jsonPath("$.title").value(saveRoomPost.getTitle()))
                .andDo(print());
    }


    @Test
    @DisplayName("글 하나 조회 실패 : 이미지가 없는 경우")
    void getRoomPostImageNotFound() throws Exception {

        // given
        Member member = createMember(email, role);

        RoomPost roomPost = RoomPost.builder()
                .title("제목")
                .roomOwner("방주인")
                .depositPrice("100")
                .roomStatus(RoomStatus.임대)
                .deposit(Deposit.전세)
                .content("내용입니다. 10글자 이상입니다...")
                .member(member)
                .monthlyPrice("10000")
                .squareFootage("4")
                .address("주소")
                .detailAddress("상세 주소")
                .build();

        RoomPost saveRoomPost = roomPostRepository.save(roomPost);


        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/room-post/posts/{roomPostId}", saveRoomPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    @DisplayName("글 하나 조회 실패 : 글이 없는 경우")
    void getRoomPostNotFound() throws Exception {

        // given
        Long roomPostId = 1L;

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/room-post/posts/{roomPostId}", roomPostId)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    @DisplayName("글 리스트 조회 성공")
    void searchRoomPostList() throws Exception {

        // given
        String searchOption = "title"; // 검색 옵션
        String searchContent = ""; // 검색 내용

        Member member = createMember(email, role);

        RoomPost roomPost = null;
        
        for (int i= 0; i<5; i++) {

            roomPost = RoomPost.builder()
                    .title("제목")
                    .roomOwner("방주인")
                    .depositPrice("100")
                    .roomStatus(RoomStatus.임대)
                    .deposit(Deposit.전세)
                    .content("내용입니다. 10글자 이상입니다...")
                    .member(member)
                    .monthlyPrice("10000")
                    .squareFootage("4")
                    .address("주소")
                    .detailAddress("상세 주소")
                    .build();

            RoomPost saveRoomPost = roomPostRepository.save(roomPost);

            Images images = Images.builder()
                    .roomPost(saveRoomPost)
                    .path("테스트 경로")
                    .name("테스트 이름")
                    .build();

            List<Images> imagesList = new ArrayList<>();
            imagesList.add(images);

            saveRoomPost.addImagesList(imagesList);

            roomPostRepository.save(saveRoomPost);
        }

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/room-post/posts")
                        .param("searchOption", searchOption)
                        .param("searchContent", searchContent)
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5)) // 검색 결과가 2개인지 확인
                .andExpect(jsonPath("$.content[0].title").value(roomPost.getTitle()))
                .andExpect(jsonPath("$.content[1].title").value(roomPost.getTitle()))
                .andExpect(jsonPath("$.content[2].title").value(roomPost.getTitle()))
                .andExpect(jsonPath("$.content[3].title").value(roomPost.getTitle()))
                .andExpect(jsonPath("$.content[4].title").value(roomPost.getTitle()))
                .andDo(print());
    }


    @Test
    @DisplayName("글 리스트 조회 실패 : searchOption이 없는 경우")
    void searchRoomPostListSearchOptionNotFound() throws Exception {

        // given
        String searchOption = ""; // 검색 옵션
        String searchContent = ""; // 검색 내용

        Member member = createMember(email, role);

        RoomPost roomPost = null;

        for (int i= 0; i<5; i++) {

            roomPost = RoomPost.builder()
                    .title("제목")
                    .roomOwner("방주인")
                    .depositPrice("100")
                    .roomStatus(RoomStatus.임대)
                    .deposit(Deposit.전세)
                    .content("내용입니다. 10글자 이상입니다...")
                    .member(member)
                    .monthlyPrice("10000")
                    .squareFootage("4")
                    .address("주소")
                    .detailAddress("상세 주소")
                    .build();

            RoomPost saveRoomPost = roomPostRepository.save(roomPost);

            Images images = Images.builder()
                    .roomPost(saveRoomPost)
                    .path("테스트 경로")
                    .name("테스트 이름")
                    .build();

            List<Images> imagesList = new ArrayList<>();
            imagesList.add(images);

            saveRoomPost.addImagesList(imagesList);

            roomPostRepository.save(saveRoomPost);
        }

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/room-post/posts")
                        .param("searchOption", searchOption)
                        .param("searchContent", searchContent)
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }



    private Member createMember(String email, Role role){

        Member member = Member.builder()
                .name("홍길동")
                .password(passwordEncoder.encode("qwer!1234"))
                .role(role)
                .email(email)
                .build();

        return memberRepository.save(member);
    }

    private JwtDto jwtDto(){

        long accessTime = 3600000L;       // accessToken 1시간
        long refreshTime = 2592000000L;   // refreshToken 30일


        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());

        long now = (new Date()).getTime();

        // AccessToken
        Date accessTokenExpiresIn = new Date(now + accessTime);

        String accessToken = Jwts.builder()
                .setSubject(email)
                .claim("role", "ROLE_"+role)
                .setExpiration(accessTokenExpiresIn)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}