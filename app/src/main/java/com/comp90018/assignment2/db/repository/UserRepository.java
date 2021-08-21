package com.comp90018.assignment2.db.repository;

import com.comp90018.assignment2.dto.UserDTO;

import java.util.List;

/**
 * Basic atomic operations on database objects。
 * 主要是被service层调用
 */
public class UserRepository {

    /*单例的一堆代码*/

    private static UserRepository instance;

    private UserRepository() {
        // protect the constructor
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }

        return instance;
    }



    // 增-删-改-查等基本原子操作
    /**
     * find userDTO from db with it's id
     * @param userId user's auto generated id
     * @return
     */
    public UserDTO findUserById(String userId) {
        return null;
    }

    /**
     *
     * @param nickname user's nick name
     * @return List of users whose nickname matches query
     */
    public List<UserDTO> findUsersByNickname(String nickname) {
        return null;
    }

    /**
     *
     * @return
     */
    public List<UserDTO> findAllUsers() {
        return null;
    }

    /**
     *
     * @param userDTO
     */
    public void addNewUser(UserDTO userDTO) {

    }

    /**
     *
     * @param userId
     * @param newPassword
     */
    public void updateUserPassword(String userId, String newPassword) {

    }
}
