package com.room.reservation.repository.room;

import com.room.reservation.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomQueryDslRepository {


    Page<Object[]> getListPageWithReview(Pageable pageable);

    Page<Object[]> getListPageWithReply(Pageable pageable);

    Room search1();

    Page<Object[]> searchPage(String type, String keyword, Pageable pageable);



}
