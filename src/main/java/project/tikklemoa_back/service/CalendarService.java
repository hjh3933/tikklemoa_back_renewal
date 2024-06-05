package project.tikklemoa_back.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.tikklemoa_back.dto.*;
import project.tikklemoa_back.entity.CalendarEntity;
import project.tikklemoa_back.entity.SettingEntity;
import project.tikklemoa_back.entity.UserEntity;
import project.tikklemoa_back.repository.CalendarRepository;
import project.tikklemoa_back.repository.SettingRepository;
import project.tikklemoa_back.repository.UserRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CalendarService {
    final private UserRepository userRepository;
    final private CalendarRepository calendarRepository;
    final private SettingRepository settingRepository;

    @Autowired
    public CalendarService(final UserRepository userRepository
            , final SettingRepository settingRepository
            , final CalendarRepository calendarRepository) {
        this.userRepository = userRepository;
        this.calendarRepository = calendarRepository;
        this.settingRepository = settingRepository;
    }

    public CalendarEntity insertCalendar(CalendarDTO calendarDTO, long id) {
        // 현재 로그인한 계정의 회원 검증 후 외래키로 사용
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));

        // dto -> entity
        // calendarDTO.getDate()가 yyyy-MM-dd 중요!!
        CalendarEntity calendar = CalendarEntity.builder()
                .date(Date.valueOf(calendarDTO.getDate()))
                .category(CalendarEntity.Category.valueOf(calendarDTO.getCategory()))
                .subcategory(calendarDTO.getSubcategory())
                .price(calendarDTO.getPrice())
                .details(calendarDTO.getDetails())
                .user(user)
                .build();

        // 추가한 객체 리턴
        return calendarRepository.save(calendar);
    }

    public ArrayList<MonthCalendarDTO> getMonth(String dateStr, long id) {
        // yyyy-mm을 분리함
        String[] parts = dateStr.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        List<Object[]> results = calendarRepository.findByYearAndMonthAndUserIdGroupedByDate(year, month, id);
        ArrayList<MonthCalendarDTO> dtos = new ArrayList<>();

        for (Object[] result : results) {
            String date = result[0].toString();
            int totalMinus = ((Number) result[1]).intValue();
            int totalPlus = ((Number) result[2]).intValue();
            int dateTotal = ((Number) result[3]).intValue();

            MonthCalendarDTO dto = MonthCalendarDTO.builder()
                    .date(date)
                    .totalMinus(totalMinus)
                    .totalPlus(totalPlus)
                    .dateTotal(dateTotal)
                    .build();
            dtos.add(dto);
        }


        return new ArrayList<>(dtos);
    }

    public ArrayList<CalendarDTO> getDate(String dateStr, long id) {
        //dateStr = yyyy-mm-dd 이므로 바로 넣음
        List<CalendarEntity> calendarEntities = calendarRepository.findByDateAndUserId(dateStr, id);
        ArrayList<CalendarDTO> dtos = new ArrayList<>();

        for (CalendarEntity entity : calendarEntities) {
            CalendarDTO dto = CalendarDTO.builder()
                    .id(entity.getId())
                    .date(entity.getDate().toString())
                    .category(entity.getCategory().toString())
                    .subcategory(entity.getSubcategory())
                    .price(entity.getPrice())
                    .details(entity.getDetails())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public CalendarEntity getDetail(long dateStr, long id) {

        return calendarRepository.findByIdAndUserid(dateStr, id);
    }

    public CalendarEntity deleteCalendar(long dateStr, long id) {
        // 로그인 회원 + id 와 일치하는 튜플 있는지 검사
        CalendarEntity deleteThing = getDetail(dateStr, id);
        if(deleteThing != null) {
            // 삭제 작업
            calendarRepository.delete(deleteThing);

            return deleteThing;
        } else {
            return null;
        }
    }

    public CalendarEntity updateCalendar(CalendarDTO calendar, long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));
        CalendarEntity updateThing = CalendarEntity.builder()
                .id(calendar.getId())
                .date(Date.valueOf(calendar.getDate()))
                .category(CalendarEntity.Category.valueOf(calendar.getCategory()))
                .subcategory(calendar.getSubcategory())
                .price(calendar.getPrice())
                .details(calendar.getDetails())
                .user(user)
                .build();

        return calendarRepository.save(updateThing);
    }

    public SettingEntity getSetting(long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));

        SettingEntity setting = settingRepository.findByUserId(user.getId());

        return setting;
    }

    public SettingEntity updateSetting(SettingDTO settingDTO, final long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));
        SettingEntity settingCheck = settingRepository.findByUserId(user.getId());
        if (settingCheck.getId() != settingDTO.getId()) {
            return null;
        }

        SettingEntity settingEntity = SettingEntity.builder()
                .id(settingDTO.getId())
                .theme(settingDTO.getTheme())
                .Lone(settingDTO.getLone())
                .Ltwo(settingDTO.getLtwo())
                .Lthree(settingDTO.getLthree())
                .priceView(settingDTO.isPriceView())
                .user(user)
                .build();

        SettingEntity setting = settingRepository.save(settingEntity);

        return setting;
    }

    public ArrayList<CalenderStatsDTO> getMonthTotal(String dateStr, long id) {

        List<Object[]> results = calendarRepository.findMonthlyStats(dateStr, id);
        ArrayList<CalenderStatsDTO> dtos = new ArrayList<>();

        for (Object[] result : results) {
            String subcategory = (String) result[0];
            int totalPrice = ((Number) result[1]).intValue();
            double percentage = ((Number) result[2]).doubleValue();

            CalenderStatsDTO dto = CalenderStatsDTO.builder()
                    .subcategory(subcategory)
                    .totalPrice(totalPrice)
                    .percentage(percentage)
                    .build();
            dtos.add(dto);
        }


        return new ArrayList<>(dtos);
    }

    public NextBadgeDTO getNextBadge(long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));
        String dateStr = getCurrentYearMonth();

        List<Object[]> results = calendarRepository.findTotalMinusAndPlus(id, dateStr);
        if (results.isEmpty() || results.get(0) == null) {
            return NextBadgeDTO.builder()
                    .nextBadge("No Badge")
                    .totalPlus(0)
                    .totalMinus(0)
                    .stats(0.0)
                    .build();
        }

        Object[] result = results.get(0);
        int totalMinus = result[0] != null ? ((Number) result[0]).intValue() : 0;
        int totalPlus = result[1] != null ? ((Number) result[1]).intValue() : 0;
        double stats = totalPlus != 0 ? ((double) totalMinus / totalPlus) * 100 : 0;

        String nextBadge = determineNextBadge(stats);
        return NextBadgeDTO.builder()
                .nextBadge(nextBadge)
                .totalPlus(totalPlus)
                .totalMinus(totalMinus)
                .stats(stats)
                .build();
    }

    // format 함수
    public static String getCurrentYearMonth() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return LocalDate.now().format(formatter);
    }
    
    // badge 를 기준에 따라 부여하는 함수(변경 필요)
    private String determineNextBadge(double stats) {
        if (stats < 50) {
            return "One";
        } else if (stats < 75) {
            return "Two";
        } else {
            return "Three";
        }
    }
}
