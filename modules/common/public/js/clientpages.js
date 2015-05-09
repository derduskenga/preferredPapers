$(document).ready(function(){
		//$('.client-friend-mail-invitation-div').hide();
});

function alternateInvationMailView(panel){	
		$('.client-friend-mail-invitation-div').hide(1000);
		$('.email-invitation-div').hide(1000);
		if(panel=="btn_action"){
			//$('.messages-action').removeClass('hidden');
			$(".client-friend-mail-invitation-div").show(1000);
		}else{
			//$('.new-msg-div').removeClass('hidden');
			$('.email-invitation-div').show(1000);
		}
} 

function alternateAddWriterView(btn){
		$('.add-preferred-writer-div').hide(1000);
		$('.add-preferred-writer-form-div').hide(1000);
		if(btn=="add_btn"){
			$(".add-preferred-writer-form-div").show(1000);
		}else{
			$('.add-preferred-writer-div').show(1000);
		}
}