# Blind-Dating
![image](https://github.com/Blind-Dating/Blind-Dating-BE/assets/59335316/6b771471-8e6c-49b8-aa9a-ba8bbb86b5da)

## 👉🏻 프로젝트 소개
 - 관심사가 맞는 이성과 친해지고 싶을 때
 - 간편하게 여러 사람들과 대화를 나누고 싶을때
 - 이성을 소개해주는 블라인드 데이팅을 이용해주세요!    

[👉🏻블라인드 데이팅 이용해보기 Click](https://fe-zeta.vercel.app)


## 🛠프로젝트 아키텍처
![dffd](https://github.com/Blind-Dating/Blind-Dating-BE/assets/59335316/86abd2cb-2bb2-4700-b08f-3e4fa97df224)




## ⚙기술 스택

### ✔ Back-end
<div>
 <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
 <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"/>
 <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
 <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"/>
 <img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"/>
 </br>
 <img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"/>
 <img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"/>
 <img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"/>
 <img src="https://img.shields.io/badge/elasticache-527FFF?style=for-the-badge&logo=elasti&logoColor=white"/>
 <img src="https://img.shields.io/badge/codedeploy-007054?style=for-the-badge&logo=elasti&logoColor=white"/>
</div>

### ✔ Dev tools
<div>
 <img src="https://img.shields.io/badge/intellijidea-000000?style=for-the-badge&logo=intellijidea&logoColor=white"/>
 <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"/>
 <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"/>
</div>    

## 🪧DB 설계
[👉🏻 DB 상세페이지로 이동](https://www.erdcloud.com/d/FveRbu3AKcJ2Zd3ko)
   
![image](https://github.com/Blind-Dating/Blind-Dating-BE/assets/59335316/346b2553-ffc7-472b-9cdd-fae6a350bbdb)


## 🪧API 문서
[👉🏻 API 문서 상세보기](https://blind-dating.site/swagger-ui/index.html)   
API 문서는 Swagger를 통해 작성되었습니다.

## 💡담당 업무
 - Github Action을 도입하여 CI/CD를 구현했습니다.
 - 엑세스 토큰과 리프레시 토큰을 기반으로 한 인증/인가 시스템을 구현했습니다.
 - 실시간 채팅 기능 구현을 위해 웹소켓을 활용하고 메세지 브로커로 Redis를 도입했습니다.
 - 채팅방 리스트에서 웹소켓 연결을 통해 새로운 메세지가 도착할 때마다 최신 메세지 내용이 갱신되도록 했습니다.
 - Swagger를 이용하여 REST API 문서화를 진행하여 협업의 효율성을 높였습니다.

## 💡주요 기능
1. 회원가입
2. 로그인
3. 이성 추천 리스트
4. 좋아요 기능
5. 실시간 채팅

## :rocket: 개발 이슈
### 1. Jacoco 도입후 코드 커버리지 100%  
기존 controller와 service에 대한 테스트 코드를 성공 케이스에 대해서만 작성을 했었습니다. 좀더 좋은 가독성과 성능을 위해 리팩토링 후에 배포를 진행했는데 예상치 못한 곳에서
여러번 원하는 대로 기능이 동작하지 않았었고, 이를 통해 테스트 코드와 테스트의 자동화의 중요성을 인식하였습니다. 이런 경험을 통해 안정적인 시스템을 구축하고자 테스트코드가
프로턱션 코드를 검증 할 수 있도록 Jacoco를 도입한 후 Controller, Service에 대해 100%의 커버리지를 달성했습니다. 
            
![image](https://github.com/Blind-Dating/Blind-Dating-BE/assets/59335316/726fe6c8-824b-4d5b-accb-590ac85f3680)

### 2. 다중 패치 조인시 오류 발생
추천유저 조회시 유저정보와 함께 해당 유저의 관심사와 질의답변을 조회했어야 했고, 이는 N+1의 문제를 야기했습니다. 해당문제를 해결하기 위해 하나의 쿼리로 유저정보, 관심사, 질의답변 내용을
조회하고자 JPQL을 작성했었고, 테스트시 MultipleBagFetchException이 발생했습니다. 해당 예외는 List 타입의 2개 이상의 엔티티를 패치조인 할때 발생하는 예외이고 이 문제를 해결하기 위해 
BatchSize를 적용했습니다. 기본적으로 추천 유저는 페이지네이션 처리를 하였고, 한 페이지당 10명을 조회하기에 BatchSize는 10으로 정해서 MultipleBagFetchException과 N+1 문제를 해결했습니다.

### 3. ElastiCache에서 요금 발생  
AWS에서 발생한 만원 이상의 요금을 조사하기 위해 요금 명세서를 확인해 보았습니다. 결과적으로 Redis를 위한 ElastiCache에서 요금이 발생한 것을 확인할 수 있었습니다. 
해당 서비스의 구성을 확인해 본 결과 노드의 수가 3개로 설정되어있었고, 이 3개의 노드가 24시간 동안 작동하여 기본 제공량인 750시간을 초과하면서 요금이 발생한 것을 파악했습니다.
이에 따른 과금 문제를 해결하기 위해 노드의 수를 3개에서 1개로 줄여서 문제를 해결하였습니다.


### 4. Post/Patch 테스트 코드 동작시 403 Forbidden 발생    
Get 테스트 코드는 잘 실행되는데 Post, Patch, Delete 테스트 코드 실행 시 csrfToken이 요청 시 주어지지 않아 403 Forbidden이 발생하는 것을 확인하였습니다.
이에 대해 조사한 결과, @WebMvcTest는 보안 구성을 자동으로 로드하지 않는 것을 확인하였습니다. 이를 해결하기 위해 SecurityConfig 클래스를 `@Import(SecurityConfig.class)`를 추가해
로드해주고, `csrf().disable()` 설정을 통해 csrfToken을 사용하지 않도록 함으로써 해당 문제를 해결할 수 있었습니다.



## 👻 Blind-Dating 팀원들!
<table>
  <tbody>
    <tr>
     <th>Role</th>
     <th>Name</th>
     <th>Github</th>
    <tr/>
    <tr>
     <td>팀장 FE</td>
     <td>Ted JV</td>
     <td>https://github.com/ted-jv</td>
    </tr>
    <tr>
     <td>팀원 FE</td>
     <td>mijeong</td>
     <td>https://github.com/snakechickensoup</td>
    </tr>
    <tr>
     <td>팀원 BE</td>
     <td>박연준</td>
     <td>https://github.com/pyjhoop</td>
    </tr>
      
  </tbody>
</table>




