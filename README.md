# 로그인

도메인 = 화면, UI, 기술 인프라 등등의 영역은 제외한 시스템이 구현해야 하는 핵심 비즈니스 업무 영역을 말함

향후 web을 다른 기술로 바꾸어도 도메인은 그대로 유지할 수 있어야 한다.

이렇게 하려면 web은 domain을 알고있지만 domain은 web을 모르도록 설계해야 한다.

이것을 web은 domain을 의존하지만, domain은 web을 의존하지 않는다고 표현한다.

예를 들어 web 패키지를 모두 삭제해도 domain에는 전혀 영향이 없도록 의존관계를 설계하는 것이 중요하다.

반대로 이야기하면 domain은 web을 참조하면 안된다.

## 쿠키

사용자의 정보를 클라이언트(브라우저)에서 보관하기 위해 서버에서 만들어진 텍스트 파일

쿠키는 심각한 보안 문제가 있다.

1. 임의로 값을 변경할 수 있다.
2. 쿠키에 저장된 정보를 훔쳐갈수 있다. (누구나 쉽게 볼수 있다. 따라서 악의적인 사용 가능)

**대안**

1. 쿠키에는 중요한 정보를 노출하지 않는다. 토큰을 통해서 값을 노출하도록 한다.
2. 토큰은 해커가 임의의 값을 넣어도 찾을 수 없도록 예상 불가능 해야 한다.
3. 해커가 토큰을 털어가도 시간이 지나면 사용할 수 없도록 서버에서 해당 토큰의 만료시간을 짧게(예: 30분) 유지 한다. 또는 해킹이 의심되는 경우 서버에서 해당 토큰을 강제로 제거하면 된다.

## 세션

클라이언트로부터 오는 일련의 요청을 하나의 상태로 보고 그 상태를 일정하게 유지하는 기술

세션 ID를 생성하는데 추정 불가능해야 한다.

UUID는 추정이 불가능하다.

생성된 세션ID와 세션에 보관할 값을 서버에 세션 저장소에 보관한다.

**정리**

쿠키 값은 변조 가능 -> 예상 불가능한 복잡한 세션ID를 사용한다.

쿠키에 보관하는 정보는 해킹을 당해도 임의 값(UUID)만 있기 때문에 중요정보를 알수 없다.

또한 시간이 지나면 (서버에서 설정한 시간) 해당 세션은 더 이상 유효하지 않다.

### 구성

1. 세션 생성
2. 세션 조회
3. 세션 만료

**HttpSession 소개**

서블릿이 제공하는 `HttpSession` 도 결국 우리가 직접 만든 `SessionManager` 와 같은 방식으로 동작한다.

서블릿을 통해 `HttpSession` 을 생성하면 다음과 같은 쿠키를 생성한다.

쿠키 이름이 `JSESSIONID` 이고, 값은 추정 불가능한 랜덤 값이다.

### 세션 생성과 조회

세션을 생성하려면 `request.getSession(true)` 를 사용하면 된다.

```java
public HttpSession getSession(boolean create);
```

1) `request.getSession(true)`: default

세션이 있으면 기존 세션을 반환한다.

세션이 없으면 새로운 세션을 생성해서 반환한다.

2) `request.getSession(false)`

세션이 있으면 기존 세션을 반환한다.

세션이 없으면 새로운 세션을 생성하지 않는다. `null` 을 반환한다.

**세션에 로그인 회원 정보 보관**

```text
session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
session.setAttribute(SessionConst.USERNAME, "username");
```

세션에 데이터를 보관하는 방법은 `request.setAttribute(..)` 와 비슷하다.

하나의 세션에 여러 값을 저장할 수 있다.

### @SessionAttribute

```java
public String homeLoginV3Spring(
        @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {
}
```

### TrackingModes

로그인을 처음 시도하면 URL이 다음과 같이 `jsessionid` 를 포함하고 있는 것을 확인할 수 있다.

