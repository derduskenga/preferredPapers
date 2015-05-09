$(document).ready(function(e){  
  setClientLocalTime();
  setTimeZoneCookie();
  $('#client-order-form-submit').click(function(e){
    var cpp_mode = $('#deadline_category_tracker').val();
    if(cpp_mode == "perpage"){
      validateForPerPageBasis();
    }else if(cpp_mode == "perassignment"){
      //alert(cpp_mode);
      validateForPerAssignmentBasis();
      
    }else if(cpp_mode == "perquestion"){
      //alert(cpp_mode);
      validateForPerQuestionBasis();
      
    }else{//use per page
      validateForPerPageBasis();
    }
  });  
  
    setInterval(setClientLocalTime, 5000);
  
});


function validateForPerQuestionBasis(){
  live: 'enabled',
  $('#clientorderform').bootstrapValidator({
    feedbackIcons: {
      valid: 'glyphicon glyphicon-ok',
      invalid: 'glyphicon glyphicon-remove',
      validating: 'glyphicon glyphicon-refresh'
    },
    fields: {
	f_name: {
	  validators: {
	    notEmpty: {
	    message: 'First name is required'
	    }
	  }
	},
	l_name: {
	  validators: {
	    notEmpty: {
	    message: 'Last name is required'
	    }
	  }
	},
	email:{
	  validators:{
	    notEmpty:{
	      message:'Email is required'
	    },
	    emailAddress:{
	      message:'Please enter a valid email'
	    },
	    identical:{
	      field: 'c_email',
              message: 'Email and confirm email do not match'
	    },
	  }
	},
	c_email:{
	  validators:{
	    notEmpty:{
	      message:'Please confirm your email'
	    },
	    emailAddress:{
	      message:'Email is invalid'
	    },
	    identical:{
	      field: 'email',
              message: 'Email and confirm email do not match'
	    },
	  }
	},
	client_country:{
	  validators:{
	    notEmpty:{
	      message:'Please select country'
	    }
	  }
	},
	area_code:{
	  validators:{  
	    notEmpty:{
	      message:'Enter a valid phone number'
	    },
	    digits:{
	      message:'Invalid phone number'
	    }
	  }
	},
	phone_number:{
	  validators:{
	    notEmpty:{
	      message:'Enter a valid phone number' 
	    },
	    digits:{
	      message:'Invalid phone number'
	    }
	  }
	},
	document_subject:{
	  validators:{
	    notEmpty:{
	      message:'select subject area'
	    }
	  }
	},
	topic:{
	  validators:{
	    notEmpty:{
	      message:'Please type your order topic'
	    }
	  }
	},
	order_instruction:{
	  validators:{
	    notEmpty:{
	      message:'Please type your order instructions'
	    }
	  }
	},
	number_of_units:{
	  validators:{
	    notEmpty:{
	      message:'Please select number of pages/assignments/questions'
	    }
	  }
	}
    }
  });
}

