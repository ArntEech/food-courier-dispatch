package com.foodcourier.alpha1;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.foodcourier.domain.Customer;
import com.foodcourier.domain.Order;
import com.foodcourier.domain.OrderStatus;
import com.foodcourier.domain.Priority;
import com.foodcourier.domain.Restaurant;

class OrderIntakeServiceTest {

    private static final Path SEED_FILE = Path.of("data", "seed", "orders.csv");

    @Test
    void receivesRealOrdersAndProcessesThemInFifoOrder() {
        OrderIntakeService service = new OrderIntakeService();
        Order first = order("first", "2026-08-13T12:00:00");
        Order second = order("second", "2026-08-13T12:01:00");

        service.receiveOrder(first);
        service.receiveOrder(null);
        service.receiveOrder(second);

        assertEquals(2, service.getQueueSize());
        assertEquals(first, service.getNextOrder());
        assertEquals(second, service.getNextOrder());
        assertNull(service.getNextOrder());
    }

    @Test
    void loadsSeedOrdersAndSupportsSearchAndTimestampSorting() throws Exception {
        OrderIntakeService service = new OrderIntakeService();

        service.loadSeedData(SEED_FILE);

        assertEquals(6, service.getQueueSize());
        assertTrue(service.findOrderById("3").isPresent());
        assertEquals("1", service.getNextOrder().getId());
        assertEquals("2", service.findOrderById("2").get().getId());

        List<Order> sorted = service.getOrdersSortedByTimestamp();
        assertEquals(List.of("2", "3", "4", "5", "6"),
                sorted.subList(0, 5).stream().map(Order::getId).toList());
        assertEquals(LocalDateTime.parse("2026-08-13T12:03:00"), sorted.get(0).getTimestamp());
    }

    @Test
    void replacesQueueWhenLoadingAList() {
        OrderIntakeService service = new OrderIntakeService();
        service.receiveOrder(order("old", "2026-08-13T12:00:00"));

        service.loadSeedData(List.of(order("new", "2026-08-13T12:01:00")));

        assertEquals(1, service.getQueueSize());
        assertEquals("new", service.getNextOrder().getId());
    }

    private Order order(String id, String timestamp) {
        return new Order(id, new Customer("customer", "Customer", "", null),
                new Restaurant("restaurant", "Restaurant", null), 25.0, Priority.MEDIUM, OrderStatus.PENDING,
                LocalDateTime.parse(timestamp));
    }
}
