### Sleep()

- 지정된 시간동안 현재 스레드를 일시정지
- 네이티브 메서드로 연결되며, **시스템 콜을 통해 커널모드에서 실행 후 유저모드로 전환**한다.
    - JVM 단독으로 실행할 수 없다.

**깨어나는 조건**

- 주어진 시간 경과
- 인터럽트 (ex. InterruptedException) 발생
(인터럽트 발생 시 실행 대기 상태로 전환되어, 실행 상태를 기다린다.)

**시스템 콜을 통해 커널모드에서 실행 후 유저모드로 전환 다시 이해하기**

- sleep(millis) 메서드는 네이티브 메서드이기 때문에, 실행 시 시스템콜을 호출하고 유저모드에서 커널모드로 전환된다.
- 만약, 다른 스레드에게 명확하게 실행을 양보 (즉, CPU 할당을 양보) 하기 위함이라면, sleep(0) 보다는 sleep(1) 을 사용하자.

**sleep(0)** 

<img width="683" alt="스크린샷 2024-05-09 오후 11 32 18" src="https://github.com/kihyuk-jeong/Java-Concurrency/assets/39195377/a539b479-103d-4cdc-addc-b2b1e3a1bd98">


- sleep(0) 은 어쨋든 sleep() 메소드를 실행하기 때문에 시스템콜로 인한 커널모드 전환이 일어난다.
- 하지만 sleep(0) 은 우선순위가 동일한 스레드 존재할때만 다른 스레드에게 CPU 를 할당한다.
- 즉, 우선순위가 동일한 스레드가 존재하지 않는다면 시스템콜만 발생하고 스레드가 대기상태로 빠지지 않고 계속 실행 상태로 유지된다.

**sleep(n)** 

- 스레드가 커널 모드로 전환 후 스케줄러는 조건에 상관 없이 현재 스레드를 대기 상태에 두고 다른 스레드에게 CPU 를 할당한다.

**🔑  명확하게 CPU 할당을 다른 스레드에게 넘기기 위함이라면, sleep(1) 을 사용해야 한다.**

**정리**

- sleep() 메소드는 시스템콜로, 유저영역에서 커널 영역으로 전환되며, 전환이 되면 다른 스레드 혹은 프로세스에게 CPU 를 사용하도록 한다.
- 대기 시간이 끝나면 스레드는 실행상태가 아닌 대기상태로 전환되며, CPU 할당을 기다린다. 또한 CPU 가 할당되면 이전 작업부터 진행한다.
- 임계영역에서 sleep() 이 실행되면 흭득한 Lock 을 잃지 않고 계속 가지고 있는다.
(반면에 wati() 메소드는 sleep() 과 마찬가지로 대기 상태로 가지만, Lock 을 잃는다)
- sleep 실행 중 인터럽트에 걸리면 대기상태에서 빠져나오고 예외를 처리한다.

---

### Join()

- join() 은 한 스레드가 다른 스레드가 종료될 때까지 실행을 중지하고 대기상태에 들어갔다가 스레드가 종료되면 실행대기 상태로 전환된다.
- 스레드의 순서를 제어하거나, 다른 스레드의 작업을 기다려야 하거나 순차적인 흐름을 구성하고자 할 때 사용할 수 있다.
- Object 클래스의 wait() 네이티브 메서드로 연결되며, 시스템 콜을 통해 커널모드로 수행한다.
(내부적으로 wait() & notify() 흐름을 가지고 제어한다)

**동작방식**

<img width="713" alt="스크린샷 2024-05-09 오후 11 32 34" src="https://github.com/kihyuk-jeong/Java-Concurrency/assets/39195377/52fd69d6-bc9e-4f69-9a00-1744b82f441d">


- Join 을 거는 순간, 건 스레드에 wait() 이 실행된다.
- Join 대상의 스레드가 작업이 완료되면, join 을 호출한 스레드에게 notify() 를 보낸다.
    - 호출 대상 스레드의 작업이 종료되면, 호출한 스레드는 실행 대기 상태로 전환되고, CPU 에게 작업을 할당받으면 실행 상태로 전환된다.
