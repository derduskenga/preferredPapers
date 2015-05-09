function del(urlToDelete) {
	$.ajax({
		url: urlToDelete,
		type: 'DELETE',
		success: function(results) {
			// Refresh the page
			location.reload();
		}
	});
}