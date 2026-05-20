package analytics;
import enums.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import models.*;
import repository.*;

class OrderListPair {
    private List<Order> currentOrders;
    private List<Order> lastPeriodOrders;

    public OrderListPair(List<Order> currentOrders, List<Order> lastPeriodOrders) {
        this.currentOrders = currentOrders;
        this.lastPeriodOrders = lastPeriodOrders;
    }
    public List<Order> getCurrentOrders() {return currentOrders;}
    public List<Order> getLastPeriodOrders() {return lastPeriodOrders;}
}


public class AnalyticsManager {

    public OrderListPair findOrdersInRange(DateRange range,boolean withLastPeriod) {
        LocalDate startDate = null;
        LocalDate endDate = null;
        LocalDate lastStartDate = null;
        LocalDate lastEndDate = null;
        
        switch (range) {
            case DateRange.TODAY:
                startDate = LocalDate.now();
                endDate = LocalDate.now();
                if (withLastPeriod) {
                    lastStartDate = startDate.minusDays(1);
                    lastEndDate = endDate.minusDays(1);
                }
                break;
            case DateRange.THIS_WEEK:
                startDate = LocalDate.now().with(DayOfWeek.MONDAY);
                endDate = startDate.plusDays(6);
                if (withLastPeriod) {
                    lastStartDate = startDate.minusWeeks(1);
                    lastEndDate = endDate.minusWeeks(1);
                }
                break;
            case DateRange.THIS_MONTH:
                startDate = LocalDate.now().withDayOfMonth(1);
                endDate = startDate.plusMonths(1).minusDays(1);
                if (withLastPeriod) {
                    lastStartDate = startDate.minusMonths(1);
                    lastEndDate = endDate.minusMonths(1);
                }
                break;
        }
        OrderRepository OrderRepo = new OrderRepository();
        List<Order> currentOrders = OrderRepo.findOrdersInRange(startDate, endDate);
        List<Order> lastPeriodOrders = withLastPeriod ? OrderRepo.findOrdersInRange(lastStartDate, lastEndDate) : null;
        return new OrderListPair(currentOrders, lastPeriodOrders);
    }

    public List<Order> findOrdersInRange(LocalDate startDate, LocalDate endDate) {
        return new OrderRepository().findOrdersInRange(startDate, endDate);
    }
    


    public double getTotalRevenue(DateRange range) {
        OrderListPair orderLists = findOrdersInRange(range,false);
        List<Order> currentOrders = orderLists.getCurrentOrders();
        double totalRevenue = 0.0;
        for (Order order : currentOrders) {
            totalRevenue += order.getTotalPrice();
        }
        return totalRevenue;
    }

    public int getTotalOrders(DateRange range) {
        OrderListPair orderLists = findOrdersInRange(range,false);
        List<Order> currentOrders = orderLists.getCurrentOrders();
        return currentOrders.size();
    }

    public double getAverageOrderValue(DateRange range) {
        OrderListPair orderLists = findOrdersInRange(range,false);
        List<Order> currentOrders = orderLists.getCurrentOrders();
        if(getTotalOrders(range) != 0 && !currentOrders.isEmpty()){
            return getTotalRevenue(range) / getTotalOrders(range);
        } else {
            return 0.0;
        }
    }



    public double getRevenueChangePercent(DateRange range) {
        OrderListPair orderLists = findOrdersInRange(range,true);
        List<Order> currentOrders = orderLists.getCurrentOrders();
        List<Order> lastPeriodOrders = orderLists.getLastPeriodOrders();
        double currentTotalRevenue = 0.0;
        for (Order order : currentOrders) {
            currentTotalRevenue += order.getTotalPrice();
        }
        double lastPeriodTotalRevenue = 0.0;
        for (Order order : lastPeriodOrders) {
            lastPeriodTotalRevenue += order.getTotalPrice();
        }
        double revenueChange = currentTotalRevenue - lastPeriodTotalRevenue;
        double changePercent = lastPeriodTotalRevenue != 0.0 ? (revenueChange / lastPeriodTotalRevenue) * 100 : 0;
        return changePercent;
    }

