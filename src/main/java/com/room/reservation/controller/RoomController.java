package com.room.reservation.controller;


import com.room.reservation.dto.PageRequestDTO;
import com.room.reservation.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/room")
@Log4j2
@RequiredArgsConstructor
public class RoomController {

    private final RoomService service; //final 로 선언

//    @GetMapping({"","/","/list"})
//    @GetMapping({"","/"})
//    public String index(){
//        log.info("list.............");
////        return "room/list";
//        return "redirect:/room/list";
//    }

    @GetMapping({"","/","/list"})
    public void list(PageRequestDTO pageRequestDTO, Model model){
        log.info("list..............." + pageRequestDTO);
        model.addAttribute("result", service.getList(pageRequestDTO));
        System.out.println("result:::::::::::::::::"+ model.getAttribute("result"));
    }


}
