package models.orders;

import java.util.Comparator;

public class ListSort implements Comparator<OrderDocumentType>{
	    @Override
	    public int compare(final OrderDocumentType object1, final OrderDocumentType object2) {
	      return object1.id.compareTo(object2.id);
	    }
	  
}