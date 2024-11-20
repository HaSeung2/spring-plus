package org.example.expert.domain.user.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<User> users) {
        int batchSize = 100000;
        List<User> userList = new ArrayList<>();
        for(int i = 0; i < users.size(); i++) {
            userList.add(users.get(i));
            if(userList.size() == batchSize) {
                this.batchInsert(userList);
                userList.clear();
            }
        }
    }

    public void batchInsert(List<User> users){
        String sql = "INSERT INTO users"
            +"(created_at, modified_at, email, nickname, password, user_role)"
            + " VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(
                PreparedStatement ps,
                int i) throws SQLException {
                User user = users.get(i);
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getNickName());
                ps.setString(5, user.getPassword());
                ps.setString(6, user.getUserRole().toString());
            }

            @Override
            public int getBatchSize() {
                return users.size();
            }
        });
    }
}
