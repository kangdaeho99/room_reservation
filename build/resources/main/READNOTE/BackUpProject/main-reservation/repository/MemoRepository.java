package com.room.reservation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    /* Query Methods, 이름 자체가 질의조건을 만듭니다. mno를 기준으로 하여 between구문을 사용, orderby가 wjrdydehlqslek.*/
    List<Memo> findByMnoBetweenOrderByMnoDesc(Long from, Long to);

    Page<Memo> findByMnoBetween(Long from, Long to, Pageable pageable);

    void deleteMemoByMnoLessThan(Long num);
}
/*
MemoRepository는 특이하게도 인터페이스 자체.
JpaRepository 인터페이스를 상속하는 것만으로 모든 작업이 끝남.
JpaRepository를 사용할때는 엔티티의 타입 정보(Memo 클래스 타입)와 @Id의 타입을 지정하게 됨.
Spring Data JPA는 인터페이스 선언만으로 자동으로 스프링 Bean으로 등록됨.
(스프링이 내부적으로 인터페이스 타입에 맞는 객체 생성하여 빈으로 등록함)

 */