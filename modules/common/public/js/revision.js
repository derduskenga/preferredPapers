$(document).ready(function(event){
    $('#revision-form').addClass('hidden');
    
    jQuery('#revision_deadline').datetimepicker({
      format:'Y-m-d H:i'     
    });  
    handleLinkClickAskRevision();
    //valididateForm();
    handleRevisionFormSubmit();
    //submitForm();
});


function handleLinkClickAskRevision(){
  $('#ask-for-revision-link').click(function(event){
    event.preventDefault();
    if($('#revision-form').hasClass('hidden')){
      $('#revision_instructions').focus();
      $('#revision-form').removeClass("hidden");
      $('#deadline-form').addClass('hidden');
      $('#feedback-form').addClass("hidden");
    }else{
      $('#revision-form').addClass("hidden");
    }
  });
}

function handleRevisionFormSubmit(){
	$('#ask_revision_form').bootstrapValidator({
        // Removing submitHandler option
	    icon: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
	    
	    	fields: {
		    revision_deadline:{
		      validators:{
			notEmpty:{
			  message:'Field is required'
			},
			date:{
			  message:'Wrong date format',
			  format:'YYYY-MM-DD h:m'
			}
		      }
		    },
		    revision_instructions:{
		      validators:{
			notEmpty:{
			  message:'Revision instructions required'
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
	var revision_deadline = $('#revision_deadline').val()+":00";
	var revision_instructions = $('#revision_instructions').val();
	var order_code = $('#order-code').text();
	$('#loading-gif-revision').removeClass("hidden");
	$("#ask_revision_form").data('bootstrapValidator').resetForm();
	$.post("/mydashboard/order/askforrevision/" + order_code + "/" + revision_deadline,{revision_instructions:revision_instructions}, function(data){
	  if(data['success'] == 1){
	      $('#loading-gif-revision').addClass("hidden");
	      $('#extended_deadline_response').html("<div id='d-response' class='alert alert-success'>" + data['message'] + "</div>");
	      $('#revision-form').addClass('hidden');
	      $('#ask-for-revision-link').addClass('hidden');
	      $('#fill-feedback-survey-link').addClass('hidden');
	      $("#d-response").show().delay(5000).fadeOut("slow");
	  }else{
	      $('#loading-gif-revision').addClass("hidden");
	      $('#extended_deadline_response').html("<div id='d-response' class='alert alert-danger'>" + data['message'] + "</div>");
	      $("#d-response").show().delay(5000).fadeOut("slow");
	  }
	},'json')
        
    });
}
