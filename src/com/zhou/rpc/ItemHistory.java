package com.zhou.rpc;

import com.zhou.db.MySQLConnection;
import com.zhou.entity.Item;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

@WebServlet(name = "history", urlPatterns = {"/history"})
public class ItemHistory extends HttpServlet {
     //点心操作 会发送post请求
     //使用函数， setFavoriteItems
     // 返回结果：set success
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //前端request--->json格式         read
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            return;
        }
        JSONObject input = rpcHelper.readJsonObject(request);
        /*
         * input格式为：
         * {
         *     "user_id", "xxx"
         *     "favorite",{
         *         item details
         *     }
         * }
         */
        try{
            String userId = input.getString("user_id");//分解数据中的user id
            //json--->item     parse
            Item item = rpcHelper.parseFavoriteItem(input.getJSONObject("favorite"));
            MySQLConnection connection = new MySQLConnection();
            connection.setFavoriteItems(userId,item);
            connection.close();
            rpcHelper.writeJsonObject(response,new JSONObject().put("result"," set success"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    //功能：查询 用户所有favorite
    //使用函数： getFavoriteItems (里面使用getFavoriteItemId)
    //返回结果：所有items
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //从request中得到user id
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            return;
        }
        String userId = request.getParameter("user_id");
        JSONArray array = new JSONArray();
        MySQLConnection connection = new MySQLConnection();
        Set<Item> items = connection.getFavoriteItems(userId);
        connection.close();

        for (Item item : items) {
            JSONObject obj = item.toJSONObject();
            try {
                obj.append("favorite", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }
        rpcHelper.writeJsonArray(response, array);
    }
    //功能：取消点心收藏
    //使用函数：unsetFavoriteItems
    //返回结果：unset success
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            return;
        }
        JSONObject input = rpcHelper.readJsonObject(request);  //从前端拿到数据的json格式
        try{
            String userId = input.getString("user_id");//分解数据中的user id
            String itemId = input.getJSONObject("favorite").getString("item_id");//分解数据中的item
            MySQLConnection connection = new MySQLConnection();
            connection.unsetFavoriteItems(userId,itemId);
            connection.close();
            rpcHelper.writeJsonObject(response,new JSONObject().put("result"," unset success"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
