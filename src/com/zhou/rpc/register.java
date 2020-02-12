package com.zhou.rpc;

import com.zhou.db.MySQLConnection;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "register" , urlPatterns = {"/register"})
public class register extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MySQLConnection connection = new MySQLConnection();
        try {
            JSONObject input = rpcHelper.readJsonObject(request);
            String userId = input.getString("user_id");
            String password = input.getString("password");
            String firstname = input.getString("first_name");
            String lastname = input.getString("last_name");

            JSONObject obj = new JSONObject();
            if (connection.registerUser(userId, password, firstname, lastname)) {
                obj.put("status", "OK");
            } else {
                obj.put("status", "User Already Exists");
            }
            rpcHelper.writeJsonObject(response, obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }


}
