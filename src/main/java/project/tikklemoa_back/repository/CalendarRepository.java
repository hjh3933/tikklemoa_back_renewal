package project.tikklemoa_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.tikklemoa_back.entity.CalendarEntity;

import java.util.List;

public interface CalendarRepository extends JpaRepository<CalendarEntity, Long> {
    @Query(value = "SELECT c.date, " +
            "SUM(CASE WHEN c.category = 'MINUS' THEN c.price ELSE 0 END) as totalMinus, " +
            "SUM(CASE WHEN c.category = 'PLUS' THEN c.price ELSE 0 END) as totalPlus, " +
            "(SUM(CASE WHEN c.category = 'MINUS' THEN c.price ELSE 0 END) " +
            "- SUM(CASE WHEN c.category = 'PLUS' THEN c.price ELSE 0 END)) as dateTotal " +
            "FROM calendar c " +
            "WHERE c.date BETWEEN :startOfMonth AND :endOfMonth AND c.userid = :userid " +
            "GROUP BY c.date", nativeQuery = true)
    List<Object[]> findByYearAndMonthAndUserIdGroupedByDate(@Param("startOfMonth") String startOfMonth, @Param("endOfMonth") String endOfMonth, @Param("userid") Long userId);

    @Query(value = "SELECT * FROM calendar c WHERE c.date BETWEEN :startOfDay AND :endOfDay AND c.userid = :userid", nativeQuery = true)
    List<CalendarEntity> findByDateAndUserId(@Param("startOfDay") String startOfDay, @Param("endOfDay") String endOfDay, @Param("userid") Long userId);

    @Query(value = "SELECT * FROM calendar c WHERE c.id = :id AND c.userid = :userid", nativeQuery = true)
    CalendarEntity findByIdAndUserid(@Param("id") Long id, @Param("userid") Long userId);

    @Query(value = "SELECT c.subcategory, " +
            "SUM(c.price) as totalPrice, " +
            "ROUND(SUM(c.price) * 100.0 / " +
            "(SELECT SUM(c2.price) FROM calendar c2 WHERE c2.category = 'MINUS' AND c2.userid = :userid AND DATE_FORMAT(c2.date, '%Y-%m') = :dateStr), 2) as percentage " +
            "FROM calendar c " +
            "WHERE c.category = 'MINUS' AND c.userid = :userid AND c.date BETWEEN :startOfMonth AND :endOfMonth " +
            "GROUP BY c.subcategory", nativeQuery = true)
    List<Object[]> findMonthlyStats(@Param("startOfMonth") String startOfMonth, @Param("endOfMonth") String endOfMonth, @Param("userid") Long userId);

    @Query(value = "SELECT " +
            "SUM(CASE WHEN c.category = 'MINUS' THEN c.price ELSE 0 END) as totalMinus, " +
            "SUM(CASE WHEN c.category = 'PLUS' THEN c.price ELSE 0 END) as totalPlus " +
            "FROM calendar c " +
            "WHERE c.userid = :userid AND c.date BETWEEN :startOfMonth AND :endOfMonth", nativeQuery = true)
    List<Object[]> findTotalMinusAndPlus(@Param("userid") Long userId, @Param("startOfMonth") String startOfMonth, @Param("endOfMonth") String endOfMonth);


}
