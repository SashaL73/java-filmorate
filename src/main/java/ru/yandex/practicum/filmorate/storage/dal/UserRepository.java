package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.UserRowMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("userRepository")
public class UserRepository extends BaseRepository<User> implements UserStorage {
    @Autowired
    UserRowMapper userRowMapper;
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_USER_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String UPDATE_USER_QUERY = "UPDATE USERS SET EMAIL= ?, LOGIN= ?, NAME= ?, BIRTHDAY= ? WHERE ID = ? ";
    private static final String FIND_FRIENDS_QUERY = "SELECT * FROM users " +
            "WHERE id IN (SELECT friend_id FROM friendships WHERE user_id = ?)"; //AND status = 'CONFIRMED')";
    private static final String FIND_COMMON_FRIEND_QUERY = "SELECT * FROM users " +
            "WHERE id IN ( SELECT f1.friend_id FROM friendships f1 JOIN friendships f2 ON f1.friend_id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ? )";
    private static final String INSERT_USER_QUERY = "INSERT INTO USERS (email, login, name, birthday)" +
            " VALUES (?, ?, ?, ?)";



    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public List<User> findAll() {
        System.out.println(1);
        return findMany(FIND_ALL_USERS_QUERY, userRowMapper);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findOne(FIND_USER_BY_EMAIL_QUERY, email);
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return findOne(FIND_USER_BY_ID_QUERY, userId);
    }

    @Override
    public List<User> findCommonFriends(long userId1, long userId2) {
        return findMany(FIND_COMMON_FRIEND_QUERY, userRowMapper, userId1, userId2);
    }

    @Override
    public List<User> findFriends(long userId) {
        return findMany(FIND_FRIENDS_QUERY, userRowMapper, userId);
    }

    @Override
    public User saveUser(User user) {
        LocalDate localDate = user.getBirthday();
        Date sqlDate = Date.valueOf(localDate);
        try {


            long id = insert(
                    INSERT_USER_QUERY,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    sqlDate
            );
            user.setId(id);
            return user;
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User updateUser(User user) {
        update(
                UPDATE_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }
}
