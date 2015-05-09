$(document).ready(function(event){
  $('#deadline-form').addClass('hidden');
  jQuery('#datetimepicker').datetimepicker({
    format:'Y-m-d H:i'
  });
  validateDeadlineExtension();
  //handleFormSubmit();
  handleLinkClickDeadline();
});

function handleLinkClickDeadline(){
  $('#extend-deadline-link').click(function(event){
    event.preventDefault();    
    if($('#deadline-form').hasClass('hidden')){
      $('#deadline-form').removeClass('hidden');
      $('#revision-form').addClass("hidden");
      $('#feedback-form').addClass("hidden");
    }else{
      $('#deadline-form').addClass('hidden');
    }
  });
}


function validateDeadlineExtension(){
	$('#extend_deadline_form').bootstrapValidator({
        // Removing submitHandler option
	    icon: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
	    
		fields:{
	  datetimepicker:{
	    validators:{
	      notEmpty:{
		message:'Field is required'
	      },
	      date:{
		message:'Wrong date format',
		format:'YYYY-MM-DD h:m'
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
	    
      var extended_deadline = $("#datetimepicker").val();
      var order_code = $('#order-code').text();
      $("#extend_deadline_form").data('bootstrapValidator').resetForm();
      $('#loading-gif-extend').removeClass("hidden");
      $.post("/mydashboard/order/volunteerdeadlineextension/" + extended_deadline + "/" +order_code,{tag:"tag"}, function(data){
	if(data['success'] == 1){
	  $('#loading-gif-extend').addClass("hidden");
	  $('#extended_deadline_response').html("<div id='d-response' class='alert alert-success'>" + data['message'] + "</div>");
	  $('#deadline-form').addClass('hidden');
	  $("#d-response").show().delay(5000).fadeOut("slow");
	}else{
	  $('#loading-gif-revision').addClass("hidden");
	  $('#extended_deadline_response').html("<div id='d-response' class='alert alert-danger'>" + data['message'] + "</div>");
	  $("#d-response").show().delay(5000).fadeOut("slow");
	}
      },'json')

        
    });
  }