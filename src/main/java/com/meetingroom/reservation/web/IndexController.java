package com.meetingroom.reservation.web;

import com.meetingroom.reservation.service.posts.PostsService;
import com.meetingroom.reservation.web.dto.PostsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class IndexController {
    private final PostsService postsService;
    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("posts", postsService.findAllDesc());
        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave(){
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model){
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);

        return "posts-update";
    }



}

/*
머스테치 스타터 덕분에 컨트롤러에서 문자열을 반환할때 앞의 경로와 뒤의 파일 확장자는 자동으로 지정됩니다.
앞의 경로는 src/main/resources/templates로 뒤의 파일 확장자는 .mustache가 붙는 것입니다.
즉 여기선 "index"을 반환하므로 src/main/resources/templates/index.mustache로 전환되어 View Resolver가 처리하게 됩니다.
ViewResolver는 URL 요청의 결과를 전달할 타입과 값을 지정하는 관리자 격으로 볼 수 있습니다.

자 여기까지 코드가 완성되었으니 이번에도 테스트 코드로 검증해보겠습니다.
test 패키지에 IndexControllerTest 클래스를 생성합니다.

---
Model : 서버템플릿 엔진에서 사용할 수 있는 객체를 저장할 수 있습니다.
여기서는 postsService.findAllDesc()로 가져온 결과를 posts로 index.mustache에 전달합니다.
Controller까지 모두 완성되었습니다.
http://localhost:8080/ 로 접속한뒤 등록 화면을 이용해 하나의 데이터를 등록해봅니다.
그럼 다음과 같이 목록 기능이 정상적으로 작동하는 것을 확인할 수 있습니다.

4.5 글 목록기능을 완성했으니, 게시글 수정, 삭제화면을 만들어보겠습니다.
마지막으로 게시글 수정, 삭제 화면을 만들어 보겠습니다.
[\src\main\java\com\meetingroom\reservation\web\PostApiController.java]
게시글 수정 API는 이미 3.4 절에서 만들어 두었습니다.
    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto){
        return postsService.update(id, requestDto);
    }
이미 만들어두었던 해당 API를 활용하여 해당 API로 요청하는 화면을 개발하겠습니다.

---
게시글 수정
게시글 수정화면 머스테치 파일을 생성합니다.

---
IndexController에
@GetMapping("/posts/update/{id}")
public String postsUpdate(@PathVariable Long id, Model model){
를 통하여 수정화면을 연결합니다.
등록화면과 조회화면을 개발하면서 익숙해진 코드들이라 수정코드는 쉽게 어려울 것이 없습니다.

자! 그럼 수정기능을 한번 사용해보겠습니다.
localhost:8080/ 으로 접속하여 시도해봅니다.
메인화면으로 이동하면 타이틀 항목에 링크표시가 된 것을 확인할 수 있습니다.
해당 링크를 클릭하면 수정페이지로 이동합니다. 글번호와 작성자가 읽기전용 상태인것을 확인한뒤,
제목과 내용을 수정해봅니다.
수정완료 버튼을 클릭하면 수정완료 메세지가 나타나며 제목은 '테스트'에서 '테스트2'로 변경된 것을 확인할 수 있습니다.

---
게시글 삭제
수정기능이 정상적으로 구현되었으니, 삭제기능도 구현해봅니다.
삭제버튼은 본문을 확인하고 진행해야하므로, 수정화면에 추가하겠습니다.
[\src\main\resources\templates\posts-update.mustache]







 */