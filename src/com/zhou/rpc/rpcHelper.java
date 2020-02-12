package com.zhou.rpc;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zhou.entity.Item;
import com.zhou.entity.Item.ItemBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class rpcHelper {
    public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {
        response.setContentType("application/json");
        response.getWriter().print(array);
    }
    public static void writeJsonObject(HttpServletResponse response, JSONObject object) throws IOException {
        response.setContentType("application/json");
        response.getWriter().print(object);
    }
    public static JSONObject readJsonObject(HttpServletRequest request)throws IOException {
        //使用request的reader逐行写入sb，再转成string，再转成jsonobject
         StringBuilder stringbuilder = new StringBuilder();
         try(BufferedReader reader = request.getReader()){
             String line = null;
             while((line=reader.readLine())!=null){
                 stringbuilder.append(line);
             }
             return new JSONObject(stringbuilder.toString());
         }catch(Exception e){
             e.printStackTrace();
         }
         return new JSONObject();
    }
     //jsonobject ---> Item
    public static Item parseFavoriteItem (JSONObject favoriteItem) throws JSONException{
        ItemBuilder builder = new ItemBuilder();
        builder.setItemId(favoriteItem.getString("item_id"))
                .setName(favoriteItem.getString("name"))
                .setRating(favoriteItem.getDouble("rating"))
                .setDistance(favoriteItem.getDouble("distance"))
                .setImageUrl(favoriteItem.getString("image_url"))
                .setUrl(favoriteItem.getString("url"))
                .setAddress(favoriteItem.getString("address"));

        Set<String> categories = new HashSet<>();
        if(!favoriteItem.isNull("categories")){
            JSONArray array = favoriteItem.getJSONArray("categories");
            for(int i=0;i<array.length();i++){
                categories.add(array.getString(i));
            }
        }
        builder.setCategories(categories);
        return builder.build();
    }
}
