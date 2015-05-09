$.writer_code_global = {};
$(document).ready(function(e){
	fetchWriterId();
	linkClick();
	validateAssignOrderForm();
	submitAssignedWriterIdForNonPreferedWriter();
	setInterval(setAdminLocalForOrderAssignment, 5000);
	
	if($('#btn-assign-for-prefered-writer').length !=0){
		assignForPreferedWriter();
	}
	
	if($('#assign_order_form').length !=0){
		$("#assigning-writer-id").on("propertychange change keyup paste input", function(){
			var writer_id = $.trim($("#assigning-writer-id").val());
			var found = false;
			for(var i=0; i<$.writer_code_global.writer_id.length;i++){
				if(writer_id == $.writer_code_global.writer_id[i]['writer_id']){
					found=true;
				}
			}
			
		      if(found){
			      $('#assign-writer-response-message').html("<p class='text-success'>Correct writer code</p>");
			  
		      }else{      
			      $('#assign-writer-response-message').html("<p class='text-danger'>Wrong writer code</p>");
		      }
		});
	}
	
});

function assignForPreferedWriter(){
	$('#btn-assign-for-prefered-writer').click(function(event){
		event.preventDefault();
		var writer_id = $('#prefered-writer-id').val();
		var order_code = $('#assigned-order-code-for-prefered').val();
		var admin_local_time = $('#assign_order_local_time').val();
		$('#loading-gif-for-prefered-writer-assign').removeClass("hidden");
	 $.ajax({
            type: "POST",
            url:"/manageorder/assign/" + admin_local_time + "/" + order_code + "/" + writer_id,
            success: function(data){
			      if(data['success'] == 1){
				     $('#loading-gif-for-prefered-writer-assign').addClass("hidden");
				     $('#assign-writer-response-message-for-prefered-writer').html("<p class='text-success'>" + data['message'] +"</p>");
				     $('#btn-assign-for-prefered-writer').addClass("hidden");
			      }else if(data['success'] == 3){
				      window.reload();
			      }else{
				     $('#loading-gif-for-prefered-writer-assign').addClass("hidden"); 
				    $('#assign-writer-response-message-for-prefered-writer').html("<p class='text-danger'>" + data['message'] +"</p>");  
			      } 
            }
        });
			
	});
}

function submitAssignedWriterIdForNonPreferedWriter(){
	  $('#order-assign-btn').click(function(event){
	    event.preventDefault();
	    var writer_id = $.trim($("#assigning-writer-id").val());
	    var order_code = $('#assigned-order-code').val();
	    var admin_local_time = $('#assign_order_local_time').val();
	    $('#loading-gif-for-non-prefered-writer-assign').removeClass("hidden");
	    $.ajax({
            type: "POST",
            url:"/manageorder/assign/" + admin_local_time + "/" + order_code + "/" + writer_id,
            success: function(data){
			      if(data['success'] == 1){
				     $('#loading-gif-for-non-prefered-writer-assign').addClass("hidden");
				     $('#assign-writer-response-message').html("<p class='text-success'>" + data['message'] +"</p>");
				     $('#assign-order-form').addClass("hidden");
				     $('#no-prefered-writer-assign-btn').addClass("hidden");
			      }else if(data['success'] == 3){
				      window.reload();
			      }else{
				     $('#loading-gif-for-non-prefered-writer-assign').addClass("hidden");
				     $('#assign-writer-response-message').html("<p class='text-danger'>" + data['message'] +"</p>");  
			      } 
            }
        });
	return false;	    
  });
     
}

function validateAssignOrderForm(){
  $('#assign_order_form').bootstrapValidator({
    feedbackIcons: {
      valid: 'glyphicon glyphicon-ok',
      invalid: 'glyphicon glyphicon-remove',
      validating: 'glyphicon glyphicon-refresh'
    },
    fields: {
	assigning_writer_id: {
	  validators: {
	    notEmpty: {
	    message: 'First name is required'
	    },
	    digits:{
	      message:'Only numbers required'
	    }
	  }
	}
    }
  });
}

function fetchWriterId(){
    $.post("/manageorder/fetchwriter/",{}, function(data){
	$.writer_code_global.writer_id = data;
    },'json');
}

function linkClick(){
	$('#no-prefered-writer-assign-btn').click(function(event){
		event.preventDefault();
		//remove hidden class
		if($('#assign-order-form').hasClass("hidden")){
			$('#assign-order-form').removeClass("hidden");
		}else{
			$('#assign-order-form').addClass("hidden");
			$("#assigning-writer-id").val("");
			$('#assign-writer-response-message').html("");
		}
		
	});
}

function setAdminLocalForOrderAssignment(){
  $('#assign_order_local_time').val(new Date().toString("yyyy-MM-dd HH:mm:ss"));
}