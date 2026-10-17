package com.codegym.dao;

import com.codegym.model.User;

import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {

    // Thêm người dùng bằng PreparedStatement
    void insertUser(User user) throws SQLException;

    // Lấy người dùng theo ID
    User selectUser(int id);

    // Lấy danh sách người dùng
    List<User> selectAllUsers();

    // Xóa người dùng
    boolean deleteUser(int id) throws SQLException;

    // Cập nhật người dùng
    boolean updateUser(User user) throws SQLException;

    // Stored Procedures
    User getUserById(int id);

    void insertUserStore(User user) throws SQLException;

    // Tìm kiếm theo quốc gia
    List<User> searchByCountry(String country) throws SQLException;

    // Sắp xếp theo tên
    List<User> sortByName() throws SQLException;

    // JDBC Transaction: thêm User và gán Permission
    void addUserTransaction(User user, int[] permissionIds)
            throws SQLException;

    // Thực thi SQL không sử dụng Transaction
    void insertUpdateWithoutTransaction() throws SQLException;
}