```text
http://localhost:8080/;jsessionid=F59911518B921DF62D09F0DF8F83F872
```

이것은 웹 브라우저가 쿠키를 지원하지 않을 때 쿠키 대신 URL을 통해서 세션을 유지하는 방법이다.

이 방법을 사용하 려면 URL에 이 값을 계속 포함해서 전달해야 한다.

타임리프 같은 템플릿은 엔진을 통해서 링크를 걸면 `jsessionid` 를 URL에 자동으로 포함해준다.

서버 입장에서 웹 브라우저가 쿠키를 지원하는지 하지 않는지 최초에는 판단하지 못하 므로, 쿠키 값도 전달하고, URL에 `jsessionid` 도 함께 전달한다.

URL 전달 방식을 끄고 항상 쿠키를 통해서만 세션을 유지하고 싶으면 다음 옵션을 넣어주면 된다.

```properties
server.servlet.session.tracking-modes=cookie
```

이렇게 하면 URL에 `jsessionid` 가 노출되지 않는다.

반대로 꼭 필요하다면

```properties
spring.mvc.pathmatch.matching-strategy=ant_path_matcher
```

### httpSession 속성

`sessionId` : 세션Id, `JSESSIONID` 의 값이다. 예) `34B14F008AA3527C9F8ED620EFD7A4E1`

`maxInactiveInterval` : 세션의 유효 시간, 예) 1800초, (30분)

`creationTime` : 세션 생성일시

`lastAccessedTime` : 세션과 연결된 사용자가 최근에 서버에 접근한 시간, 클라이언트에서 서버로 `sessionId` (`JSESSIONID`)를 요청해서 조회된 세션인지 여부

`isNew` : 새로 생성된 세션인지, 아니면 이미 과거에 만들어졌고, 클라이언트에서 서버로 `sessionId`

### 세션 타임아웃 설정

세션은 사용자가 로그아웃을 직접 호출해서 `session.invalidate()` 가 호출 되는 경우에 삭제된다.

그런데 대부분의 사용자는 로그아웃을 선택하지 않고, 그냥 웹 브라우저를 종료한다.

문제는 HTTP가 비 연결성(ConnectionLess) 이므로 서버 입장에서는 해당 사용자가 웹 브라우저를 종료한 것인지 아닌지를 인식할 수 없다.

따라서 서버에서 세션 데이터를 언제 삭제해야 하는지 판단하기가 어렵다.

이 경우 남아있는 세션을 무한정 보관하면 다음과 같은 문제가 발생할 수 있다.

세션과 관련된 쿠키( `JSESSIONID` )를 탈취 당했을 경우 오랜 시간이 지나도 해당 쿠키로 악의적인 요청을 할 수 있다.

세션은 기본적으로 메모리에 생성된다.

메모리의 크기가 무한하지 않기 때문에 꼭 필요한 경우만 생성해서 사용해야 한다.

10만명의 사용자가 로그인하면 10만개의 세션이 생성되는 것이다.

**세션의 종료 시점**

세션의 종료 시점을 어떻게 정하면 좋을까? 가장 단순하게 생각해보면, 세션 생성 시점으로부터 30분 정도로 잡으면 될 것 같다.

그런데 문제는 30분이 지나면 세션이 삭제되기 때문에, 열심히 사이트를 돌아다니다가 또 로그인을 해서 세션을 생성해야 한다 그러니까 30분 마다 계속 로그인해야 하는 번거로움이 발생한다.

더 나은 대안은 세션 생성 시점이 아니라 사용자가 서버에 최근에 요청한 시간을 기준으로 30분 정도를 유지해주는 것이다.

이렇게 하면 사용자가 서비스를 사용하고 있으면, 세션의 생존 시간이 30분으로 계속 늘어나게 된다.

따라서 30 분 마다 로그인해야 하는 번거로움이 사라진다. `HttpSession` 은 이 방식을 사용한다.

