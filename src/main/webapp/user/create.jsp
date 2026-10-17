
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm người dùng</title>
</head>
<body>
    <h1>Thêm người dùng</h1>

    <form action="${pageContext.request.contextPath}/users" method="post">
        <input type="hidden" name="action" value="create">

        <p>
            <label for="name">Tên:</label><br>
            <input type="text" id="name" name="name"
                   maxlength="50" required>
        </p>

        <p>
            <label for="email">Email:</label><br>
            <input type="email" id="email" name="email"
                   maxlength="50" required>
        </p>

        <p>
            <label for="country">Quốc gia:</label><br>
            <input type="text" id="country" name="country"
                   maxlength="50" required>
        </p>

        <div class="form-group">
            <label>Quyền hạn (Permissions):</label>

            <div style="display: flex; gap: 15px; margin-top: 5px; flex-wrap: wrap;">
                <label>
                    <input type="checkbox" name="permissions" value="1">
                    Thêm (Add)
                </label>

                <label>
                    <input type="checkbox" name="permissions" value="2">
                    Sửa (Edit)
                </label>

                <label>
                    <input type="checkbox" name="permissions" value="3">
                    Xóa (Delete)
                </label>

                <label>
                    <input type="checkbox" name="permissions" value="4">
                    Xem (View)
                </label>
            </div>
        </div>

        <p>
            <button type="submit">Lưu thông tin</button>
            <a href="${pageContext.request.contextPath}/users">Quay lại</a>
        </p>
    </form>
</body>
</html>
