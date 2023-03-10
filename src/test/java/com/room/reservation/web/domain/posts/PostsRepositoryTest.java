package com.room.reservation.web.domain.posts;

//PostRepositoryTest에서 save, findAll 기능을 테스트합니다.

import com.room.reservation.domain.posts.Posts;
import com.room.reservation.domain.posts.PostsRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {
    @Autowired
    PostsRepository postsRepository;

//    @After
//    - JUnit에서 단위테스트가 끝날때마다 수행되는 메소드를 지정합니다.
//    - 보통은 배포 전 전체 테스트를 수행할때 테스트 간 데이터 침범을 막기 위해 사용합니다.
//    - 여러 테스트가 동시에 수행되면 테스트용 데이터베이스인 H2에 데이터가 그대로 남아있어 다음 테스트 실행시 테스트가 실패할 수 있습니다.
    @After
    public void cleanup(){
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기(){
        //given
        String title="테스트게시글";
        String content ="테스트본문";

//      PostRepository.save
//      - 테이블 posts에 insert/update 쿼리를 실행합니다.
//      - id값이 있다면 update, 없다면 insert 쿼리가 실행됩니다.
        postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("daeho@mail.com")
                .build());

        //when
//      PostRepsotory.findAll
//        -테이블 Posts에 있는 모든 데이터를 조회해오는 메소드입니다.
        List<Posts> postsList = postsRepository.findAll();

        //then
        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }

    //JPA Auditing 테스트 코드 작성
    @Test
    public void BaseTimeEntity_등록(){
        //given
        LocalDateTime now = LocalDateTime.of(2019,6,2,0,0,0);
        //postsRepository 오타로 non-static method 'save(s)' cannot be referenced from a static context 에러났었습니다.
        postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        //when
        List<Posts> postsList = postsRepository.findAll();

        //then
        Posts posts = postsList.get(0);

        System.out.println(">>>>>>>>>>>>>> createDate="+posts.getCreatedDate()+", modifiedDate="+posts.getModifiedDate());

        assertThat(posts.getCreatedDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);


    }

}


