$(document).ready(function(){
	
	$('#client-messages-form').bootstrapValidator({
		message: 'This value is not valid',
        live: 'disabled',
		feedbackIcons: {
            valid: '',
            invalid: 'fa fa-times',
            validating: ''
		},
		fields: {
		    msg_to: {
                validators: {
					notEmpty: {
                        message: 'Please select message receipient.'
                    }
                }
            },
           message: {
                validators: {
					notEmpty: {
                        message: 'Please type your message.'
                    }
                }
            }	
		}
	});

	markMessages();
	
});

function markMessages(){
	 $(".msg-to-support.unread").each(function(){ 
      var elemid = $(this).attr('id');
      $(this).waypoint(function(direction){
					var elementArray = elemid.split('-');
					var order_code = elementArray[0]; //to or from
					var msg_id = elementArray[1];
			
						$.post("/manageorder/messages/markread/"+msg_id,{}, function(data){
						if(data['success'] == 1){
						//remove the unread class
						$("#" + elemid + "").removeClass("unread").addClass("read");
						}
						},'json');
      }, {
			offset:'75%'
      });
});
}

function alternateNewMessageView(panel){
		$('.messages-action').hide(1000);
		$('.new-msg-div').hide(1000);
		if(panel=="msg_action"){
			//$('.messages-action').removeClass('hidden');
			$(".messages-action").show(1000);
		}else{
			//$('.new-msg-div').removeClass('hidden');
			$('.new-msg-div').show(1000);
		}
}