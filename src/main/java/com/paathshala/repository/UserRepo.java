package com.paathshala.repository;

import com.paathshala.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User,Integer> {

    Optional<User> findByUsername(String username);
    User findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username , String email);

    boolean existsByUsername(String username);

    // All students (non-admin users)
    @Query("SELECT u FROM User u WHERE u.role ='STUDENT'")
    List<User> findAllStudents();

    // Students with zero enrollments (truly basic — never enrolled)
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.username NOT IN " +
            "(SELECT DISTINCT e.user.username FROM Enrollment e)")
    List<User> findStudentsWithNoEnrollments();
}