- **위 flow 외에 Interrupt() 발생에 의해 대기상태에서 빠져나오는 케이스도 존재한다.**
    - 즉, 내가 기다리고 있는 스레드가 인터럽트에 걸리면 나의 대기 상태도 종료된다.

---

### Interrupt

인터럽트의 사전적 의미는 ‘방해하다’ 라는 뜻으로, 어떤 주체의 행동이나 실행 흐름을 방해한다는  의미이다.
→ 자바에서는 인터럽트가 특정 스레드에게 ‘인터럽트 신호’ 를 보내서 스레드의 실행을 중단하거나 작업 취소, 강제 종료 등으로 사용할 수 있다.

**자바의 Interrupt() 메소드**

- 인터럽트는 스레드에게 인터럽트가 발생했다는 신호를 보내는 메카니즘이다.
- 인터럽트는 스레드가 현재 실행 흐름을 멈추고, 인터럽트 이벤트를 먼저 처리하도록 시그널을 보내는 장치라 할 수 있다.
- 스레드는 인터럽트 상태로 알려진 `Interrupted` 를 가지고 있으며, 인터럽트 발생 여부를 확인할 수 있는 상태값이다. 기본값은 false 이다.
- 한 스레드가 다른 스레드를 인터럽트 할 수 있고, 자기 자신도 인터럽트 할 수 있다.
- 인터럽트 할 때 마다 스레드의 인터럽트 상태를 true 로 변경한다.

<img width="721" alt="스크린샷 2024-05-09 오후 11 32 47" src="https://github.com/kihyuk-jeong/Java-Concurrency/assets/39195377/b6793f5f-6fc3-4048-a6f5-4791973c9d7b">


**interrupted() vs isInterrupted()**

- interrupted() 는 static 메소드로, 현재 스레드의 인터럽트 상태를 리턴하고 인터럽트 상태를 초기화 시킨다.
    - 예를들어, 현재 스레드가 인터럽트 상태라면 true 를 리턴하고, 리턴 이후에 인터럽트 상태를 초기화한다. (즉, 리턴 후 상태는 false 가 된다)
- isInterrupted() 는 스레드의 인터럽트 상태를 반환하는 인스턴스 메서드이다.
    - 해당 스레드의 인터럽트 여부만 리턴할 뿐, 추가적인 작업은 없다.

**InterruptedException**

- InterruptedException 은 대기 또는 차단 등 블록킹 상태에 있거나, 블록킹 상태를 만나는 시점의 스레드에 인터럽트 할 때 발생한다.
    - 예를들어, 인터럽트가 걸린 스레드가 sleep 또는 wait 와 같은 대기 또는 블로킹 상태에 걸리게 되면 발생한다.
    - 또한 이미 대기 또는 블로킹 상태의 스레드에 인터럽트를 걸 때도 발생한다.

<img width="718" alt="스크린샷 2024-05-09 오후 11 32 58" src="https://github.com/kihyuk-jeong/Java-Concurrency/assets/39195377/9d6a0aec-9401-4e36-b58f-f84609b40772">


**인터럽트에 걸린 이후 상태가 false 로 변경되는 경우**

인터럽트에 걸리면 해당 스레드의 인터럽트 상태는 true 가 된고, 아래 경우에 false 로 변한다.

- interrupted()  실행
- InterruptedException 예외 처리

---

### 스레드 우선순위 (Priority)

- 단일 CPU 에서 여러 스레드를 실행하는 것을 스케줄링(=OS 스케줄링) 이라고 하며, 스레드는 스케줄링에 의해 선점되어 CPU 를 할당받는다.
- 자바 런타임은 고정 우선순위 선점형 스케줄링으로 알려진 매우 단순하고 결정적인 스케줄링 알고리즘을 지원한다.
→ 이 알고리즘은 실행 대기 상태의 스레드 중에 상대적인 우선 순위에 따라 스레드를 예약한다.
- 참고로 스케줄링이란 것 자체가 CPU 코어 수 < Thread 수 인 경우에만 유효한 개념이다.

