package com.app.user.service;

import com.app.user.dto.AccountDto;
import com.app.user.dto.SearchDto;
import com.app.user.dto.UserDto;
import com.app.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {

    Map<String, Object> validate(AccountDto userDto) throws Exception;
    Map<String, Object> createAccount(AccountDto userDto) throws Exception;
    List<User> getAllUsers();
    UserDto getUserById(int userId, int id, boolean download) throws  Exception;
    void UpdateUserDetails(int id,UserDto userDto, MultipartFile imageFile) throws Exception;
    void deleteAccount();
    void deleteById(int id);
    int getCount();
    List<SearchDto> findUserByIds (Set<Integer> userIds);
    List<SearchDto> searchByName(String username);
    Page<SearchDto> findAllUsers(Pageable pageable);
}
