package com.zhou.rpc;

import com.mysql.cj.xdevapi.JsonArray;
import com.zhou.db.MySQLConnection;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "login" , urlPatterns = {"/login"})
public class login extends HttpServlet {
    //check账号和密码 ，登陆成功就redirect到homepage，失败则不变
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        MySQLConnection connection = new MySQLConnection();
        try {
            JSONObject input = rpcHelper.readJsonObject(request);
            String userId = input.getString("user_id");
            String password = input.getString("password");

            JSONObject obj = new JSONObject();
            if (connection.verifyLogin(userId, password)) {
                HttpSession session = request.getSession();
                session.setAttribute("user_id", userId);
                session.setMaxInactiveInterval(600);
                obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
            } else {
                obj.put("status", "User Doesn't Exist");
                response.setStatus(401);
            }
            rpcHelper.writeJsonObject(response, obj);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }

    }
   //看用户有没有sessionid， 有 就到homepage，没有就redirect到login界面
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //     true： 判断当前是否有session，没有则创建，有则使用原来的
        //默认是false，判断是否有session，没有的话也不去创建，为null
        MySQLConnection connection = new MySQLConnection();
        try {
            HttpSession session = request.getSession(false);
            JSONObject obj = new JSONObject();
            if (session != null) {
                String userId = session.getAttribute("user_id").toString();
                obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
            } else {
                obj.put("status", "Invalid Session");
                response.setStatus(403);
            }
            rpcHelper.writeJsonObject(response, obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}
