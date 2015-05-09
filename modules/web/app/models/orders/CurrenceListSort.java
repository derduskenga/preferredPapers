package models.orders;

import java.util.Comparator;

public class CurrenceListSort implements Comparator<OrderCurrence>{
	    @Override
	    public int compare(final OrderCurrence object1, final OrderCurrence object2) {
	      return object1.order_currency_id.compareTo(object2.order_currency_id);
	    }
	  
}