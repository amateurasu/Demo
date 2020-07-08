package vn.elite.spring.demo;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MyHouse {
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Hành động bấm chuông cửa
     */
    public void rangDoorbellBy(String guestName) {
        // Phát ra một sự kiện DoorBellEvent
        // source (Nguồn phát ra) chính là class này
        applicationEventPublisher.publishEvent(new DoorBellEvent(this, guestName));
    }
}
