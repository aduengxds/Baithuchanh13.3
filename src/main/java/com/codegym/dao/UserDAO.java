
package com.codegym.dao;

import com.codegym.model.User;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {

    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/demo"
            + "?useSSL=false&serverTimezone=UTC"
            + "&allowPublicKeyRetrieval=true";

    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "password";

    private static final String SELECT_ALL_USERS =
            "SELECT id, name, email, country FROM users";

    private static final String SELECT_USER_BY_ID =
            "SELECT id, name, email, country FROM users WHERE id = ?";

    private static final String INSERT_USER =
            "INSERT INTO users (name, email, country) VALUES (?, ?, ?)";

    private static final String UPDATE_USER =
            "UPDATE users SET name = ?, email = ?, country = ? WHERE id = ?";

    private static final String DELETE_USER =
            "DELETE FROM users WHERE id = ?";

    // SQL cho bài 13.3: thực thi không sử dụng Transaction
    private static final String SQL_INSERT =
            "INSERT INTO Employee (name, salary, created_Date) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE Employee SET salary = ? WHERE name = ?";

    private static final String SQL_TABLE_CREATE =
            "CREATE TABLE Employee ("
            + "id INT AUTO_INCREMENT,"
            + "name VARCHAR(100) NOT NULL,"
            + "salary DECIMAL(15, 2) NOT NULL,"
            + "created_Date TIMESTAMP,"
            + "PRIMARY KEY (id)"
            + ")";

    private static final String SQL_TABLE_DROP =
            "DROP TABLE IF EXISTS Employee";

    // Tạo kết nối đến MySQL
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                JDBC_URL,
                JDBC_USERNAME,
                JDBC_PASSWORD
        );
    }

    // Lấy toàn bộ người dùng
    @Override
    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi khi lấy danh sách User", e
            );
        }

        return users;
    }

    // Lấy người dùng theo ID bằng PreparedStatement
    @Override
    public User selectUser(int id) {
        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_USER_BY_ID)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi khi lấy User theo ID: " + id, e
            );
        }

        return null;
    }

    // Lấy người dùng theo ID bằng Stored Procedure
    @Override
    public User getUserById(int id) {
        String sql = "{CALL get_user_by_id(?)}";

        try (Connection connection = getConnection();
             CallableStatement statement =
                     connection.prepareCall(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            id,
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("country")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi khi lấy User theo ID: " + id, e
            );
        }

        return null;
    }

    // Thêm người dùng bằng PreparedStatement
    @Override
    public void insertUser(User user) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(INSERT_USER)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());

            statement.executeUpdate();
        }
    }

    // Thêm người dùng bằng Stored Procedure
    @Override
    public void insertUserStore(User user) throws SQLException {
        String sql = "{CALL insert_user(?, ?, ?)}";

        try (Connection connection = getConnection();
             CallableStatement statement =
                     connection.prepareCall(sql)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());

            statement.executeUpdate();
        }
    }

    // Cập nhật người dùng
    @Override
    public boolean updateUser(User user) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(UPDATE_USER)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());
            statement.setInt(4, user.getId());

            return statement.executeUpdate() > 0;
        }
    }

    // Xóa người dùng
    @Override
    public boolean deleteUser(int id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(DELETE_USER)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        }
    }

    // Tìm kiếm người dùng theo quốc gia
    @Override
    public List<User> searchByCountry(String country)
            throws SQLException {

        List<User> users = new ArrayList<>();
        String sql = SELECT_ALL_USERS
                + " WHERE country LIKE ? ORDER BY id";

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, "%" + country + "%");

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        }

        return users;
    }

    // Sắp xếp người dùng theo tên A-Z
    @Override
    public List<User> sortByName() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = SELECT_ALL_USERS + " ORDER BY name ASC";

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }

        return users;
    }

    // Bài 13.3: Thực thi SQL không sử dụng Transaction
    @Override
    public void insertUpdateWithoutTransaction()
            throws SQLException {

        try (Connection conn = getConnection();
             Statement statement = conn.createStatement()) {

            // Không tắt auto-commit.
            // Mỗi câu lệnh SQL thành công được commit riêng.
            System.out.println("Auto-commit: " + conn.getAutoCommit());

            // Xóa bảng cũ và tạo bảng mới
            statement.execute(SQL_TABLE_DROP);
            statement.execute(SQL_TABLE_CREATE);

            // INSERT nhân viên thứ nhất
            try (PreparedStatement psInsert =
                         conn.prepareStatement(SQL_INSERT)) {

                psInsert.setString(1, "Quynh");
                psInsert.setBigDecimal(
                        2, new BigDecimal("10.00"));
                psInsert.setTimestamp(
                        3, Timestamp.valueOf(LocalDateTime.now()));

                psInsert.executeUpdate();

                System.out.println("Đã INSERT nhân viên Quynh.");

                // INSERT nhân viên thứ hai
                psInsert.setString(1, "Ngan");
                psInsert.setBigDecimal(
                        2, new BigDecimal("20.00"));
                psInsert.setTimestamp(
                        3, Timestamp.valueOf(LocalDateTime.now()));

                psInsert.executeUpdate();

                System.out.println("Đã INSERT nhân viên Ngan.");
            }

            // Cố tình làm UPDATE thất bại
            try (PreparedStatement psUpdate =
                         conn.prepareStatement(SQL_UPDATE)) {

                // Lỗi chủ đích: tham số 1 (salary) chưa được gán.
                psUpdate.setBigDecimal(
                        2, new BigDecimal("999.99"));
                psUpdate.setString(2, "Quynh");

                // Dự kiến SQLException xảy ra tại đây
                psUpdate.executeUpdate();

            } catch (SQLException e) {
                System.out.println(
                        "UPDATE thất bại như dự kiến do thiếu tham số."
                );
                e.printStackTrace();
            }

            // Hai INSERT đã commit không bị rollback
            System.out.println(
                    "Kiểm thử hoàn tất. Hãy kiểm tra bảng Employee."
            );
        }
    }

    // Bài 13.2: Thêm User và gán Permission trong cùng Transaction
    @Override
    public void addUserTransaction(
            User user,
            int[] permissionIds
    ) throws SQLException {

        Connection connection = null;
        boolean originalAutoCommit = true;

        try {
            connection = getConnection();
            originalAutoCommit = connection.getAutoCommit();

            // Bắt đầu Transaction
            connection.setAutoCommit(false);

            int userId;

            // 1. Thêm User và lấy ID tự sinh
            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 INSERT_USER,
                                 Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, user.getName());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getCountry());

                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException(
                            "Thêm User thất bại, không có dòng nào được ghi."
                    );
                }

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException(
                                "Không lấy được ID của User vừa tạo."
                        );
                    }

                    userId = rs.getInt(1);
                }
            }

            // 2. Gán các quyền cho User
            if (permissionIds != null && permissionIds.length > 0) {
                String insertPermissionSql =
                        "INSERT INTO user_permission "
                        + "(user_id, permission_id) VALUES (?, ?)";

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     insertPermissionSql)) {

                    for (int permissionId : permissionIds) {
                        statement.setInt(1, userId);
                        statement.setInt(2, permissionId);
                        statement.executeUpdate();
                    }
                }
            }

            // 3. Tất cả thành công thì commit
            connection.commit();

        } catch (SQLException e) {
            // Có lỗi thì rollback toàn bộ Transaction
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }

            throw e;

        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(originalAutoCommit);
                } catch (SQLException e) {
                    System.err.println(
                            "Không thể khôi phục auto-commit: "
                            + e.getMessage()
                    );
                }

                connection.close();
            }
        }
    }

    // Chuyển một dòng ResultSet thành đối tượng User
    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("country")
        );
    }
}
