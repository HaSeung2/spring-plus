package org.example.expert.domain.user.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.aws.AwsS3Service;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(),
                                user.getEmail(),
                                user.getNickName());
    }

    @Transactional
    public void changePassword(
        long userId,
        UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(),
                                    user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(),
                                     user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8
            || !userChangePasswordRequest.getNewPassword().matches(".*\\d.*")
            || !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    public List<UserResponse> searchByNickName(String nickName) {
        return userRepository.findByNickName(nickName).stream().map(UserResponse::new).toList();
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserResponse::new).toList();
    }

    @Transactional
    public void uploadImage(String fileName, User user) {
        Optional.ofNullable(user.getFileName()).ifPresent(awsS3Service::deleteFile);
        user.uploadImage(fileName);
        userRepository.save(user);
    }

    @Transactional
    public void deleteFile(String fileName, User user) {
        Optional.ofNullable(user.getFileName()).ifPresent(file-> {
           if(!fileName.equals(file)){
               throw new InvalidRequestException("본인 프로필 이미지가 아닙니다.");
           }
            user.uploadImage(null);
            awsS3Service.deleteFile(file);
            userRepository.save(user);
        });
    }
}
