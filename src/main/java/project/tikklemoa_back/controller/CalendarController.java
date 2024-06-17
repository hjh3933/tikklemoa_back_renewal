package project.tikklemoa_back.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.tikklemoa_back.dto.*;
import project.tikklemoa_back.entity.CalendarEntity;
import project.tikklemoa_back.entity.SettingEntity;
import project.tikklemoa_back.service.CalendarService;
import project.tikklemoa_back.service.UserService;

import java.sql.Date;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api-server")
public class CalendarController {
    final private UserService userService;
    final private CalendarService calendarService;

    @Autowired
    public CalendarController(final UserService userService
            , final CalendarService calendarService) {
        this.userService = userService;
        this.calendarService = calendarService;
    }

    // calender 추가
    @PostMapping("/insertCalendar")
    public ResponseEntity<?> insertCalendar(@RequestBody CalendarDTO calendarDTO,@AuthenticationPrincipal String userid) {
        try{
            long id = Long.parseLong(userid);
            CalendarEntity calendar = calendarService.insertCalendar(calendarDTO,id);

            CalendarDTO responseCalendarDTO = CalendarDTO.builder()
                    .result(true)
                    .msg("내역 추가가 완료되었습니다")
                    .build();

            return ResponseEntity.ok().body(responseCalendarDTO);
        }
        catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }

    }

    // 한 달 전체 내역 조회
    @GetMapping("/getMonth/{dateStr}")
    public ResponseEntity<?> getMonth(@PathVariable String dateStr,@AuthenticationPrincipal String userid) {
        try{
        // dateStr -> yyyy-mm
        long id = Long.parseLong(userid);
        ArrayList<MonthCalendarDTO> monthCalendars = calendarService.getMonth(dateStr, id);

        return ResponseEntity.ok().body(monthCalendars);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }

    }

    // 특정 날짜 내역 조회
    @GetMapping("/getDate/{dateStr}")
    public ResponseEntity<?> getDate(@PathVariable String dateStr,@AuthenticationPrincipal String userid) {
        try{
            // dateStr -> yyyy-mm-dd
            long id = Long.parseLong(userid);
            ArrayList<CalendarDTO> dateCalendars = calendarService.getDate(dateStr, id);

            return ResponseEntity.ok().body(dateCalendars);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
    
    // 상세 내역 조회 - front 에서 처리할까 고민, 일단 만들어 두기
    @GetMapping("/getDetail/{calendarId}")
    public ResponseEntity<?> getDetail(@PathVariable String calendarId,@AuthenticationPrincipal String userid) {
        try{
            long id = Long.parseLong(userid);
            long calendarid = Long.parseLong(calendarId);
            CalendarEntity calendar = calendarService.getDetail(calendarid, id);
            CalendarDTO calendarDTO = CalendarDTO.builder()
                    .id(calendar.getId())
                    .category(String.valueOf(calendar.getCategory()))
                    .subcategory(calendar.getSubcategory())
                    .date(String.valueOf(calendar.getDate()))
                    .price(calendar.getPrice())
                    .details(calendar.getDetails())
                    .build();

            return ResponseEntity.ok().body(calendarDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 캘린더 내역 삭제
    @DeleteMapping("/deleteCalendar")
    public ResponseEntity<?> deleteCalendar(@RequestBody CalendarDTO calendar, @AuthenticationPrincipal String userid) {
        try{
            long id = Long.parseLong(userid);
            CalendarEntity calendarEntity = calendarService.deleteCalendar(calendar.getId(), id);

            CalendarDTO responseCalendarDto;

            if(calendarEntity != null) {
                responseCalendarDto = CalendarDTO.builder()
                        .result(true)
                        .msg("내역 삭제가 완료되었습니다")
                        .build();
            } else {
                responseCalendarDto = CalendarDTO.builder()
                        .result(false)
                        .msg("존재하지 않는 내역입니다.")
                        .build();
            }

            return ResponseEntity.ok().body(responseCalendarDto);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 캘린더 내역 수정
    @PatchMapping("/updateCalendar")
    public ResponseEntity<?> updateCalendar(@RequestBody CalendarDTO calendar, @AuthenticationPrincipal String userid) {
        try{
            long id = Long.parseLong(userid);
            CalendarEntity calendarEntity = calendarService.updateCalendar(calendar, id);
            CalendarDTO updateCalender = CalendarDTO.builder()
                    .id(calendarEntity.getId())
                    .date(String.valueOf(calendarEntity.getDate()))
                    .category(String.valueOf(calendarEntity.getCategory()))
                    .subcategory(calendarEntity.getSubcategory())
                    .price(calendarEntity.getPrice())
                    .details(calendarEntity.getDetails())
                    .result(true)
                    .msg("내역 수정이 완료되었습니다")
                    .build();

            return ResponseEntity.ok().body(updateCalender);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 설정 기능 조회
    @GetMapping("/getSetting")
    public ResponseEntity<?> getSetting(@AuthenticationPrincipal String userid) {
        try{
            long id = Long.parseLong(userid);
            SettingEntity settingEntity = calendarService.getSetting(id);
            SettingDTO setting = SettingDTO.builder()
                    .id(settingEntity.getId())
                    .theme(settingEntity.getTheme())
                    .Lone(settingEntity.getLone())
                    .Ltwo(settingEntity.getLtwo())
                    .Lthree(settingEntity.getLthree())
                    .priceView(settingEntity.isPriceView())
                    .build();

            return ResponseEntity.ok().body(setting);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 설정 기능 수정
    @PatchMapping("/updateSetting")
    public ResponseEntity<?> updateSetting(@RequestBody SettingDTO settingDTO ,@AuthenticationPrincipal String userid) {
        try{
            final long id = Long.parseLong(userid);
            SettingEntity settingEntity = calendarService.updateSetting(settingDTO, id);
            SettingDTO setting;
            
            if(settingEntity != null) {
                setting= SettingDTO.builder()
                        .id(settingEntity.getId())
                        .theme(settingEntity.getTheme())
                        .Lone(settingEntity.getLone())
                        .Ltwo(settingEntity.getLtwo())
                        .Lthree(settingEntity.getLthree())
                        .priceView(settingEntity.isPriceView())
                        .result(true)
                        .msg("설정 변경이 완료되었습니다")
                        .build();
            } else  {
                setting= SettingDTO.builder()
                        .result(false)
                        .msg("잘못된 접근입니다")
                        .build();
            }

                return ResponseEntity.ok().body(setting);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 월별 통계
    @GetMapping("/getMonthTotal/{dateStr}")
    public ResponseEntity<?> getMonthTotal(@PathVariable String dateStr, @AuthenticationPrincipal String userid) {
        try{
            long id = Long.parseLong(userid);
            ArrayList<CalenderStatsDTO> calenderStatsDTOS = calendarService.getMonthTotal(dateStr, id);

            return ResponseEntity.ok().body(calenderStatsDTOS);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 다음 달 badge 예상
    @GetMapping("/getNextBadge/{dateStr}")
    public ResponseEntity<?> getNextBadge(@PathVariable String dateStr, @AuthenticationPrincipal String userid) {
        try{
            long id = Long.parseLong(userid);
            NextBadgeDTO nextBadgeDTO = calendarService.getNextBadge(dateStr, id);

            return ResponseEntity.ok().body(nextBadgeDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

}