function validateForPerAssignmentBasis(){
  live: 'enabled',
  $('#clientorderform').bootstrapValidator({
    feedbackIcons: {
      valid: 'glyphicon glyphicon-ok',
      invalid: 'glyphicon glyphicon-remove',
      validating: 'glyphicon glyphicon-refresh'
    },
    fields: {
	f_name: {
	  validators: {
	    notEmpty: {
	    message: 'First name is required'
	    }
	  }
	},
	l_name: {
	  validators: {
	    notEmpty: {
	    message: 'Last name is required'
	    }
	  }
	},
	email:{
	  validators:{
	    notEmpty:{
	      message:'Email is required'
	    },
	    emailAddress:{
	      message:'Please enter a valid email'
	    },
	    identical:{
	      field: 'c_email',
              message: 'Email and confirm email do not match'
	    },
	  }
	},
	c_email:{
	  validators:{
	    notEmpty:{
	      message:'Please confirm your email'
	    },
	    emailAddress:{
	      message:'Email is invalid'
	    },
	    identical:{
	      field: 'email',
              message: 'Email and confirm email do not match'
	    },
	  }
	},
	client_country:{
	  validators:{
	    notEmpty:{
	      message:'Please select country'
	    }
	  }
	},
	area_code:{
	  validators:{  
	    notEmpty:{
	      message:'Enter a valid phone number'
	    },
	    digits:{
	      message:'Invalid phone number'
	    }
	  }
	},
	phone_number:{
	  validators:{
	    notEmpty:{
	      message:'Enter a valid phone number' 
	    },
	    digits:{
	      message:'Invalid phone number'
	    }
	  }
	},
	topic:{
	  validators:{
	    notEmpty:{
	      message:'Please type your order topic'
	    }
	  }
	},
	order_instruction:{
	  validators:{
	    notEmpty:{
	      message:'Please type your order instructions'
	    }
	  }
	},
	number_of_units:{
	  validators:{
	    notEmpty:{
	      message:'Please select number of pages/assignments/questions'
	    }
	  }
	},
	operating_system:{
	  validators:{
	    notEmpty:{
	      message:'Please select operating system (OS)'
	    }
	  }
	},
	prog_language:{
	  validators:{
	    notEmpty:{
	      message:'Please select programming language'
	    }
	  }
	},
	database:{
	  validators:{
	    notEmpty:{
	      message:'Please select database'
	    }
	  }
	}
    }
  });
}

function validateForPerPageBasis(){
  live: 'enabled',
  $('#clientorderform').bootstrapValidator({
    feedbackIcons: {
      valid: 'glyphicon glyphicon-ok',
      invalid: 'glyphicon glyphicon-remove',
      validating: 'glyphicon glyphicon-refresh'
    },
    fields: {
	f_name: {
	  validators: {
	    notEmpty: {
	    message: 'First name is required'
	    }
	  }
	},
	l_name: {
	  validators: {
	    notEmpty: {
	    message: 'Last name is required'
	    }
	  }
	},
	email:{
	  validators:{
	    notEmpty:{
	      message:'Email is required'
	    },
	    emailAddress:{
	      message:'Please enter a valid email'
	    },
	    identical:{
	      field: 'c_email',
              message: 'Email and confirm email do not match'
	    },
	  }
	},
	c_email:{
	  validators:{
	    notEmpty:{
	      message:'Please confirm your email'
	    },
	    emailAddress:{
	      message:'Email is invalid'
	    },
	    identical:{
	      field: 'email',
              message: 'Email and confirm email do not match'
	    },
	  }
	},
	client_country:{
	  validators:{
	    notEmpty:{
	      message:'Please select country'
	    }
	  }
	},
	area_code:{
	  validators:{  
	    notEmpty:{
	      message:'Enter a valid phone number'
	    },
	    digits:{
	      message:'Invalid phone number'
	    }
	  }
	},
	phone_number:{
	  validators:{
	    notEmpty:{
	      message:'Enter a valid phone number' 
	    },
	    digits:{
	      message:'Invalid phone number'
	    }
	  }
	},
	document_subject:{
	  validators:{
	    notEmpty:{
	      message:'select subject area'
	    }
	  }
	},
	topic:{
	  validators:{
	    notEmpty:{
	      message:'Please type your order topic'
	    }
	  }
	},
	order_instruction:{
	  validators:{
	    notEmpty:{
	      message:'Please type your order instructions'
	    }
	  }
	},
	number_of_units:{
	  validators:{
	    notEmpty:{
	      message:'Please select number of pages/assignments/questions'
	    }
	  }
	}
    }
  });
}


function setTimeZoneCookie(){
    var order_date = new Date();
    var timezone_offset = order_date.getTimezoneOffset();
    
    $("#client_time_zone_offset").val(timezone_offset);
}

function setClientLocalTime(){
  $("#client_local_time").val(new Date().toString("yyyy-MM-dd HH:mm:ss"));
  //alert(moment().format("YYYY-MM-DD HH:mm:ss"));
  //alert(new Date().getTime());
}