**우선순위 개념**

- 자바는 1~19 사이의 정수로 우선순위를 줄 수 있으며, 값이 높을수록 우선순위가 높다.
- **But, 우선순위가 높은 스레드를 실행한다고 보장할 수 없다. 운영체제마다 다른 정책들이 있을 수 있으며 기아상태를 피하기 위해 스케줄러는 우선순위가 낮은 스레드를 선택할 수 있다. (중요)**

---

### 스레드 예외처리

스레드 내부에서 발생한 예외는 스레드 외부에서 처리할 수 없으며, 내부에서 발생한 예외는 그냥 소멸되어 버린다.

```java
public static void main(String [] args) {

			try {
				new Thread(() -> {
						throw new RuntimeException("스레드 예외 발생")
						}).start();
			} catch(Exception e) {
				 notify(e)
			}

}
```

예를 들어 위 예시 코드는 새로 생성한 스레드 내부에서 발생한 예외를 Main 스레드의 catch 구문에서 처리하고자 하였지만, catch 구문의 코드는 실행되지 않는다.

**예외 처리 방법**

1. main 스레드에서 처리하는 방법
- static void setDefaultUncaughtExceptionHandler
    - 모든 스레드에서 발생하는 uncaughtException 을 처리하는 메소드

```java
 public class DefaultExceptionHandlerExample {

    public static void main(String[] args) {

        // 모든 스레드의 예외에 대한 기본 핸들러 설정
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(t.getName() + " 에서 예외 발생 " + e);
            }
        });

        // 예외를 발생시키는 여러 스레드
        Thread thread1 = new Thread(() -> {
            throw new RuntimeException("스레드 1 예외!");
        });

        Thread thread2 = new Thread(() -> {
            throw new RuntimeException("스레드 2 예외!");
        });

        thread1.start();
        thread2.start();
    }
}
```

1. 스레드 개별로 처리하는 방법
- void setUncaughtExceptionHandler(UnaughtExceptionHandler ueh)
    - 대상 스레드에서 발생하는 uncaughtException 을 처리하는 인스턴스 메서드
    - setDefaultUncaughtExceptionHandler 보다 **우선순위가** **높음**

```java
public class UncaughtExceptionHandlerExample {
    private static final Logger LOGGER = Logger.getLogger(UncaughtExceptionHandlerExample.class.getName());

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println("스레드 시작!");

            // 예기치 않은 예외 발생
            throw new RuntimeException("예기치 않은 예외!");
        });

        // 스레드의 UncaughtExceptionHandler 설정
        thread.setUncaughtExceptionHandler((t, e) -> {
            LOGGER.log(Level.SEVERE, t.getName() + " 에서 예외가 발생했습니다.", e);

            // 오류가 발생한 경우 알림 서비스 호출 (예: 이메일 또는 Slack 알림)
            sendNotificationToAdmin(e);
        });

        thread.start();
    }
}
```

**스레드 중지**

- 자바에서는 무한 반복이나 지속적인 실행 중에 있는 스레드를 중지하거나 종료할 수 있는 API 를 더 이상 사용할 수 없다 (suspend(), stop())
- 스레드를 종료하는 방법은 플래그 변수를 사용하거나, interrupt() 를 활용해서 구현할 수 있다.
- 플래그 변수를 사용할 때는 캐시 메모리를 거치지 않는 atomicBoolean 또는 volatile 를 사용한 boolean 전역 변수를 사용하도록 하자.

---

### 사용자 스레드 vs 데몬 스레드