    public int getOrdersCountChange(DateRange range) {
        OrderListPair orderLists = findOrdersInRange(range,true);
        List<Order> currentOrders = orderLists.getCurrentOrders();
        List<Order> lastPeriodOrders = orderLists.getLastPeriodOrders();
        int currentCount = currentOrders.size();
        int lastPeriodCount = lastPeriodOrders.size();
        return currentCount - lastPeriodCount;
    }

    public double getAvgOrderChangePercent(DateRange range) {
        OrderListPair orderLists = findOrdersInRange(range,true);
        List<Order> currentOrders = orderLists.getCurrentOrders();
        List<Order> lastPeriodOrders = orderLists.getLastPeriodOrders();
        double currentAvg = 0.0;
        if (!currentOrders.isEmpty()) {
            double currentTotalRevenue = 0.0;
            for (Order order : currentOrders) {
                currentTotalRevenue += order.getTotalPrice();
            }
            currentAvg = currentTotalRevenue / currentOrders.size();
        }
        double lastPeriodAvg = 0.0;
        if (!lastPeriodOrders.isEmpty()) {
            double lastPeriodTotalRevenue = 0.0;
            for (Order order : lastPeriodOrders) {
                lastPeriodTotalRevenue += order.getTotalPrice();
            }
            lastPeriodAvg = lastPeriodTotalRevenue / lastPeriodOrders.size();
        }
        double avgChange = lastPeriodAvg != 0.0 ? ((currentAvg - lastPeriodAvg) / lastPeriodAvg) * 100 : 0;
        return avgChange;
    }



    public String getPeakHourLabel(DateRange range) {
        int[] ordersByHour = getOrdersByHour(range);
        int peakHour = 0;
        int maxOrders = 0;
        for (int hour = 0; hour < ordersByHour.length; hour++) {
            if (ordersByHour[hour] > maxOrders) {
                maxOrders = ordersByHour[hour];
                peakHour = hour;
            }
        }
        return String.format("%d %s – %d %s", 
            (peakHour == 0 || peakHour == 12) ? 12 : peakHour % 12, 
            (peakHour < 12) ? "AM" : "PM",
            ((peakHour + 1) == 0 || (peakHour + 1) == 12) ? 12 : (peakHour + 1) % 12, 
            ((peakHour + 1) < 12) ? "AM" : "PM");
    }


    public int[] getRevenueByDay(DateRange range) {
        OrderListPair orderLists = findOrdersInRange(range,false);
        List<Order> currentOrders = orderLists.getCurrentOrders();
        
        int[] revenueByDay;
        switch (range) {
            case DateRange.TODAY:
                return new int[] {(int) getTotalRevenue(range)};
            case DateRange.THIS_WEEK:
                revenueByDay = new int[7];
                for (Order order : currentOrders) {
                    DayOfWeek dayOfWeek = order.getOrderDate().getDayOfWeek();
                    int dayIndex = dayOfWeek.getValue() - 1; // Monday=0, Sunday=6
                    revenueByDay[dayIndex] += order.getTotalPrice();
                }
                return revenueByDay;
            case DateRange.THIS_MONTH:
                LocalDate now = LocalDate.now();
                int daysInMonth = now.lengthOfMonth();
                revenueByDay = new int[daysInMonth];
                for (Order order : currentOrders) {
                    int dayOfMonth = order.getOrderDate().getDayOfMonth();
                    revenueByDay[dayOfMonth - 1] += order.getTotalPrice();
                }
                return revenueByDay;
        }
        return new int[0];
    }
    public String[] getRevenueByDayLabels(DateRange range) {
        switch (range) {
            case DateRange.TODAY:
                return new String[] {"Today"};
            case DateRange.THIS_WEEK:
                return new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            case DateRange.THIS_MONTH:
                LocalDate now = LocalDate.now();
                int daysInMonth = now.lengthOfMonth();
                String[] labels = new String[daysInMonth];
                for (int i = 0; i < daysInMonth; i++) {
                    labels[i] = String.valueOf(i + 1);
                }
                return labels;
        }
        return new String[0];
    }


