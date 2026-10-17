package com.codegym.controller;

import com.codegym.dao.UserDAO;
import com.codegym.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "UserServlet", urlPatterns = "/users")
public class UserServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if (action == null || action.isBlank()
                    || "list".equals(action)) {
                listUsers(request, response);
                return;
            }

            switch (action) {
                case "search":
                    searchUsers(request, response);
                    break;

                case "sort":
                    sortUsers(request, response);
                    break;

                case "create":
                    showCreateForm(request, response);
                    break;

                case "edit":
                    showEditForm(request, response);
                    break;

                case "delete":
                    showDeleteForm(request, response);
                    break;

                case "test-without-tran":
                    testWithoutTran(request, response);
                    break;
                    
                case "test-use-tran":
                    testUseTran(request, response);
                    break;
                default:
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND
                    );
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(
                    "Lỗi khi truy cập cơ sở dữ liệu", e
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if (action == null || action.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            switch (action) {
                case "create":
                    insertUser(request, response);
                    break;

                case "edit":
                    updateUser(request, response);
                    break;

                case "delete":
                    deleteUser(request, response);
                    break;

                default:
                    response.sendError(
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(
                    "Lỗi khi thao tác với cơ sở dữ liệu", e
            );
        }
    }

    // Hiển thị danh sách người dùng
    private void listUsers(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        List<User> users = userDAO.selectAllUsers();
        request.setAttribute("users", users);

        request.getRequestDispatcher("/user/list.jsp")
                .forward(request, response);
    }

    // Tìm kiếm người dùng theo quốc gia
    private void searchUsers(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        String country = request.getParameter("country");

        if (country == null) {
            country = "";
        }

        List<User> users = userDAO.searchByCountry(country);

        request.setAttribute("country", country);
        request.setAttribute("users", users);

        request.getRequestDispatcher("/user/list.jsp")
                .forward(request, response);
    }

    // Sắp xếp người dùng theo tên A-Z
    private void sortUsers(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        List<User> users = userDAO.sortByName();
        request.setAttribute("users", users);

        request.getRequestDispatcher("/user/list.jsp")
                .forward(request, response);
    }

    // Hiển thị form thêm người dùng
    private void showCreateForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher("/user/create.jsp")
                .forward(request, response);
    }

    // Hiển thị form sửa người dùng bằng Stored Procedure
    private void showEditForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        int id = parseId(request, response);

        if (id == -1) {
            return;
        }

        User user = userDAO.getUserById(id);

        if (user == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Không tìm thấy người dùng."
            );
            return;
        }

        request.setAttribute("user", user);

        request.getRequestDispatcher("/user/edit.jsp")
                .forward(request, response);
    }

    // Hiển thị form xác nhận xóa
    private void showDeleteForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        int id = parseId(request, response);

        if (id == -1) {
            return;
        }

        User user = userDAO.selectUser(id);

        if (user == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Không tìm thấy người dùng."
            );
            return;
        }

        request.setAttribute("user", user);

        request.getRequestDispatcher("/user/delete.jsp")
                .forward(request, response);
    }

    // Bài 13.2: Thêm User và phân quyền trong cùng một Transaction
    private void insertUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String country = request.getParameter("country");

        String[] permissionsStr =
                request.getParameterValues("permissions");

        int[] permissionIds = null;

        if (permissionsStr != null && permissionsStr.length > 0) {
            permissionIds = new int[permissionsStr.length];

            for (int i = 0; i < permissionsStr.length; i++) {
                try {
                    permissionIds[i] =
                            Integer.parseInt(permissionsStr[i]);
                } catch (NumberFormatException e) {
                    throw new SQLException(
                            "Mã Permission không hợp lệ: "
                                    + permissionsStr[i],
                            e
                    );
                }

                if (permissionIds[i] < 1 || permissionIds[i] > 4) {
                    throw new SQLException(
                            "Permission không hợp lệ: "
                                    + permissionIds[i]
                    );
                }
            }
        }

        User user = new User(name, email, country);

        userDAO.addUserTransaction(user, permissionIds);

        redirectToList(request, response);
    }

    // Bài 13.3: Kiểm thử SQL không sử dụng Transaction
    private void testWithoutTran(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            userDAO.insertUpdateWithoutTransaction();

            System.out.println(
                    "Đã chạy xong hàm testWithoutTran. "
                    + "Hãy kiểm tra database Employee!"
            );

            out.println("<!DOCTYPE html>");
            out.println("<html lang='vi'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Kiểm thử SQL</title>");
            out.println("</head>");
            out.println("<body>");
            out.println(
                    "<h2>Đã thực thi bài kiểm thử SQL không Transaction.</h2>"
            );
            out.println(
                    "<p>Hãy kiểm tra Console và bảng Employee trong MySQL.</p>"
            );
            out.println(
                    "<a href='" + request.getContextPath()
                    + "/users'>Quay về danh sách người dùng</a>"
            );
            out.println("</body>");
            out.println("</html>");
        }
    }

    // Cập nhật người dùng
    private void updateUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, IOException {

        int id = parseId(request, response);

        if (id == -1) {
            return;
        }

        User user = new User(
                id,
                request.getParameter("name"),
                request.getParameter("email"),
                request.getParameter("country")
        );

        userDAO.updateUser(user);
        redirectToList(request, response);
    }

    // Xóa người dùng
    private void deleteUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, IOException {

        int id = parseId(request, response);

        if (id == -1) {
            return;
        }

        userDAO.deleteUser(id);
        redirectToList(request, response);
    }

    // Kiểm tra ID đầu vào
    private int parseId(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            if (id <= 0) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "ID không hợp lệ."
                );
                return -1;
            }

            return id;

        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID không hợp lệ."
            );
            return -1;
        }
    }

    // Quay về danh sách sau khi thêm, sửa hoặc xóa
    private void redirectToList(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/users"
        );
    }

    private void testUseTran(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            userDAO.insertUpdateUseTransaction();

            out.println("<h3>Transaction thực hiện thành công.</h3>");
        } catch (SQLException e) {
            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            try (PrintWriter out = response.getWriter()) {
                out.println("<h3>Transaction thất bại.</h3>");
                out.println("<p>" + e.getMessage() + "</p>");
            }
        }
    }
}
