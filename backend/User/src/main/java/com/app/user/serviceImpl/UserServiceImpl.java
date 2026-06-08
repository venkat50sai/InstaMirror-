package com.app.user.serviceImpl;

import com.app.user.dto.*;
import com.app.utility.JwtUtil;
import com.app.user.entity.User;
import com.app.user.exception.UserException;
import com.app.user.repository.UserRepository;
import com.app.user.service.FollowService;
import com.app.user.service.PostService;
import com.app.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PostService postService;

    @Autowired
    private FollowService followService;

    // @Autowired
    // private WebClient.Builder webClientBuilder;

    @Override
    public Map<String, Object> validate(AccountDto userDto) throws UserException{
        Optional<User> optional = userRepo.findByEmail(userDto.getEmail());
        User user = optional.orElseThrow(()-> new UserException("User with email " + userDto.getEmail() + " does not exist."));
        if (!user.getPassword().equals(userDto.getPassword())) {
            throw new UserException("The password you entered is incorrect.");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login is Successful");
        response.put("token",token);
        return response;
    }

    @Override
    public Map<String, Object> createAccount(AccountDto userDto) throws UserException{
        if(userRepo.findByEmail(userDto.getEmail()).isPresent())
            throw new UserException("An account with this email already exists.");
        if(userRepo.findByUsername(userDto.getUsername()).isPresent())
            throw new UserException("An account with this username already exists.");
        User user = modelMapper.map(userDto,User.class);
        user.setRole(User.Role.User);
        // set the password in an encrypted way in db
        user = userRepo.save(user);
        System.out.println(user);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Account created Successfully");
        return response;
    }

    @Override
    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    @Override
    public UserDto getUserById(int userId, int id, boolean isDownload) throws UserException{
        Optional<User> optionalUser;
        UserDto userDto;
        if (isDownload) {
            Optional<AccountDto> optionalUserExport = userRepo.findUserForExport(id);
            if (optionalUserExport.isEmpty()) {
                throw new UserException("User not found for export with ID: " + id);
            }
            userDto = modelMapper.map(optionalUserExport.get(), UserDto.class);
        } else {
            optionalUser = userRepo.findById(id);
            if (optionalUser.isEmpty()) {
                throw new UserException("User not found with ID: " + id);
            }
            userDto = modelMapper.map(optionalUser.get(), UserDto.class);
        }
        Map<String, Object> follow = followService.getFollowFromFollowService(id);
        int followersCount = 0, followingCount = 0;
        if(follow != null) {
            followersCount = (Integer) follow.getOrDefault("followersCount", 0);
            followingCount = (Integer) follow.getOrDefault("followingsCount",0);
        }
        userDto.setFollowings(followersCount);
        userDto.setFollowers(followingCount);
        userDto.setSelf(userId == id);
        userDto.setFollowStatus(userId == id ? null : followService.checkIsFollowing(userId, id) != null);
        Map<String, Object> post = postService.getCountfromPostService(id);
        int count = 0;
        if (post != null && post.get("count") instanceof Integer) {
            count = (Integer) post.get("count");
        }
        userDto.setPosts(count);
        return userDto;
    }

    @Override
    public void UpdateUserDetails(int id, UserDto userDto, MultipartFile imageFile) throws UserException, IOException {
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new UserException("Account doesn't exist."));
        if (imageFile != null && !imageFile.isEmpty()) {
            userDto.setImage(imageFile.getBytes());
        } else {
            userDto.setImage(existingUser.getImage());
        }
        userRepo.findByEmail(userDto.getEmail()).ifPresent(userWithEmail -> {
            if (userWithEmail.getId() != id) {
                throw new UserException("An account with this email already exists.");
            }
        });
        userRepo.findByUsername(userDto.getUsername()).ifPresent(userWithUsername -> {
            if (userWithUsername.getId() != id) {
                throw new UserException("An account with this username already exists.");
            }
        });
        userRepo.findByPhone(userDto.getPhone()).ifPresent(userWithPhone -> {
            if (userWithPhone.getId() != id) {
                throw new UserException("An account with this phone number already exists.");
            }
        });
        modelMapper.typeMap(UserDto.class, User.class).addMappings(mapper -> {
            mapper.skip(User::setId);
        });
        modelMapper.map(userDto, existingUser);
        userRepo.save(existingUser);
    }

    @Override
    public void deleteAccount(){

    }

    @Override
    public void deleteById(int id){
        userRepo.deleteById(id);
    }

    @Override
    public int getCount() {
        return (int) userRepo.countByRole(User.Role.User);
    }

    @Override
    public List<SearchDto> findUserByIds(Set<Integer> userIds) {
        List<User> userList = userRepo.findAllById(userIds);
        List<SearchDto> List = new ArrayList<>();
        for (User user : userList) {
            SearchDto dto = modelMapper.map(user, SearchDto.class);
            List.add(dto);
        }
        return List;
    }

    @Override
    public List<SearchDto> searchByName(String username){
        List<User> userList = userRepo.findByUsernameContainingIgnoreCase(username);
        List<SearchDto> List = new ArrayList<>();
        for (User user : userList) {
            SearchDto dto = modelMapper.map(user, SearchDto.class);
            List.add(dto);
        }
        return List;
    }

    public Page<SearchDto> findAllUsers(Pageable pageable) {
        Page<User> users = userRepo.findAll(pageable);
        return users.map(u -> new SearchDto(u.getId(), u.getUsername(), u.getFullname(), u.getImage()));
    }
}
