function del(urlToDelete) {  
  if(confirm('You are about to delete a coupon code. Do you wish to proceed?')){
	$.ajax({
		url: urlToDelete,
		type: 'DELETE',
		success: function(results) {
		//Load this very page as a new document.
		location.assign("../newadmincouponcodes");
		}
	});
  }else{/*Do nothing*/}
}