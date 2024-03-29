//package com.room.reservation.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Service;
//
//import java.util.stream.IntStream;
//
///**
// * Description :
// *
// * dtoToEntity(BoardDTO) : DTO가 연관관계를 가진 Board엔티티객체와
// * Member엔티티객체를 구성해야하므로 내부적으로 Member엔티티를 처리하는 과정을 거칩니다.
// *
// *
// */
//@Service
//@Log4j2
//@RequiredArgsConstructor
//public class MemberServiceImpl implements MemberService {
////    private final MemberRepository repository;
//
//    @Override
//    public void initMemberDataBase() {
//        IntStream.rangeClosed(1, 12).forEach( i -> {
//            Member member = Member.builder()
//                    .email("user"+i+"@aaa.com")
//                    .password("1111")
//                    .name("user"+i)
//                    .build();
//            repository.save(member);
//        });
//    }
//}
