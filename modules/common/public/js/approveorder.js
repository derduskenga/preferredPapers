$(document).ready(function(){
  approveOrderProduct();
});

function approveOrderProduct(){
  $('.approve-order-class').click(function(event){
    event.preventDefault();
    var id = event.target.id;
    var theArray = id.split("-");
    var order_code = theArray[2];
      $.post("/mydashboard/order/approveorder/"+ order_code,{tag:"tag"}, function(data){
// 	  if(data['success'] == 1){
// 	    $('#' + id).html("<i class='fa fa-thumbs-up'></i> You have approved Order Product");
// 	    $('#' + id).attr('disabled');
// 	  }
      },'json');
    
    
  });
}