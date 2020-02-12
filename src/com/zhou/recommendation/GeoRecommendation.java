package com.zhou.recommendation;
import com.zhou.db.MySQLConnection;
import com.zhou.entity.Item;
import com.zhou.external.TicketMasterClient;

import java.util.*;
import java.util.Map.Entry;
public class GeoRecommendation {

    public List<Item> recommendItems(String userId, double lat, double lon ){
        List<Item> recommendItems = new ArrayList<>();
        //step1 get all favorite by user
        MySQLConnection connection = new MySQLConnection();
        Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
       //step 2 get categories
        Map<String,Integer> allCategories = new HashMap<>();
        for(String ItemId : favoritedItemIds){
            Set<String> categories = connection.getCategories(ItemId);//每一个喜欢的ID 得到它所属目录们
            for(String category : categories){
                allCategories.put(category,allCategories.getOrDefault(category,0)+1);

            }
        }
        //all categories为 用户所有 喜欢目录的一个哈希表
        connection.close();
        List<Entry<String,Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
        Collections.sort(categoryList, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
//        (Entry<String,Integer> e1, Entry<String,Integer> e2) -> {
//            return  Integer.compare(e2.getValue(),e1.getValue());
//        }
        //step 3 search based on category,exclude visited item
        TicketMasterClient client = new TicketMasterClient();
        Set<String> visitedItemIds = new HashSet<>();
        for(Entry<String,Integer> category : categoryList){
            List<Item> items = client.search(lat,lon,category.getKey());
            for(Item item : items){
                if(!favoritedItemIds.contains(item.getItemId())&&!visitedItemIds.contains(item.getItemId())){
                    recommendItems.add(item);
                    visitedItemIds.add(item.getItemId());
                }
            }
        }

        return recommendItems;
    }
}
