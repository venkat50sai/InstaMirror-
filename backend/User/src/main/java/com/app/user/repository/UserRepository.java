package com.app.user.repository;

import com.app.user.dto.AccountDto;
import com.app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    public Optional<User> findByEmail(String email);
    public Optional<User> findByPhone(String phone);
    public Optional<User> findByUsername(String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
    @Query("select new com.app.user.dto.AccountDto(u.id, u.username, u.password, u.email, u.fullname, u.phone, u.dob, u.bio) from User u where u.id = :userId")
    Optional<AccountDto> findUserForExport (@Param("userId") int userId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") User.Role role);
}
