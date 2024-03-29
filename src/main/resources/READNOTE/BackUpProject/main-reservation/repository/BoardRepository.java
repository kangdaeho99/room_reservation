package com.room.reservation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Description
 *
 * JPQL과 left ( outer ) join
 * 목록화면에서 게시글의 정보와 함께 댓글의 수를 가져오기 위해서 단순히 하나의 엔티티타입만 사용불가
 * JPQL의 조인(join)을 사용합니다.
 *
 * Board Entity는 Member Entity를 변수로 선언하고 연관관계가 있음.
 * 이러한 경우에는 Board의 Writer 변수를 이용하여 조인처리.
 *
 *
 */
public interface BoardRepository extends JpaRepository<Board, Long>, SearchBoardRepository {

    //한개의 로우(Object) 내에 Object [] 로 나옴

    /**
     * 엔티티 클래스 내부에 연관관계가 있는경우
     *
     * Board를 사용하고 있지만 Member를 같이 조회해야합니다.
     * Board Entity는 Member Entity와 연관관계
     * b.writer와 같은형태로 사용
     * 내부에 있는 엔티티를 이용할때는 'LEFT JOIN' 뒤에 'ON'을 이용하는 부분 없음.
     *
     */
    @Query("select b, w from Board b left join b.writer w where b.bno = :bno")
    Object getBoardWithWriter(@Param("bno") Long bno);

    /**
     * 연관관계가 없는 엔티티 조인처리에는 on
     *
     * Board와 Member 사이에는 내부적으로 참조를 통해서 연관관계가 있지만,
     * Board와 Reply는 상황이 다릅니다.
     * Reply쪽이 @ManyToOne으로 참조하고 있으나
     * Board 입장에서는 Reply 객체들을 참조하고 있지 않기때문에 문제가 됩니다.
     * 이런 경우에는 직접 조인에 필요한 조건은 'on'을 이용해서 작성해주어야 합니다.
     *
     * 특정게시물과 해당 게시물에 속한 댓글들을 조회 해야하는 상황.
     * board와 reply 테이블을 조인해서 쿼리 처리.
     * 순수 쿼리일시에는
     * 'select board.bno, board.title, board.writer_email, rno, text
     * from board left outer join reply
     * on reply.board_bno = board.bno
     * where board.bno = 100; '
     *
     * 위의 쿼리를 JPQL로 처리합니다.
     *
     */
    @Query("SELECT b, r FROM Board b LEFT JOIN Reply r ON r.board = b where b.bno = :bno")
    List<Object[]> getBoardWithReply(@Param("bno") Long bno);

    /**
     *
     * 게시물(Board) : 게시물의 번호, 제목, 게시물의 작성시간
     * 회원(Member) : 회원의 이름/이메일
     * 댓글(Reply) : 해당 게시물의 댓글수
     *
     * 위 3개의 엔티티 중에서 가장 많은 데이터를 가져오는 쪽은 Board이므로 Board를 중심으로 조인관계를 작성
     * Member는 Board 내에 Writer라는 필드로 연관관계를 맺고있고,
     * Reply는 연관관계가 없는 상황입니다.
     * 조인 후에는 Board를 기준으로 'GROUP BY' 처리를 해서 하나의 게시물 당 하나의 라인이 될 수 있도록 처리합니다.
     * (JPQL을 이용하면 SQL에 사용하는 많은 함수를 적용할 수 있습니다.)
     * Pageable을 파라미터, Page<Object[]> 리턴타입의 getBoardWithReplyCount를 만듭니다.
     *
     * @param pageable
     * @return
     */
    @Query(value = "SELECT b, w, count(r) FROM Board b LEFT JOIN b.writer w LEFT JOIN Reply r ON r.board = b GROUP By b",
    countQuery = "SELECT count(b) FROM Board b")
    Page<Object[]> getBoardwithReplyCount(Pageable pageable);


    @Query("SELECT b, w, count(r) FROM Board b LEFT JOIN b.writer w LEFT OUTER JOIN Reply r ON r.board = b where b.bno = :bno")
    Object getBoardByBno(@Param("bno") Long bno);
}