/*
별다른 설정없이 @SpringBootTest를 사용할경우 H2 데이터베이스를 자동으로 실행해줍니다.
이 테스트 역시 실행할 경우 H2가 자동으로 실행됩니다. 이 테스트를 실행해보겠습니다.

실행된 쿼리를 로그로 보기위하여,
쿼리로그를 ON/OFF할 수 있는 설정도 있고, 이런 설정들을 JAVA클래스로 구현할 수 있으나
실행된 쿼리를 로그로 보기 위해 스프링부트에서는 application.properties, application.yml 등의 파일로 한줄의 코드로 설정할 수 있도록 지원하고 권장합니다.

src/main/resources 디렉토리 아래에 application.properties 파일을 생성합니다.
spring.jpa.show_sql=true 을 추가합니다.

다시 테스트를 실행해보면
create table posts (id bigint generated by default as identity, author varchar(255), content TEXT not null, title varchar(500) not null, primary key (id))
insert into posts (id, author, content, title) values (null, ?, ?, ?)
delete from posts where id=?
drop table posts if exists

이런 로그를 확인할 수 있습니다. 여기서 한가지 걸리는 것이 있습니다.
create table 쿼리를 보면 id bigint generated by default as identity 라는 옵션으로 생성됩니다.
이는 H2의 쿼리 문법이 적용되었기 때문입니다. H2는 MySQL의 쿼리를 수행해도 정상적으로 작동하기 때문에 이후 디버깅을 위해서 출력되는 쿼리 로그를 MySQL 버전으로 변경합니다.

이 옵션 역시 [src/main/resources/application.properties] 파일에서 설정합니다.
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect

이제 다시 테스트코드를 실행하여 로그를 확인합니다.
create table posts (id bigint not null auto_increment, author varchar(255), content TEXT not null, title varchar(500) not null, primary key (id)) engine=InnoDB
id bigint not null auto_increment로 MySQL 문법으로 잘 변경이 되었습니다.


이제 3.4 등록/수정/조회 API 만들기입니다.
API를 만들기 위해 총 3개의 클래스가 필요합니다.
-Request 데이터를 받을 DTO
-API 요청을 받을 Controller
-트랜잭션, 도메인 기능 간의 순서를 보장하는 Service
여기서 많은 분들이 오해하는 점은 Service에서 비즈니스 로직을 처리해야한다는 것입니다.
하지만, 전혀 그렇지 않습니다.
Service는 트랜잭션, 도메인 간 순서 보장의 역할만 합니다.
그럼 비즈니스 로직은 어디서 처리하나요?
다음 그림을 보겠습니다.
Web Layer
Service Layer
Repository Layer
Dtos
Domain Model

(1) Web Layer
- 흔히 사용하는 컨트롤러와 JSP/FreeMaker등의 뷰 템플릿 영역입니다.
- 이외에도 필터, 인터셉터, 컨트롤러 어드바이스 등 외부 요청과 응답에 대한 전반적인 영역을 이야기합니다.

(2) Service Layer
- @Service에 사용되는 서비스 영역입니다.
- 일반적으로 Controller와 DAO의 중간 영역에서 사용됩니다. 나 같은겨우 ServiceImpl 입니다.
- @TransActional이 사용되어야하는 영역이기도 합니다.

(3) Repository Layer
- Database와 같이 데이터 저장소에 접근하는 영역입니다.
- 기존에 개발하셨던 분들이라면 Dao(Data Acess Object) 영역으로 이해하시면 됩니다.

(4) Dtos
- DTO(Data Transfer Object)는 계층 간에 데이터 교환을 위한 객체를 이야기하며 Dtos는 이들의 영역을 이야기합니다.
- 예를들어 뷰 템플릿 엔진에서 사용될 객체나 Repisotiry Layer에서 결과로 넘겨준 객체 등이 이들을 이야기합니다.

(5) Domain Model
- 도메인이라 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화시킨 것을 도메인모델이라고 합니다.
- 이를테면 택시 앱이라고하면 배차, 탒승, 요금 등이 모두 도메인이 될 수 있습니다.
- @Entity를 사용해보신 분들은 @Entity가 사용된 영역 역시 도메인 모델이라고 이해해주시면 됩니다.
- 다만, 무조건 데이터베이스의 테이블과 관계가 있어야만 하는것은 아닙니다.
- VO 처럼 값 객체들도 이 영역에 해당하기 때문입니다.

Web(Controller), Service, Repository, Dto, Domain 이 5가지 레이어에서 비즈니스 처리를 담당해야할 곳은 어디일까여?
바료 Domain 입니다.
기존에 서비스로 처리하던 방식을 트랜잭션 스크립트라고 합니다.(이거 제가 MVC에서 하던 것입니다.)
주문 취소 로직을 작성한다면 다음과 같습니다.

먼저 슈도코드입니다.
@TransActional
public Order cancelOrder(int orderId){
    1)데이터베이스로부터 주문정보(Orders), 결제정보(Billing), 배송정보(Delivery) 조회

    2)배송취소를 해야하는지 확인

    3) if(배송중이라면){
        배송취소로 변경
      }

    4) 각 테이블에 취소상태 Update
}

@Transactional
public Order cancelOrder(int orderid){
    //1)
    OrdersDto order = ordersDao.selectOrders(orderId);
    BillingDto billing = billingDao.selectBilling(orderId);
    DeliveryDto delivery = deliveryDao.selectDelivery(orderId);

    //2)
    String deliveryStatus = delivery.getStatus();

    //3)
    if("IN_PROGRESS".equals(deliveryStatus)){
        delivery.setStatus("CAMCEL");
        deliveryDao.update(delivery);
    }

    //4)
    order.setStatus("CANCEL");
    ordersDao.update(order);

    billing.setStatus("CANCEL");
    deliveryDao.update(billing);

    return order;
}
--> 이거 내가 딱 하던 방식입니다 ServiceImpl에서 모든 작업을 처리하고, 객체는 단순히 데이터 덩어리 역할만했습니다.

모든 로직이 서비스 클래스 내부에서 처리됩니다. 그러다보니 서비스계층이 무의미하며, 객체란 단순히 데이터 덩어리 역할만 하게됩니다.
반면 도메인 모델에서 처리할 경우 다음과 같은 코드가 될 수 있습니다.

@TransActional
public Order cancelOrder(int orderId){
    //1)
        Orders order = ordersRepository.findById(orderId);
        Billing billing = billingRepository.findByOrderId(orderId);
        Delivery delivery = deliveryRepository.findByOrderId(orderId);

    //2-3)
        delivery.cancel();

    //4)
        order.cancel();
        billing.cancel();

        return order;
}

order, billing, delivery가 각자 본인의 취소 이벤트 처리를 하며, 서비스메소드는 트랜잭션과 도메인 간의 순서만 보장해줍니다.
이 책에서는 계속 이렇게 도메인 모델을 다루고 코드를 작성합니다.
그럼 등록, 수정, 삭제 기능을 만들어 보겠습니다.
PostsApiController를 [src/main/java/com.meetingroom.reservation/web/PostsApiController.java] 패키지에,
PostsSaveRequestDto를 [src/main/java/com.meetingroom.reservation/web/dto/PostsSaveRequestDto.java] 패키지에,
PostsService를 [src/main/java/com.meetingroom.reservation/service/posts/PostsService] 패키지에 생성합니다.


---

BaseTimeEntity_등록 테스트.
테스트 코드를 수행해보면 다음과 같이 실제 시간이 잘 저장된것을 확인할 수 있습니다.
>>>>>>>>>>>>>> createDate=2023-02-21T05:59:58.901, modifiedDate=2023-02-21T05:59:58.901
앞으로 추가될 엔티티들은 더이상 등록일/수정일로 고민할 필요가 없습니다.
BaseTimeEntity만 상속받으면 자동으로 해결되기 때문입니다.
다음장에서는 템플릿 엔진을 이용하여 화면을 만들어보겠습니다.


 */