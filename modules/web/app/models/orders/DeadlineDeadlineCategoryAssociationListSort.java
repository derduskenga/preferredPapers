package models.orders;

import java.util.Comparator;

public class DeadlineDeadlineCategoryAssociationListSort implements Comparator<DeadlineDeadlineCategoryAssociation>{
	    @Override
	    public int compare(final DeadlineDeadlineCategoryAssociation object1, final DeadlineDeadlineCategoryAssociation object2) {
	      return object1.id.compareTo(object2.id);
	    }
	  
}