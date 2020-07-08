package vn.elite.spring.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {
    @Autowired
    MyHouse myHouse;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    CommandLineRunner run() {
        return args -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + ": Loda đi tới cửa nhà !!!");
            System.out.println(threadName + ": => Loda bấm chuông và khai báo họ tên!");
            // gõ cửa
            myHouse.rangDoorbellBy("Loda");
            System.out.println(threadName +": Loda quay lưng bỏ đi");
        };
    }

}
