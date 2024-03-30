package springboot.yongjunstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.room.Images;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.repository.ImagesRepository;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.repository.RoomPostRepository;
import springboot.yongjunstore.request.RoomPostRequest;
import springboot.yongjunstore.response.ImagesResponse;
import springboot.yongjunstore.response.RoomPostResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomPostService {

    private final RoomPostRepository roomPostRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final ImagesRepository imagesRepository;

    @Transactional
    public void createRoom(RoomPostRequest roomDto, List<MultipartFile> uploadImages){

        Member findMember = memberRepository.findById(roomDto.getMemberId())
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        RoomPost roomPost = RoomPost.builder()
                .roomOwner(roomDto.getRoomOwner())
                .content(roomDto.getContent())
                .description(roomDto.getDescription())
                .detail(roomDto.getDetail())
                .name(roomDto.getName())
                .monthlyPrice(roomDto.getMonthlyPrice())
                .deposit(roomDto.getDeposit())
                .depositPrice(roomDto.getDepositPrice())
                .squareFootage(roomDto.getSquareFootage())
                .address(roomDto.getAddress())
                .roomStatus(roomDto.getRoomStatus())
                .member(findMember)
                .build();

        RoomPost saveRoom = roomPostRepository.save(roomPost);

        RoomPost findRoomPost = roomPostRepository.findById(saveRoom.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.ROOM_POST_NOT_FOUND));

        fileService.mainPhotoUpload(uploadImages, findRoomPost);
    }


    @Transactional
    public RoomPostResponse getRoomPost(Long roomPostId){

        RoomPost findRoomPost = roomPostRepository.SelectRoomPostPosts(roomPostId);

        List<Images> imagesList = imagesRepository.findByRoomPostId(roomPostId);

        List<ImagesResponse> imagesResponse = imagesList.stream()
                .map((i) -> new ImagesResponse(i))
                .collect(Collectors.toList());

        return new RoomPostResponse(findRoomPost, imagesResponse);
    }

}
