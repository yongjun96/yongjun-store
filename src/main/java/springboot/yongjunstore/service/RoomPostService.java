package springboot.yongjunstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.yongjunstore.common.exception.GlobalException;
import springboot.yongjunstore.common.exceptioncode.ErrorCode;
import springboot.yongjunstore.domain.Member;
import springboot.yongjunstore.domain.room.RoomPost;
import springboot.yongjunstore.repository.MemberRepository;
import springboot.yongjunstore.repository.RoomPostRepository;
import springboot.yongjunstore.request.RoomPostDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomPostService {

    private final RoomPostRepository roomPostRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public RoomPostDto createRoom(RoomPostDto roomDto){

        Member findMember = memberRepository.findByEmail(roomDto.getMember().getEmail())
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        RoomPost roomPost = RoomPost.builder()
                .roomOwner(roomDto.getRoomOwner())
                .member(findMember)
                .content(roomDto.getContent())
                .description(roomDto.getDescription())
                .detail(roomDto.getDetail())
                .images(roomDto.getImages())
                .mainPhoto(roomDto.getMainPhoto())
                .name(roomDto.getName())
                .monthlyPrice(roomDto.getMonthlyPrice())
                .deposit(roomDto.getDeposit())
                .depositPrice(roomDto.getDepositPrice())
                .squareFootage(roomDto.getSquareFootage())
                .address(roomDto.getAddress())
                .roomStatus(roomDto.getRoomStatus())
                .build();

        RoomPost saveRoom = roomPostRepository.save(roomPost);

        RoomPostDto returnRoomPostDto = new RoomPostDto(saveRoom);

        return returnRoomPostDto;
    }

}
