package com.comp90018.assignment2.db.service;

import com.comp90018.assignment2.db.repository.UserRepository;
import com.comp90018.assignment2.dto.UserDTO;

import java.util.List;

/**
 * some services that is used for user model。
 * 直接被应用层调用
 * @author Xiaotian
 */
public class UserService {
    private static UserService instance;

    private UserRepository userRepository;

    private UserService() {
        userRepository = UserRepository.getInstance();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }

        return instance;
    }

    /**
     *
     * @param followerId
     * @param toFollowId
     */
    @Deprecated
    public void userFollowAnotherUserById(String followerId, String toFollowId) {
        // 给自己的添加following
        // 给对方的添加follower
    }

    /**
     *
     * @param loginId
     * @param plainPassword
     */
    @Deprecated
    public void userLogin(String loginId, String plainPassword) {
        ;
    }

    /**
     *
     * @param userId
     * @return
     */
    @Deprecated
    public List<UserDTO> getFollowersFromUserId(String userId) {
        // 可能只会直接复用repository里的一个方法
        return null;
    }
}
