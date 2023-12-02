package uz.akbarali.test.servlets.service;

import com.google.gson.Gson;
import uz.akbarali.test.servlets.dao.UserDao;
import uz.akbarali.test.servlets.models.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class UserService {
    Gson gson = new Gson();

    public void getAllUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                User user = UserDao.getUserById(id);
                if (user == null) {
                    resp.setStatus(204);
                    resp.getWriter().write("No contend");
                    return;
                }
                String data = this.gson.toJson(user);
                resp.getWriter().write(data);
                resp.setStatus(200);
                return;

            } catch (Exception e) {
                resp.setStatus(204);
                resp.getWriter().write("No contend");
            }
        }

        List<User> allUsers = UserDao.getAllUsers();
        String userStr = this.gson.toJson(allUsers);
        resp.setStatus(200);
        resp.getWriter().write(userStr);
    }
    public void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                boolean isDeleted = UserDao.deleteUserById(id);
                if (isDeleted) {
                    resp.getWriter().write("deleted");
                    resp.setStatus(200);
                }

            } catch (Exception e) {
                resp.setStatus(404);
            }
        }

        resp.getWriter().write("not found");
        resp.setStatus(204);

    }

    public void createUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        User user = gson.fromJson(requestBody.toString(), User.class);
        if (user.getAge() == 0 || user.getFullname() == null) {
            resp.setContentType("application/json");
            resp.setStatus(403);
            resp.getWriter().write("Not valid");
            return;
        }
        UserDao.saveUser(user.getFullname(), user.getAge());
        resp.setContentType("application/json");
        resp.setStatus(200);
        resp.getWriter().write("created");
    }

    public void updateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = mapper(req.getReader());
        String pathInfo = req.getPathInfo();
        int id = 0;
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                id = Integer.parseInt(pathInfo.substring(1));
                if (UserDao.getUserById(id) == null) {
                    resp.setStatus(204);
                    resp.getWriter().write("No contend");
                    return;
                }
            } catch (Exception ignored) {
                resp.setStatus(204);
                resp.getWriter().write("No contend");
                return;
            }
        } else {
            resp.setStatus(204);
            resp.getWriter().write("No contend");
            return;
        }

        user.setId(id);
        if (user.getAge() == 0 && user.getFullname() == null) {
            resp.setContentType("application/json");
            resp.setStatus(403);
            resp.getWriter().write("Not valid");
            return;
        }
        UserDao.updateUser(user);
        resp.setContentType("application/json");
        resp.setStatus(200);
        resp.getWriter().write("updated");
    }

    public User mapper(BufferedReader read) {
        StringBuilder requestBody = new StringBuilder();
        String line;
        try (BufferedReader reader = read) {
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return gson.fromJson(requestBody.toString(), User.class);
    }
}
