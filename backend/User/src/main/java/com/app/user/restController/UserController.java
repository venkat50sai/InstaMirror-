package com.app.user.restController;

import com.app.user.dto.AccountDto;
import com.app.user.dto.SearchDto;
import com.app.user.dto.UserDto;
import com.app.user.entity.User;
import com.app.user.service.UserService;
import com.app.user.util.ExportHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user")
//@CrossOrigin(origins="http://localhost:4200")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private ExportHelper exportHelper;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> validateUser(@RequestBody AccountDto userDto) throws Exception {
        Map<String, Object> response = userService.validate(userDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody AccountDto userDto) throws Exception{
        Map<String, Object> response = userService.createAccount(userDto);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> userList = userService.getAllUsers();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/getById")
    public ResponseEntity<UserDto> getUserById(@RequestHeader("X-User-Id") Integer userId, @RequestParam("id") int id) throws Exception{
        UserDto user = userService.getUserById(userId, id, false);
        return  new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(value="/updateUser")
    public ResponseEntity<Map<String, Object>> updateUserDetails(@RequestHeader("X-User-Id") Integer id, @ModelAttribute UserDto userDto, @RequestParam(value = "img", required = false) MultipartFile imageFile) throws Exception{
        userService.UpdateUserDetails(id, userDto, imageFile);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User is updated Successfully");
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestParam("id") int id) throws Exception{
        userService.deleteById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User with id: " + id + " deleted successfully");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<SearchDto>> fetchUsers(@RequestBody Set<Integer> userIds) throws Exception{
        List<SearchDto> userDtoList = userService.findUserByIds(userIds);
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchDto>> Search(@RequestParam("username") String name) throws Exception{
        List<SearchDto> userList = userService.searchByName(name);
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<SearchDto>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<SearchDto> usersPage = userService.findAllUsers(PageRequest.of(page, size, sort));
        return new ResponseEntity<>(usersPage, HttpStatus.OK);
    }

    @GetMapping("/user-Count")
    public ResponseEntity<Map<String, Object>> Count() throws Exception{
        Map<String , Object> m = new HashMap<>();
        m.put("count", userService.getCount());
        return new ResponseEntity<>(m, HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> getFile(@RequestHeader("X-User-Id") Integer userId, @RequestParam("id") int id, @RequestParam("format") String format) throws Exception{
        System.out.println("request came for "+ format);
        UserDto details = userService.getUserById(userId, id, !"zip".equalsIgnoreCase(format));
        byte[] fileBytes;
        String fileName;
        String contentType;
        switch (format.toLowerCase()) {
            case "json":
                fileBytes = exportHelper.createJson(details);
                fileName = "userJson.json";
                contentType = "application/json";
                break;
            case "excel":
                fileBytes = exportHelper.createExcel(details);
                fileName = "userExcel.xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                break;
            case "zip":
                fileBytes = exportHelper.createZip(details);
                fileName = "user_profile.zip";
                contentType = "application/zip";
                break;
            default:
                throw new IllegalArgumentException("Invalid format: " + format);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

}
