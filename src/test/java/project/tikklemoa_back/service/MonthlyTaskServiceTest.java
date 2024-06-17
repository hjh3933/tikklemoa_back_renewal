package project.tikklemoa_back.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MonthlyTaskServiceTest {

    @Autowired
    private MonthlyTaskService monthlyTaskService;

    @Test
    public void testPerformMonthlyTask() {
        monthlyTaskService.testPerformMonthlyTask();
    }
}