
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý người dùng</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        h1 {
            margin-bottom: 20px;
        }

        form {
            margin-top: 20px;
        }

        input[type="text"] {
            padding: 8px;
            width: 220px;
        }

        button {
            padding: 8px 12px;
            cursor: pointer;
        }

        a {
            margin-right: 8px;
        }

        table {
            border-collapse: collapse;
            width: 100%;
            margin-top: 20px;
        }

        th, td {
            border: 1px solid #ccc;
            padding: 10px;
            text-align: left;
        }

        th {
            background: #eee;
        }

        .actions {
            margin-top: 15px;
        }
    </style>
</head>

<body>
    <h1>Danh sách người dùng</h1>

    <!-- Form tìm kiếm theo quốc gia -->
    <form action="${pageContext.request.contextPath}/users" method="get">
        <input type="hidden" name="action" value="search">

        <label for="country">Tìm theo quốc gia:</label>

        <input
            type="text"
            id="country"
            name="country"
            value="<c:out value='${country}'/>"
            placeholder="Nhập tên quốc gia"
        >

        <button type="submit">Tìm kiếm</button>

        <a href="${pageContext.request.contextPath}/users">
            Hiển thị tất cả
        </a>
    </form>

    <!-- Chức năng thêm và sắp xếp -->
    <div class="actions">
        <a href="${pageContext.request.contextPath}/users?action=create">
            Thêm người dùng
        </a>

        <a href="${pageContext.request.contextPath}/users?action=sort">
            Sắp xếp theo tên A-Z
        </a>
    </div>

    <!-- Bảng danh sách người dùng -->
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Tên</th>
                <th>Email</th>
                <th>Quốc gia</th>
                <th>Thao tác</th>
            </tr>
        </thead>

        <tbody>
            <c:choose>
                <c:when test="${empty users}">
                    <tr>
                        <td colspan="5">
                            Không tìm thấy người dùng nào.
                        </td>
                    </tr>
                </c:when>

                <c:otherwise>
                    <c:forEach var="user" items="${users}">
                        <tr>
                            <td>
                                <c:out value="${user.id}"/>
                            </td>

                            <td>
                                <c:out value="${user.name}"/>
                            </td>

                            <td>
                                <c:out value="${user.email}"/>
                            </td>

                            <td>
                                <c:out value="${user.country}"/>
                            </td>

                            <td>
                                <a href="${pageContext.request.contextPath}/users?action=edit&id=${user.id}">
                                    Sửa
                                </a>

                                <a href="${pageContext.request.contextPath}/users?action=delete&id=${user.id}">
                                    Xóa
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</body>
</html>
