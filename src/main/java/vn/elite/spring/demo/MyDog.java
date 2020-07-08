package vn.elite.spring.demo;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MyDog {

    /**
     * {@literal @}{@link EventListener} sẽ lắng nghe mọi sự kiện xảy ra Nếu có một sự kiện DoorBellEvent được bắn ra,
     * nó sẽ đón lấy và đưa vào hàm để xử lý {@literal @}{@link Async} là cách lắng nghe sự kiện ở một Thread khác,
     * không ảnh hưởng tới luồng chính
     */
    @Async
    @EventListener
    public void doorBellEventListener(DoorBellEvent doorBellEvent) throws InterruptedException {
        // Giả sử con chó đang ngủ, 1 giây sau mới dậy
        Thread.sleep(1000);
        // Sự kiện DoorBellEvent được lắng nghe và xử lý tại đây
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": Chó ngủ dậy!!!");
        System.out.println(String.format("%s: Go go!! Có người tên là %s gõ cửa!!!", threadName, doorBellEvent.getGuestName()));
    }
}

