package recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

import java.util.Map.Entry;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon){
		List<Item> recommendedItems= new ArrayList<>();
		// get add favorited itemIds
		DBConnection connection = DBConnectionFactory.getConnection();
		Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
		// get all qcategories, sort by count {"sports" : 5, "art":2}
		Map<String, Integer> allCategoriesMap = new HashMap<>();
		for(String itemId : favoritedItemIds) {
			Set<String> categorieSet = connection.getCategories(itemId);
			for(String category : categorieSet) {
				allCategoriesMap.put(category, allCategoriesMap.getOrDefault(category, 0) + 1);
			}
		}
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategoriesMap.entrySet());
		Collections.sort(categoryList, (Entry<String, Integer> e1, Entry<String, Integer> e2) -> {
			return Integer.compare(e2.getValue(), e1.getValue());
		});
		// step3 search based on category, fillter our favorite items
		Set<String> visitedItemIdSet = new HashSet<>();
		for(Entry<String, Integer> categoryEntry : categoryList) {
			List<Item> items = connection.searchItems(lat, lon, categoryEntry.getKey());
			for(Item item: items) {
				if(!favoritedItemIds.contains(item.getItemId()) && !visitedItemIdSet.contains(item.getItemId())) {
					recommendedItems.add(item);
					visitedItemIdSet.add(item.getItemId());
				}
			}
		}
		connection.close();
		return recommendedItems;
	}
	
}
