package project.tikklemoa_back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import project.tikklemoa_back.entity.UserEntity;
import project.tikklemoa_back.repository.CalendarRepository;
import project.tikklemoa_back.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MonthlyTaskService {
    final private UserRepository userRepository;
    final private CalendarRepository calendarRepository;

    @Autowired
    public MonthlyTaskService(final UserRepository userRepository
            , final CalendarRepository calendarRepository) {
        this.userRepository = userRepository;
        this.calendarRepository = calendarRepository;
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void performMonthlyTask() {
        // 매달 1일 실행
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Monthly task executed at: " + dtf.format(now));

        List<UserEntity> users = userRepository.findAll();
        for (UserEntity user : users) {
            long userId = user.getId();
            updateBadgeForUser(userId);
        }
    }
    private void updateBadgeForUser(long userId) {
        // 이전 달
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        String startOfMonth = lastMonth.atDay(1).atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endOfMonth = lastMonth.atEndOfMonth().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<Object[]> results = calendarRepository.findTotalMinusAndPlus(userId, startOfMonth, endOfMonth);
        if (results.isEmpty() || results.get(0) == null) {
            // 데이터 없을 때 - 제일 높은 등급 뱃지
            userRepository.findById(userId).ifPresent(user -> {
                UserEntity updatedUser = UserEntity.builder()
                        .id(user.getId())
                        .nickname(user.getNickname())
                        .userid(user.getUserid())
                        .userpw(user.getUserpw())
                        .img(user.getImg())
                        .badge("One")
                        .build();
                userRepository.save(updatedUser);
            });
            return;
        }

        Object[] result = results.get(0);
        int totalMinus = result[0] != null ? ((Number) result[0]).intValue() : 0;
        int totalPlus = result[1] != null ? ((Number) result[1]).intValue() : 0;
        double stats = totalPlus != 0 ? ((double) totalMinus / totalPlus) * 100 : 0;

        String nextBadge = determineNextBadge(stats);
        userRepository.findById(userId).ifPresent(user -> {
            UserEntity updatedUser = UserEntity.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .userid(user.getUserid())
                    .userpw(user.getUserpw())
                    .img(user.getImg())
                    .badge(nextBadge)
                    // 다른 필드들도 여기서 설정해야 합니다.
                    .build();
            userRepository.save(updatedUser);
        });
    }

    private String determineNextBadge(double stats) {
        if (stats > 0 && stats < 50) {
            // 수입의 절반 이하 금액을 소비함
            return "One";
        } else if (stats < 75) {
            return "Two";
        } else if (stats < 100) {
            return "Three";
        } else if (stats < 150) {
            return "Four";
        } else {
            // 수입이 0이거나 수입의 1.5배 이상의 금액을 소비함
            return "Five";
        }
    }
    
    // 테스트용 코드
    public void testPerformMonthlyTask() {
        performMonthlyTask();
    }
}