- 자바는 크게 사용자 스레드와 데몬 스레드로 나눌 수 있다.
- 각 스레드들은 부모 스레드와 동일한 상태를 가진다. (사용자 스레드의 자식은 사용자 스레드)
- 자바 애플리케이션이 실행 되면 JVM 은 사용자 스레드인 메인스레드와 나머지 데몬 스레드를 동시에 생성하고 시작한다.

**사용자 스레드**

- 사용자 스레드는 메인 스레드에서 직접 생성한 스레드
- 사용자 스레드는 각각 독립적인 라이프 사이클을 가지고 실행하게 되며, 메인 스레드를 포함한 모든 사용자 스레드가 종료하게 되면 어플리케이션이 종료됨
- foreground 에서 실행되는 높은 우선순위를 가지며, JVM 은 사용자 스레드가 종료되기 전 까지 애플리케이션을 종료하지 않는다.
- 자바의 ThreadPoolExecutor 는 사용자 스레드를 생성함

**데몬 스레드**

- 데몬 스레드는 JVM 에서 생성한 스레드 이거나 직접 데몬 스레드로 (`setDaemon(true)`) 로 생성한 경우를 말한다.
- 데몬 스레드는 사용자 스레드가 모두 종료되면, JVM 이 데몬 스레드를 강제로 종료하고 애플리케이션을 종료한다. (백그라운드에서 실행되는 낮은 우선순위)
    
    → 따라서 생명주기도 사용자 스레드에 의존적이다.
    
- 데몬 스레드는 사용자 스레드를 **보조 및 지원** 하는 성격을 가진 스레드로, 보통 사용자 작업을 방해하지 않으면서 백그라운드에서 자동으로 작동되는 기능을 가진 스레드이다.
- ForkjoinPool 에서 생성되는 스레드는 데몬 스레드다.

---

### 스레드 그룹

- 자바는 스레드 그룹이라는 객체를 통해서 여러 스레드를 그룹화하는 편리한 방법을 제공한다.
- 스레드 그룹 내에는, 다른 스레드 그룹도 포함될 수 있고, 그룹 내의 모든 스레드는 한 번에 종료 되거나 중단될 수 있다. (즉, UserGroup1 안에, subGroup1 이 존재할 수 있는 구조)
- 스레드는 반드시 하나의 스레드 그룹에 포함되어야 하며, 평시적으로 지정하지 않는 경우 자신을 생성한 스레드가 속한 그룹에 속한다.
→ 즉, 일반적으로 메인 스레드에서 생성하는 모든 스레드는 기본적으로 메인 스레드 그룹에 속하게 된다.

---

### 스레드 로컬

모든 스레드가 공통적으로 처리해야 하는 기능이나, 객체를 제어해야 하는 상황에서 **스레드마다 다른 값을 적용해야 하는 경우** 사용한다.

<img width="708" alt="스크린샷 2024-05-09 오후 11 33 23" src="https://github.com/kihyuk-jeong/Java-Concurrency/assets/39195377/39738a27-39ad-4970-b5b7-91e9047e031f">


- 각 클라이언트마다 WAS 로 요청하면 클라이언트 별 스레드가 할당되고, 할당된 스레드 마다 스레드 로컬이 Map 형태로 존재하는 구조이다.
- 직접 사용되는 경우는 많이 없지만, 프레임워크나 라이브러리에서 많아 사용하고 있다.

**스레드 로컬의 작동 원리**

`Thread.currentThread()` 를 활용한다.

- CPU 는 오직 하나의 스레드만 할당받아 처리하기 때문에 스레드 로컬에서 해당 메소드를 참조하여 현재 실행중인 스레드의 로컬 변수를 저장하거나 참조할 수 있게 된다.

**주의사항**

- 스레드 로컬을 스레드 풀에서 사용할 경우, 사용 후 반드시 초기화 해줘야 한다.
- 그렇지 않으면 서로 다른 클라이언트의 요청들이 스레드풀 내 동일한 스레드를 사용하게 될 경우, 의도치 않은 스레드 로컬의 값을 참조하게 된다.
