package uz.akbarali.test.servlets.dao;

import uz.akbarali.test.servlets.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static uz.akbarali.test.servlets.utils.Constants.*;

public class UserDao {
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try {
            Statement statement = getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users order by id");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String fullName = resultSet.getString("full_name");
                int age = resultSet.getInt("age");
                users.add(new User(id, fullName, age));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public static User getUserById(int id) {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("select * from users where id=" + id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String fullName = resultSet.getString("full_name");
                int age = resultSet.getInt("age");
                return new User(id, fullName, age);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static boolean deleteUserById(int id) {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("delete from users WHERE id=" + id);
            statement.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean saveUser(String fullName, Integer age) {
        Connection connection = getConnection();
        try {
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement("insert into users (full_name, age) values('" + fullName + "', '" + age + "')");
            statement.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    public static boolean updateUser(User user) {
        Connection connection = getConnection();
        try {
            assert connection != null;
            String query = "update users\n" +
                    "set ";
            if (user.getAge() != 0)
                query += "age = "+user.getAge();
            if (user.getAge()!=0 && user.getFullname()!=null)
                query+=", ";
            if (user.getFullname() != null)
                query += "full_name='"+user.getFullname()+"' ";
            query+="where id="+user.getId();

            PreparedStatement statement = connection.prepareStatement(query);
            statement.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
}
