package com.app.user.sheduler;

import com.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserScheduler {

    @Autowired
    private UserService userService;

    @Scheduled(cron = "*/10 * * * * ?")
    public void deleteAccount(){
        userService.deleteAccount();
    }
}
