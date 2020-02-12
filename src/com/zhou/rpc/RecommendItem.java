package com.zhou.rpc;

import com.zhou.entity.Item;
import com.zhou.external.TicketMasterClient;
import com.zhou.recommendation.GeoRecommendation;
import org.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "RecommendItem" , urlPatterns = {"/recommend"})
public class RecommendItem extends HttpServlet {
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            return;
        }
        String userId = request.getParameter("user_id");
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));
        GeoRecommendation recommendation = new GeoRecommendation();
        List<Item> items = recommendation.recommendItems(userId,lat,lon);
        JSONArray array = new JSONArray();
        for(Item item: items){
            array.put(item.toJSONObject());
        }
        rpcHelper.writeJsonArray(response,array);
    }
}
