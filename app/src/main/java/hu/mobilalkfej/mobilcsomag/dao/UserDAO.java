package hu.mobilalkfej.mobilcsomag.dao;

import hu.mobilalkfej.mobilcsomag.models.User;

public interface UserDAO {
    boolean registerUser(User user, String password, FirestoreCallback firestoreCallback);
    boolean loginUser(String email, String password, FirestoreCallback firestoreCallback);
    boolean logoutUser(User user);

    boolean updateUser(User user, String password);

    boolean deleteUser(User user, FirestoreCallback firestoreCallback);
}
