package vn.elite.spring.demo;

import lombok.*;
import org.springframework.context.ApplicationEvent;

/**
 * DoorBellEvent phải kế thừa lớp ApplicationEvent của Spring Như vậy nó mới được coi là một sự kiện hợp lệ.
 */
@Getter
public class DoorBellEvent extends ApplicationEvent {
    private final String guestName;

    /**
     * Mọi Class kế thừa ApplicationEvent sẽ phải gọi Constructor tới lớp cha.
     */
    public DoorBellEvent(Object source, String guestName) {
        // Object source là object tham chiếu tới nơi đã phát ra event này!
        super(source);
        this.guestName = guestName;
    }
}
