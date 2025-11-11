package org.example.repository.implementations;
import org.example.model.order.Order;
import org.example.model.order.OrderStatus;
import org.example.repository.OrderRepository;
import org.example.repository.RestaurantRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantRepositoryImpl implements RestaurantRepository {
    Map<String, List<String>> restaurantOrderMap = new ConcurrentHashMap<>();

    // add complex logic here.. & Based on current order in queue add order to queue or keep on waiting..
    @Override
    public Boolean addOrder(String restaurantId, String orderId) {
        if(!restaurantOrderMap.containsKey(restaurantId)){
            restaurantOrderMap.put(restaurantId, new ArrayList<>());
        };
        restaurantOrderMap.get(restaurantId).add(orderId);
        return true;
    }

    @Override
    public List<String> findById(String restaurantId) {
        return restaurantOrderMap.computeIfAbsent(restaurantId, k-> new ArrayList<>());
    }

}
