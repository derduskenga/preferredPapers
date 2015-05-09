$(document).ready(function(){
  $('#change-password').popover({
    html : true,
    trigger: 'click'
  });
  
  $('#change-password').click(function(event){
    event.preventDefault();
  });
});

function validateChangePasswordForm(){
	    $('#change_password_form').bootstrapValidator({
		    message: 'This value is not valid',
		    live: 'enabled',
		    
		    feedbackIcons: {
			valid: 'fa fa-check',
			invalid: 'fa fa-times',
			validating: 'fa fa-refresh'
		    },
	fields:{
  r_password:{
    validators:{
      notEmpty:{
	message:'Current password is required'
      }
    }
  },
  new_password:{
    validators:{
      notEmpty:{
	message:'New password is required'
      },
      identical:{
	field: 'confirm_password',
	message: 'New password and its confirm do not match'
      }
    }
  },
    confirm_password:{
    validators:{
      notEmpty:{
	message:'Confirm new password'
      },
      identical:{
	field: 'new_password',
	message: 'New password and its confirm do not match'
      }
    }
  }  
}
});
}

function handleChangePasswordFormSubmit(){
  $('#change-password-btn').click(function(event){
    event.preventDefault();
    var current_password = $('#r_password').val();
    var new_password = $('#new_password').val();
    $.post("/mydashboard/profile/changepassword",{current_password:current_password,new_password:new_password}, function(data){
      
    },'json');
    
  });
}
