개발언어 : JAVA, DB : H2

개발환경 :
SpringBoot2, Gradle, JPA, Lombok

github에서 clone하고, gradle project 로 import 한후 lombok 세팅만 해주면 빌드됩니다.
src/main/java/com/rest/api/Applications.java 로 서버실행

http://localhost:8080/swagger-ui.html 를 통해 API문서에 접근 가능합니다.
payment-controller 선택
/v1/DoPayment : 결제API
/v1/CancelPayment : 취소API
/v1/GetPayment/{uid} : 조회API

테이블은 결제정보가 저장되는 PAYMENT, 취소정보가 저장되는 CANCELLATION 두개로 만들었으며
취소정보는 PAYMENT_UID 를 FK 로하여 결제정보와 join 됩니다.
JPA+Lombok 을 이용하여  Entity 를 생성하였습니다.

src/main/java/com/rest/api/controller/v1/PaymentController.java
에서 위 세가지 API를 모두 구현하였습니다.

암복호화는 utils package 에 crypto.java 에 따로 만들었습니다.
AES-256 방식을 이용하여 인터넷에 있는 예제를 참고 하여 만들었고, key는 우선은 소스안에 하드코딩하였습니다.

UNIQUE ID 생성, 카드사 전송 String Data 만들기나 기타 필요한 함수들은 utils 에 commonUtils 에 만들었습니다.

UNIQUE ID 20 자리는 현재시각을 밀리세컨까지하여 17자리에, 뒤 두자리는 한자리씩 난수 발생하여 붙였고, 결제ID인경우 앞에 P, 취소ID인경우 앞에 C를 붙여 생성하였습니다.

DB와 인터페이스는 최대한 JPA repository 를 이용하였습니다.

리턴하는 결과값은 ResultDTO 클래스들을 만들어 사용하였습니다.

리턴하는 메세지들은 인터넷에 구현되어있는 ResponseService 클래스를 참고하여 수정해서 만들었습니다.

중복호출을 방지하기 위해 전역MAP을 사용하여 체크하였으며, 첫 호출 이후 5초이내 동일번호 호출은 모두 중복처리 하였습니다.

하나의 처리가 5초가 넘어가는 경우를 대비해서 종료시에 종료키를 추가하여 5초가 지났더라도 종료가 되었는지 다시 체크하도록 했습니다.


전체적인 framework 세팅은  
https://daddyprogrammer.org/post/1212/springboot2-create-by-spring-initializr/
를 참고하여 개발하였음을 밝힙니다.

감사합니다.