**세션 타임아웃 설정**

스프링 부트로 글로벌 설정

```properties
server.servlet.session.timeout=60 # 60초, 기본은 1800(30분) 
# (글로벌 설정은 분 단위로 설정해야 한다. 60(1분), 120(2분). 
# 10(10초), 70(1분 10초) 이렇게 안된다.
```

특정 세션 단위로 시간 설정

```java
session.setMaxInactiveInterval(1800); //1800초
```

**세션 타임아웃 발생**

세션의 타임아웃 시간은 해당 세션과 관련된 `JSESSIONID` 를 전달하는 HTTP 요청이 있으면 현재 시간으로 다시 초기화 된다.

이렇게 초기화 되면 세션 타임아웃으로 설정한 시간동안 세션을 추가로 사용할 수 있다.

`session.getLastAccessedTime()` : 최근 세션 접근 시간

`LastAccessedTime` 이후로 timeout 시간이 지나면, WAS가 내부에서 해당 세션을 제거한다.

**정리**

서블릿의 `HttpSession` 이 제공하는 타임아웃 기능 덕분에 세션을 안전하고 편리하게 사용할 수 있다.

실무에서 주의 할 점은 세션에는 최소한의 데이터만 보관해야 한다는 점이다.

보관한 데이터 용량 * 사용자 수로 세션의 메모리 사용량 이 급격하게 늘어나서 장애로 이어질 수 있다.

추가로 세션의 시간을 너무 길게 가져가면 메모리 사용이 계속 누적 될 수 있으므로 적당한 시간을 선택하는 것이 필요하다.

기본이 30분이라는 것을 기준으로 고민하면 된다.

# 서블릿 필터

로그인한 사람이 상품 관리 페이지(등록, 조회, 수정등)에 접근할다고 할때, 모든 컨트롤 로직에 공통 로그인 로직을 넣어야한다.

이는 유지보수 관점에서 좋지 않다.

이러한 공통 관심사는 스프링의 AOP로도 해결할 수 있지만, 웹과 관련된 공통 관심사는 지금부터 설명할 서블릿 필터 또는 스프링 인터셉터를 사용하는 것이 좋다.

웹과 관련된 공통 관심사를 처리할 때는 HTTP의 헤더나 URL의 정보들 이 필요한데, 서블릿 필터나 스프링 인터셉터는 `HttpServletRequest` 를 제공한다.

## 필터

HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 컨트롤러

필터를 적용하면 필터가 호출 된 다음에 서블릿이 호출된다.

그래서 모든 고객의 요청 로그를 남기는 요구사항이 있다면 필터를 사용하면 된다.

참고로 필터는 특정 URL 패턴에 적용할 수 있다. `/*` 이라고 하면 모든 요청에 필터가 적용된다.

참고로 스프링을 사용하는 경우 여기서 말하는 서블릿은 스프링의 디스패처 서블릿으로 생각하면 된다.

### 필터 제한

HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 컨트롤러 //로그인 사용자

HTTP 요청 -> WAS -> 필터(적절하지 않은 요청이라 판단, 서블릿 호출X) //비 로그인 사용자

**필터 체인**

HTTP 요청 -> WAS -> 필터1 -> 필터2 -> 필터3 -> 서블릿 -> 컨트롤러

필터는 체인으로 구성되는데, 중간에 필터를 자유롭게 추가할 수 있다. 예를 들어서 로그를 남기는 필터를 먼저 적용하 고, 그 다음에 로그인 여부를 체크하는 필터를 만들 수 있다.

```java

public interface Filter {

    default void init(FilterConfig filterConfig) throws ServletException {}

    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException;

    default void destroy() {}
}
```

필터 인터페이스를 구현하고 등록하면 서블릿 컨테이너가 필터를 싱글톤 객체로 생성하고, 관리한다.

`init():` 필터 초기화 메서드, 서블릿 컨테이너가 생성될 때 호출된다.

