<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xóa người dùng</title>
</head>
<body>
    <h1>Xác nhận xóa người dùng</h1>

    <p>Bạn có chắc chắn muốn xóa người dùng này không?</p>

    <ul>
        <li>ID: <c:out value="${user.id}"/></li>
        <li>Tên: <c:out value="${user.name}"/></li>
        <li>Email: <c:out value="${user.email}"/></li>
        <li>Quốc gia: <c:out value="${user.country}"/></li>
    </ul>

    <form action="${pageContext.request.contextPath}/users" method="post">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="id" value="<c:out value='${user.id}'/>">

        <button type="submit">Xác nhận xóa</button>
        <a href="${pageContext.request.contextPath}/users">Hủy</a>
    </form>
</body>
</html>