# User Management

Ứng dụng quản lý người dùng sử dụng JSP, Servlet, JDBC và MySQL.

## Công nghệ

- Java 17
- Maven
- Jakarta Servlet 6
- JSP và JSTL
- MySQL 8
- Apache Tomcat 10.1+

## Chức năng

- Hiển thị danh sách người dùng
- Thêm người dùng
- Sửa thông tin người dùng
- Xóa người dùng

## Cơ sở dữ liệu

Tạo database `demo` và bảng `users` trong MySQL.

```sql
CREATE DATABASE IF NOT EXISTS demo;
USE demo;

CREATE TABLE IF NOT EXISTS users (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(220) NOT NULL,
    country VARCHAR(120),
    PRIMARY KEY (id)
);

INSERT INTO users (name, email, country)
VALUES
('Minh', 'minh@codegym.vn', 'Viet Nam'),
('Kante', 'kante@che.uk', 'Kenia');