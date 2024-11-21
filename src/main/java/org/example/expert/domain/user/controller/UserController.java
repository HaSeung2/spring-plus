package org.example.expert.domain.user.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.aws.AwsS3Service;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AwsS3Service awsS3Service;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@Auth User authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponse>> searchUserByNickName(@RequestParam String nickName) {
        return ResponseEntity.ok(userService.searchByNickName(nickName));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping("/users/image")
    public ResponseEntity<Void> uploadImage(@Auth User user, @RequestPart MultipartFile file) {
        userService.uploadImage(awsS3Service.uploadImage(file), user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/image")
    public ResponseEntity<Void> deleteImage(@Auth User user, @RequestParam String fileName) {
        userService.deleteFile(fileName, user);
        return ResponseEntity.ok().build();
    }
}
