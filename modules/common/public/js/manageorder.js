$(document).ready(function(){
  setInterval(setFileLocalTime, 5000);
  setInterval(setLocalTimeForOrderManagement, 5000);
  validateUploadFile();
  askDeadlineExtensionAdmin();
    
  askForExtraPages();
  adminProductFileDownload();
  adminOrderFileDownload(); 
  
  jQuery('#order_deadline_admin').datetimepicker({
    format:'Y-m-d H:i'
  });
  
});

function adminOrderFileDownload(){
  $('.order-file-shared-class').click(function(e){
    e.preventDefault();
    var file_id = this.id.split("-")[2];
    window.location.href = "/manageorder/downloadOrderfile/" + file_id;
  });
}


function adminProductFileDownload(){
  $('.admin-product-file-shared-class').click(function(e){
    e.preventDefault();
    var file_id = this.id.split("-")[2];
    window.location.href = "/manageorder/downloadproductfile/" + file_id;
  });
}

function setFileLocalTime(){
  $('#file_local_date').val(new Date().toString("yyyy-MM-dd HH:mm:ss"));
}


function setLocalTimeForOrderManagement(){
  
  $('#admin_local_time_for_order_management').val(new Date().toString("yyyy-MM-dd HH:mm:ss"));
}

function askForExtraPages(){
	$('#extra_pages_form').bootstrapValidator({
        // Removing submitHandler option
	    icon: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
	    
	      fields: {
		  extra_pages: {
	      validators: {
		notEmpty: {
		      message: 'Extra pages required. Please enter a number'
		  },
		  integer:{
		    message:'An integer required'
		  }
	      }
	    }	    
	  }
   	
    }).on('success.form.bv', function(e) {
        // Prevent form submission
        e.preventDefault();

        var $form        = $(e.target),
            validator    = $form.data('bootstrapValidator'),
            submitButton = validator.getSubmitButton();
	    
    var date = $('#admin_local_time_for_order_management').val();
    var pages = $('#extra_pages').val();
    var order_code = $('#order-code-admin').text();
    $('#loading-gif-extra-pages').removeClass("hidden");
    $.post("/manageorder/askforextrapages/" + pages + "/" + order_code + "/" + date,{}, function(data){
      if(data['success'] == 1){
	$('#loading-gif-extra-pages').addClass("hidden");
	$('#additional-pages-request-response').html("<div id='p-response' class='alert alert-success'>" + data['message'] +  "</div>");
	$("#p-response").show().delay(5000).fadeOut("slow");
      }else{
	$('#loading-gif-extra-pages').addClass("hidden");
	$('#additional-pages-request-response').html("<div id='p-response' class='alert alert-danger'>" + data['message'] +  "</div>");
	$("#p-response").show().delay(5000).fadeOut("slow");
      }
    },'json');

        
    }); 
} 



function askDeadlineExtensionAdmin(){  
	$('#extend_deadline_form_admin').bootstrapValidator({
        // Removing submitHandler option
	    icon: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
	    
	      fields: {
		  order_deadline_admin: {
	      validators: {
		  notEmpty: {
		      message: 'New date and time required.'
		  },
		  date:{
		    format:'YYYY-MM-DD h:m',
		    message:'Wrong date and time format'
		  }
	      }
	  },
	  deadline_extension_reason:{
	    validators:{
	      notEmpty:{
		message:'Please select the reason for deadline extension'
	      }
	    }
	  }
	      }
   	
    }).on('success.form.bv', function(e) {
        // Prevent form submission
        e.preventDefault();

        var $form        = $(e.target),
            validator    = $form.data('bootstrapValidator'),
            submitButton = validator.getSubmitButton();
	    
      var deadline = $('#order_deadline_admin').val() + ":00";
      var reason = $('#deadline_extension_reason').val();
      var order_code = $('#order-code-admin').text();
      var date = new Date().toString("yyyy-MM-dd HH:mm:ss");
      $("#loading-gif-extend-admin").removeClass("hidden");
      $.post("/manageorder/askfordeadlineextensionadmin/" + deadline + "/" + date + "/" + reason + "/" + order_code ,{}, function(data){
	if(data['success'] == 1){
	  $('#deadline-extension-request-response').html("<div id='de-response' class='alert alert-success'>" + data['message'] +  "</div>");
	  $("#de-response").show().delay(5000).fadeOut("slow");
	  $("#loading-gif-extend-admin").addClass("hidden");
	}else{
	  $('#deadline-extension-request-response').html("<div id='de-response' class='alert alert-danger'>" + data['message'] +  "</div>");
	  $("#de-response").show().delay(5000).fadeOut("slow");
	  $("#loading-gif-extend-admin").addClass("hidden");
	}
      },'json');

        
    }); 
  
}



function validateUploadFile(){
      $('#orderFileForm').bootstrapValidator({
	      message: 'This value is not valid',
	      live: 'disabled',
	      feedbackIcons: {
	  valid: '',
	  invalid: 'fa fa-times',
	  validating: ''
	      },
	      fields: {
		  product_file_type: {
	      validators: {
				      notEmpty: {
		      message: 'Select product type.'
		  }
	      }
	  }
	      }
      });  
}