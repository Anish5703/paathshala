package com.paathshala.repository;

import com.paathshala.entity.Course;
import com.paathshala.entity.Enrollment;
import com.paathshala.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepo extends JpaRepository<Enrollment,Integer> {

    List<Enrollment> findByUser(User user);
    Enrollment findByUserAndCourse(User user, Course course);
    List<Enrollment> findByCourse(Course course);

    // Corrected exists query
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Enrollment e WHERE e.user.username = :username AND e.course.title = :courseTitle")
    boolean existsByUserAndCourseTitle(@Param("username") String username, @Param("courseTitle") String courseTitle);

    // ── Active students (distinct users) ──
    @Query("SELECT COUNT(DISTINCT e.user.id) FROM Enrollment e")
    long countActiveStudents();

    // ── Recent enrollments ──
    @Query("SELECT e FROM Enrollment e ORDER BY e.createdTime DESC")
    List<Enrollment> findRecentEnrollments(Pageable pageable);

    // ── Per-course enrollment count ──
    @Query("""
        SELECT e.course.title, COUNT(e)
        FROM Enrollment e
        GROUP BY e.course.title
        ORDER BY COUNT(e) DESC
    """)
    List<Object[]> countEnrollmentsByCourse();

    // ── Student enrollments ──
    List<Enrollment> findByUserUsername(String username);
    List<Enrollment> findByUserUsernameAndPaid(String username, boolean paid);

    int countByUserUsername(String username);
    int countByUserUsernameAndPaid(String username, boolean paid);

    // ── Revenue queries ──
    @Query("""
        SELECT COALESCE(SUM(e.course.price), 0)
        FROM Enrollment e
        WHERE e.paid = true
    """)
    double getTotalRevenue();

    @Query(value = """
        SELECT COALESCE(SUM(c.price), 0)
        FROM enrollment_tbl e
        JOIN course c ON e.course_id = c.id
        WHERE e.paid = true
        AND DATE_TRUNC('month', e.created_time) =
            DATE_TRUNC('month', CURRENT_TIMESTAMP)
    """, nativeQuery = true)
    double getThisMonthRevenue();

    @Query(value = """
        SELECT COALESCE(SUM(c.price), 0)
        FROM enrollment_tbl e
        JOIN course c ON e.course_id = c.id
        WHERE e.paid = true
        AND DATE_TRUNC('month', e.created_time) =
            DATE_TRUNC('month', CURRENT_TIMESTAMP - INTERVAL '1 month')
    """, nativeQuery = true)
    double getLastMonthRevenue();

    @Query(value = """
        SELECT TO_CHAR(e.created_time, 'YYYY-MM') AS month,
               COALESCE(SUM(c.price), 0) AS amount
        FROM enrollment_tbl e
        JOIN course c ON e.course_id = c.id
        WHERE e.paid = true
        AND e.created_time >= NOW() - INTERVAL '6 months'
        GROUP BY month
        ORDER BY month
    """, nativeQuery = true)
    List<Object[]> getMonthlyRevenue();

    // ── Premium / Basic usernames ──
    @Query("SELECT DISTINCT e.user.username FROM Enrollment e WHERE e.paid = true")
    List<String> findPremiumUsernames();

    @Query("SELECT DISTINCT e.user.username FROM Enrollment e WHERE e.user.username NOT IN " +
            "(SELECT DISTINCT e2.user.username FROM Enrollment e2 WHERE e2.paid = true)")
    List<String> findBasicEnrolledUsernames();
}