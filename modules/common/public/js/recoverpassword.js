$(document).ready(function(event){
  valididateRecoverPasswordForm();
    
      $(function() {
	$(window).scroll(sticky_relocate);	
	if($('#sticky-anchor').length != 0){
	  sticky_relocate();
	}
      });
  
});


function valididateRecoverPasswordForm(){
	    $('#recover_password_form').bootstrapValidator({
		    message: 'This value is not valid',
		    live: 'enabled',
		    
		    feedbackIcons: {
			valid: 'fa fa-check',
			invalid: 'fa fa-times',
			validating: 'fa fa-refresh'
		    },
	fields:{
  recover_password_email:{
    validators:{
      notEmpty:{
	message:'Field is required'
      },
      emailAddress:{
	message:'Enter a valid email'
      }
    }
  }
}
});
}


function sticky_relocate(){
    if($('#sticky-anchor').length != 0){
	var window_top = $(window).scrollTop();
	var div_top = $('#sticky-anchor').offset().top;
	if (window_top > div_top) {
	    $('#sticky').addClass('stick');
	} else {
	    $('#sticky').removeClass('stick');
	}
    }
}
