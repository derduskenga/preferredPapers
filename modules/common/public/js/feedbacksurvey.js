$(document).ready(function(event){
  $('#feedback-form').addClass('hidden');
  handleSurveyLinkClick();
  validateSurveyForm();
  //handleSurveyFormSubmit();
});

function handleSurveyLinkClick(){
  $('#fill-feedback-survey-link').click(function(event){
    event.preventDefault();
    
    if($('#feedback-form').hasClass("hidden")){
      $('#feedback-form').removeClass("hidden");
      $('#revision-form').addClass("hidden");
      $('#deadline-form').addClass('hidden');
    }else{
      $('#feedback-form').addClass("hidden");
    }
  });
}

function handleSurveyFormSubmit(){
  $(function(){
    $("#survey_feedback_form").submit(function(event) {       
      event.preventDefault();
      var rating = $('[name=order_survey]:checked').val();
      var order_code = $('#order-code').text();
      $('#loading-gif-feedback').removeClass("hidden");
      $("#survey_feedback_form").data('bootstrapValidator').resetForm();      
      $.post("/mydashboard/order/surveyfeedback/" + order_code + "/" + rating,{}, function(data){
	if(data['success'] == 1){
	  $('#loading-gif-feedback').addClass("hidden");
	  $('#extended_deadline_response').html("<div id='d-response' class='alert alert-success'>" + data['message'] + "</div>");
	  $('#feedback-form').addClass('hidden');
	  $("#d-response").show().delay(5000).fadeOut("slow");
	}else{
	  $('#loading-gif-feedback').addClass("hidden");
	  $('#extended_deadline_response').html("<div id='d-response' class='alert alert-danger'>" + data['message'] + "</div>");
	  $("#d-response").show().delay(5000).fadeOut("slow");
	}
      },'json');
      
      
    });
  });
}


function validateSurveyForm(){
	$('#survey_feedback_form').bootstrapValidator({
        // Removing submitHandler option
	    icon: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
	    
	      fields:{
	order_survey:{
	  validators:{
	    notEmpty:{
	      message:'Please select one of the options'
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
	    
      var rating = $('[name=order_survey]:checked').val();
      var order_code = $('#order-code').text();
      $('#loading-gif-feedback').removeClass("hidden");
      $("#survey_feedback_form").data('bootstrapValidator').resetForm();      
      $.post("/mydashboard/order/surveyfeedback/" + order_code + "/" + rating,{}, function(data){
	if(data['success'] == 1){
	  $('#loading-gif-feedback').addClass("hidden");
	  $('#extended_deadline_response').html("<div id='d-response' class='alert alert-success'>" + data['message'] + "</div>");
	  $('#feedback-form').addClass('hidden');
	  $("#d-response").show().delay(5000).fadeOut("slow");
	}else{
	  $('#loading-gif-feedback').addClass("hidden");
	  $('#extended_deadline_response').html("<div id='d-response' class='alert alert-danger'>" + data['message'] + "</div>");
	  $("#d-response").show().delay(5000).fadeOut("slow");
	}
      },'json');

        
    });
}