    public int[] getOrdersByHour(DateRange range) {
        OrderListPair orderLists = findOrdersInRange(range,false);
        List<Order> currentOrders = orderLists.getCurrentOrders();
        int[] ordersByHour = new int[24];
        for (Order order : currentOrders) {
            int hour = order.getOrderTime().getHour();
            ordersByHour[hour]++;
        }
        return ordersByHour;
    }
    public String[] getOrdersByHourLabels() {
        return new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12",
                        "13","14","15","16","17","18","19","20","21","22","23"};
    }


    public String[][] getTopSellingItems(DateRange range, int limit) {
        OrderListPair orderLists = findOrdersInRange(range,false);
        List<Order> currentOrders = orderLists.getCurrentOrders();
        List<MenuItem> menuItems = new MenuRepository().findAll();
        int[] itemUnits = new int[menuItems.size()];
        for (Order order : currentOrders) {
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                String menuItemID = orderItem.getMenuItem().getID();
                int quantity = orderItem.getQuantity();
                for (int i = 0; i < menuItems.size(); i++) {
                    if (menuItems.get(i).getID().equals(menuItemID)) {
                        itemUnits[i] += quantity;
                        break;
                    }
                }
            } 
        }
        int[] itemUnitsCopy = itemUnits.clone();
        int[] topSellers = new int[limit]; //It will hold the index of the topSellers
        for (int i = 0; i < limit; i++) {
            int maxUnits = 0;
            int maxIndex = 0;
            for (int j = 0; j < itemUnitsCopy.length; j++) {
                if (itemUnitsCopy[j] > maxUnits) {
                    maxUnits = itemUnitsCopy[j];
                    maxIndex = j;
                }
            }
            topSellers[i] = maxIndex;
            itemUnitsCopy[maxIndex] = 0; //Reset to find the next top seller
        }

        String[][] topSellingItems = new String[limit][4];
        for (int i = 0; i < limit; i++) {
            int index = topSellers[i];
            if (index != -1) {
                MenuItem item = menuItems.get(index);
                topSellingItems[i][0] = item.getItemName();
                topSellingItems[i][1] = item.getCategory().toString();
                topSellingItems[i][2] = String.valueOf(itemUnits[index]);
                double revenue = itemUnits[index] * item.getPrice();
                topSellingItems[i][3] = String.valueOf(revenue) + " PKR";
            } else {
                topSellingItems[i][0] = "";
                topSellingItems[i][1] = "";
                topSellingItems[i][2] = "0";
                topSellingItems[i][3] = "0 PKR";
            }
        }
        return topSellingItems;
    }
    public String getTopSellingItemName(DateRange range) {
        String[][] topItems = getTopSellingItems(range, 1);
        if (topItems.length > 0 && topItems[0][0] != null) {
            return topItems[0][0];
        } else {
            return "";
        }
    }
    public int getTopSellingItemUnits(DateRange range) {
        String[][] topItems = getTopSellingItems(range, 1);
        if (topItems.length > 0 && topItems[0][2] != null) {
            return Integer.parseInt(topItems[0][2]);
        } else {
            return 0;
        }
    }


    
    public int[] getOrderCountByStatus(DateRange range, String[] statuses) {
        int completedCount = 0;
        int pendingCount = 0;   
        int cancelledCount = 0;
        int preparingCount = 0;
        int outForDeliveryCount = 0;
        OrderListPair orderLists = findOrdersInRange(range,false);
        List<Order> currentOrders = orderLists.getCurrentOrders();
        for (Order order : currentOrders) {
            OrderStatus status = order.getOrderStatus();
            if (status == OrderStatus.COMPLETED) {
                completedCount++;
            } else if (status == OrderStatus.PENDING) {
                pendingCount++;
            } else if (status == OrderStatus.CANCELLED) {
                cancelledCount++;
            } else if (status == OrderStatus.PREPARING) {
                preparingCount++;
            } else if (status == OrderStatus.OUT_FOR_DELIVERY) {
                outForDeliveryCount++;
            }
        }
        return new int[] { pendingCount, preparingCount, outForDeliveryCount, completedCount, cancelledCount };
    }
}