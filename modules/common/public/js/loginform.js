$(document).ready(function(e){
		$('#loginform').bootstrapValidator({
		message: 'This value is not valid',
        live: 'disabled',
		feedbackIcons: {
            valid: '',
            invalid: 'fa fa-times',
            validating: ''
		  },
		fields: {
		    email: {
                validators: {
					notEmpty: {
                        message: 'Enter your email.'
                    },
					emailAddress:{
						message: 'Enter a valid email.'
					}
                }
            },
           password: {
                validators: {
					notEmpty: {
                        message: 'Enter your password.'
                    }
                }
            }	
		}
   });	
});