package models.orders;

import java.util.Comparator;

public class SpacingListSort implements Comparator<Spacing>{
	    @Override
	    public int compare(final Spacing object1, final Spacing object2) {
	      return object1.id.compareTo(object2.id);
	    }
	  
}