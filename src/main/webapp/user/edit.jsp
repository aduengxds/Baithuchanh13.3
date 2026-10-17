<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Sửa người dùng</title>
</head>
<body>
    <h1>Sửa thông tin người dùng</h1>

    <form action="${pageContext.request.contextPath}/users" method="post">
        <input type="hidden" name="action" value="edit">
        <input type="hidden" name="id" value="<c:out value='${user.id}'/>">

        <p>
            <label for="name">Tên:</label><br>
            <input type="text" id="name" name="name"
                   maxlength="120"
                   value="<c:out value='${user.name}'/>" required>
        </p>

        <p>
            <label for="email">Email:</label><br>
            <input type="email" id="email" name="email"
                   maxlength="220"
                   value="<c:out value='${user.email}'/>" required>
        </p>

        <p>
            <label for="country">Quốc gia:</label><br>
            <input type="text" id="country" name="country"
                   maxlength="120"
                   value="<c:out value='${user.country}'/>">
        </p>

        <button type="submit">Cập nhật</button>
        <a href="${pageContext.request.contextPath}/users">Quay lại</a>
    </form>
</body>
</html>