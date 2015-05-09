$(document).ready(function(e){
  extendDeadlineActions();
  additionalPagesActions();
  setInterval(setClientLocalTimeField, 5000);
});


function additionalPagesActions(){
  $('#order-messages').on('click','.additional-pages-shared-class',function(event){
    //additional-pages-accept-30001-121
    var element_id = event.target.id;
    var eArray = element_id.split('-');
    var action = eArray[2];
    var order_code = eArray[3];
    var messages_id = eArray[4];
    var status = false;
    var date = new Date().toString("yyyy-MM-dd HH:mm:ss");
    if(action == "accept"){
      status = true;
    }
    
    $.post("/mydashboard/order/messages/messagesactionpages/" + status + "/" + date + "/" + messages_id + "/" + order_code ,{}, function(data){
      if(data['success'] == 1){
	$('.additional-pages-shared-class').prop('disabled',true); 
	$('.additional-pages-shared-class').attr('style','color: #ccc;');
	//attr("width","500"); style="color: #ccc;" prop('disabled', true);
      }else{
	alert("An error occured. Please try again");
      }
    },'json');
    
  });
}


function extendDeadlineActions(){
  $('#order-messages').on('click','.deadline-extension-shared-class',function(event){
    //deadline-extension-accept-30001-101
    var element_id = event.target.id;
    var eArray = element_id.split('-');
    var action = eArray[2];
    var order_code = eArray[3];
    var messages_id = eArray[4];
    var status = false;
    var date = new Date().getTime();
    if(action == "accept"){
      status = true;
    }
    $.post("/mydashboard/order/messages/messagesactionextenddeadline/" + status + "/" + date + "/" + messages_id + "/" + order_code ,{}, function(data){
      if(data['success'] == 1){
	$('.deadline-extension-shared-class').prop('disabled',true); 
	$('.deadline-extension-shared-class').attr('style','color: #ccc;');
	//attr("width","500"); style="color: #ccc;" prop('disabled', true);
      }else{
	alert("An error occured. Please try again");
      }
    },'json');
    
  });
}

function setClientLocalTimeField(){
  $('#client_local_time').val(new Date().toString("yyyy-MM-dd HH:mm:ss"));
}


