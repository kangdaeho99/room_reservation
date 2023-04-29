package com.room.reservation.dto;


import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private Long rno;

    private String title;

    private String content;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

}