`doFilter():` 고객의 요청이 올 때 마다 해당 메서드가 호출된다. 필터의 로직을 구현하면 된다.

`destroy():` 필터 종료 메서드, 서블릿 컨테이너가 종료될 때 호출된다.

### 필터 등록

필터를 등록하는 방법은 여러가지가 있지만, 스프링 부트를 사용한다면 `FilterRegistrationBean` 을 사용해서 등 록하면 된다.

`setFilter(new LogFilter())` : 등록할 필터를 지정한다.

`setOrder(1)` : 필터는 체인으로 동작한다. 따라서 순서가 필요하다. 낮을 수록 먼저 동작한다.

`addUrlPatterns("/*")` : 필터를 적용할 URL 패턴을 지정한다. 한번에 여러 패턴을 지정할 수 있다

**실무에서 HTTP 요청시 같은 요청의 로그에 모두 같은 식별자를 자동으로 남기는 방법은 logback mdc로 검색 해보자.**

# 인터셉터

서블릿 필터와 같이 공통 관심사항을 해결하는 기술

필터는 서블릿이 제공, 인터셉터는 스프링 MVC가 제공

적용되는 순서, 범위, 사용 방법이 다르다.

### 흐름

HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 스프링 인터셉터 -> 컨트롤러

인터셉터는 디스패처 스블릿 이후에 등장

### 제한

HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 스프링 인터셉터 -> 컨트롤러 //로그인 사용자

HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 스프링 인터셉터(적절하지 않은 요청이라 판단, 컨트롤러 호출X) // 비 로그인 사용자

**체인**

HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 인터셉터1 -> 인터셉터2 -> 컨트롤러

```java
public interface HandlerInterceptor {

    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {}

    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {}

    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) {}
}
```

서블릿 필터의 경우 단순하게 `doFilter()` 하나만 제공된다.

인터셉터는 컨트롤러 호출 전( `preHandle` ), 호출 후( `postHandle` ), 요청 완료 이후( `afterCompletion` )와 같이 단계적으로 잘 세분화 되어 있다.

서블릿 필터의 경우 단순히 `request` , `response` 만 제공했지만, 인터셉터는 어떤 컨트롤러( `handler` )가 호출되는지 호출 정보도 받을 수 있다.

그리고 어떤 `modelAndView` 가 반환되는지 응답 정보도 받을 수 있다.

### 인터셉터 호출 흐름

* `preHandle` : 컨트롤러 호출 전에 호출된다. (더 정확히는 핸들러 어댑터 호출 전에 호출된다.)

`preHandle` 의 응답값이 `true` 이면 다음으로 진행하고, `false` 이면 더는 진행하지 않는다. `false` 인 경우 나머지 인터셉터는 물론이고, 핸들러 어댑터도 호출되지 않는다. 그림에서
1번에서 끝이 나버린다.

* `postHandle` : 컨트롤러 호출 후에호출된다. (더 정확히는 핸들러 어댑터 호출 후에 호출된다.)

* `afterCompletion` : 뷰가 렌더링 된 이후에 호출된다.

### 예외 발생

**예외가 발생시**

`preHandle` : 컨트롤러 호출 전에 호출된다.

`postHandle` : 컨트롤러에서 예외가 발생하면 `postHandle` 은 호출되지 않는다.

`afterCompletion` : `afterCompletion` 은 항상 호출된다. 이 경우 예외( `ex` )를 파라미터로 받아서 어떤 예외가 발생했는지 로그로 출력할 수 있다.

**afterCompletion은 예외가 발생해도 호출된다.**

예외가 발생하면 `postHandle()` 는 호출되지 않으므로 예외와 무관하게 공통 처리를 하려면 `afterCompletion()` 을 사용해야 한다.

예외가 발생하면 `afterCompletion()` 에 예외 정보( `ex` )를 포함해서 호출된다.
