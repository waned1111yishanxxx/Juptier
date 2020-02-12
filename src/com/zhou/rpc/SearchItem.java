package com.zhou.rpc;
import com.zhou.entity.Item;
import com.zhou.external.TicketMasterClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.zhou.db.MySQLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet(name = "search", urlPatterns = {"/search"})
public class SearchItem extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            return;
        }
        //String userId = session.getAttribute("user_id").toString();
        String userId = request.getParameter("user_id");
        double lat = Double.parseDouble(request.getParameter("lat"));
       double lon = Double.parseDouble(request.getParameter("lon"));
        TicketMasterClient client = new TicketMasterClient();
        List<Item> items = client.search(lat,lon,null); //从票API网站上取到items数据



        /*-----------需要连接数据库去查是否favorite数据---------------*/
       // String userId = request.getParameter("user_id");
        MySQLConnection connection = new MySQLConnection();
        //读取数据库 favorite 的 itemid
        //从history里读
        Set<String> favoriteItemIds = connection.getFavoriteItemIds(userId);
        connection.close();
        JSONArray array = new JSONArray();
        for (Item item : items) {
            // obj 包括 item全部 + 是否favorite
            JSONObject obj = item.toJSONObject();
            try {
                obj.put("favorite", favoriteItemIds.contains(item.getItemId()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }

        rpcHelper.writeJsonArray(response, array);
    }
}
