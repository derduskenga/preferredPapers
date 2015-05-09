$(document).ready(function(e){  
	validateSearchOrderForm();
});


function validateSearchOrderForm(){
  live: 'enabled',
  $('#admin_search_order_form').bootstrapValidator({
    feedbackIcons: {
      valid: 'glyphicon glyphicon-ok',
      invalid: 'glyphicon glyphicon-remove',
      validating: 'glyphicon glyphicon-refresh'
    },
    fields:{
	sought_order_code:{
	  validators:{
	    notEmpty:{
	    message: ''
	    },
	    digits:{
	      message:''
	    }
	  }
	}
    }
  });
}