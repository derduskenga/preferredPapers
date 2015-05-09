$(document).ready(function(){
	bindNewPassForm();
});
function bindNewPassForm(){
	$('#password-reset-form').bootstrapValidator({
		    message: 'This value is not valid',
			live:'disabled',
		    feedbackIcons: {
			valid: 'fa fa-check',
			invalid: 'fa fa-times',
			validating: 'fa fa-refresh'
		    },
			fields:{
					password1:{
						validators:{
							notEmpty:{
								message:'Password is required.'
							},
							stringLength: {
								min: 5,
								message: 'Password must be at least 5 characters long.'
							}
						}
					},
					password2:{
						validators:{
								notEmpty:{
										message:'Confirm password is required.'
								},
								identical:{
									field: 'password1',
									message: 'New password and its confirm do not match'
								}
						}
					}
				}
	});
}
