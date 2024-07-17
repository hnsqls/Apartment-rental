package com.ls.lease.web.admin.schedule;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScheduleTaskTest {

    @Autowired
    private ScheduleTask scheduleTask;
    @Test
    public void test(){
        scheduleTask.checkLeaseStatus();
    }
}