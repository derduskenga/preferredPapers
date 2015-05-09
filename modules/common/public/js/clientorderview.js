$(document).ready(function(e){
  handleDownloadClick();
  handleProductFileDownload();
  setInterval(setClientLocalTime, 5000);
  approveOrderProduct();
});

function handleDownloadClick(){
  $('.file-shared-class').click(function(e){
    e.preventDefault();
    var file_id = this.id.split("-")[2];
    window.location.href = "/mydashboard/order/file/downloadorderfile/" + file_id;
  });
}

function handleProductFileDownload(){
      $('.product-file-shared-class').click(function(e){
	e.preventDefault();
	var file_id = this.id.split("-")[2];
	var date = new Date().toString("yyyy-MM-dd HH:mm:ss");
	window.location.href = "/mydashboard/order/file/downloadproductfile/" + file_id + "/" + date;
      });
}

function setClientLocalTime(){
	$('#file_upload_local_time').val(new Date().toString("yyyy-MM-dd HH:mm:ss"));
}


function approveOrderProduct(){
  $('.approve-order-class').click(function(event){
    event.preventDefault();
    var id = event.target.id;
    var theArray = id.split("-");
    var order_code = theArray[2];
      $.post("/mydashboard/order/approveorder/"+ order_code,{tag:"tag"}, function(data){
	  if(data['success'] == 1){
	    $('#' + id).html("<i class='fa fa-thumbs-up'></i> You have approved Order Product");
	    $('#' + id).attr("disabled", "disabled");
	  }
      },'json');
    
    
  });
}
