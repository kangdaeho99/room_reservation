package com.room.reservation.dto;


import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**********
 * Author 강대호
 * 제너릭 타입으로 선언
 * @param <DTO> DTO 를 의미
 * @param <EN>  Entity 를 의미
 * Descritipn :JPA를 이용하는 Repository에서는 페이지 처리결과를 Page<Entity> 타입으로 반환하게됨
 * 서비스계층에서 이를 처리하기 위해서 별도의 클래스를 만들어서 처리해야함
 * -Page<Entity>의 엔티티 객체들을 DTO 객체로 변환해서 자료구조로 담아주어야합니다.
 * -화면 출력에 필요한 페이지 정보들을 구성해주어야합니다.
 *
 * PageResultDTO는 Page<Entity> 타입을 이용해서 생성할 수 있도록 생성자로 작성합니다.
 * 이때 특별한 Function<EN,DTO>는 엔티티 객체들을 DTO로 변환해주는 기능(엔티티 객체를 Function을 통해 DTO로 변환시킴)
 * 어떤 종류의 Page<E> 타입이 생성되더라도 PageResultDTO를 이용해서 처리할 수 있다는 장점이 있습니다.
 *
 */
@Data
public class PageResultDTO<DTO, EN>{

    //DTO리스트
    private List<DTO> dtoList;

    //총 페이지 번호
    private int totalPage;

    //현재 페이지번호
    private int page;

    //목록 사이즈
    private int size;

    //시작 페이지 번호, 끝 페이지 번호
    private int start, end;

    //이전, 다음
    private boolean prev, next;

    //페이지 번호 목록
    private List<Integer> pageList;

    public PageResultDTO(Page<EN> result, Function<EN, DTO> fn){
        dtoList = result.stream().map(fn).collect(Collectors.toList());
        totalPage = result.getTotalPages();
        makePageList(result.getPageable());
    }

    private void makePageList(Pageable pageable){
        this.page = pageable.getPageNumber() + 1; //0부터 시작하므로 1을 추가
        this.size = pageable.getPageSize();

        //temp end Page
        int tempEnd = (int)(Math.ceil(page/10.0)) * 10;

        start = tempEnd - 9;

        prev = start > 1;

        end = totalPage > tempEnd ? tempEnd : totalPage;

        next = totalPage > tempEnd;

        pageList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
    }
}
