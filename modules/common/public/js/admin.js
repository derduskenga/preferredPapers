$(document).ready(function(){
	$('[data-toggle="tooltip"]').tooltip();	
	listenToActivateDeactivateUserEvents();
	checkTabs();
}); 

function checkTabs(){
		var hash = window.location.hash;
		hash && $('ul.nav a[href="' + hash + '"]').tab('show');

		$('.nav-tabs a').click(function (e) {
			$(this).tab('show');
			var scrollmem = $('body').scrollTop();
			window.location.hash = this.hash;
			$('html,body').scrollTop(scrollmem);
		});
}

function listenToActivateDeactivateUserEvents(){
				var id_;
				var cur_class;
				$('.span-icons').hover(function() {
						id_ = $(this).attr('id');
						cur_class= $(this).hasClass("fa-ban");
						cur_class1= $(this).attr("class");
						if(cur_class){
							$('#'+id_).removeClass("fqa-ban").addClass("fa-check");
						}else{
								$('#'+id_).removeClass("fa-check").addClass("fa-ban");
						}
				}, function() {
								$('#'+id_).removeClass().addClass(cur_class1);
				});
}

function deactivateUser(url){
	var id_ = url.split('/')[2];
	if($('#status-'+id_).html().trim()=="Deactivated")
		var prompt_string = "Are you sure you want to activate this user?";
	else
		var prompt_string = "Are you sure you want to deactivate this user?";
	bootbox.confirm( prompt_string, function(result) {
		if(result){
		var newDataRequest = $.ajax({
						type: "POST",
						url: url ,
						timeout: 30000, // timeout after 30 seconds
						dataType: "JSON" //JSON
				});
				newDataRequest.done(function(data){
					var id_ = url.split('/')[2];
					if(data["status"]=="1"){
							if(data["deactivation"] == 1){
								console.log($('#span-'+id_).attr("class"));
								$('#span-'+id_).removeClass("fa-check").addClass("fa-ban")
								$('#status-'+id_).html("Deactivated");

								$('#span-'+id_).parent().tooltip('hide')
									.attr('data-original-title', 'Activate User')
									.tooltip('fixTitle')
									.tooltip('show');
							}else{
								$('#span-'+id_).removeClass("fa-ban").addClass("fa-check");
								$('#status-'+id_).html("Active");
								$('#span-'+id_).parent().tooltip('hide')
									.attr('data-original-title', 'Dectivate User')
									.tooltip('fixTitle')
									.tooltip('show');
							}
						}else if(data["status"]=="0"){

						}
				});

				newDataRequest.fail(function(jqXHR, exception){
						if (jqXHR.status === 0){
							alert('Sorry, could not establishing a network connection.');
						}else if (jqXHR.status == 404){
							alert('Requested page not found. [404]');
						}else if (jqXHR.status == 500){
							alert('Internal Server Error [500].');
						}else if (exception === 'parsererror'){
							alert('Requested JSON parse failed.');
						}else if (exception === 'timeout'){
							alert('Time out error.');
						}else if (exception === 'abort'){
							alert('Sorry, Request was aborted.');
						}else{
							alert('Sorry, an error occured.');
						}
				});
		}
	});
}

function alternateNewRoleView(panel){
	if(panel=="show_form"){
			$('.add-role-div').hide(1000);
			$('.add-role-form').show(1000);
	}
	if(panel=="show_btn"){
			$('.add-role-div').show(1000);
			$('.add-role-form').hide(1000);
	}
}