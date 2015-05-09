$.coupon_global_vars = {};
$(document).ready(function(e){
	fetchClientCouponCodes();
	fetchAdminCouponCodes();
 
	if($('#order-info').length !=0){
		$("#coupon_code").on("propertychange change keyup paste input", function(){
			var coupon_code_value = $.trim($("#coupon_code").val());
			var found = false;
			for(var i=0; i<$.coupon_global_vars.client_coupons.length; i++){
				if(coupon_code_value == $.coupon_global_vars.client_coupons[i]['code']){
					found = true;
				}
			}
			
			if(found == false){/*if it was not found in the first array*/
				for(var i=0; i<$.coupon_global_vars.admin_coupons.length; i++){
					if(coupon_code_value == $.coupon_global_vars.admin_coupons[i]['code']){
						found =true;
					}
				}
			}
			
			if(found == true){
				//tell the user that the code is collect
				$('#coupon-code-message').html("<p class='alert-success'>The coupon code is correct.</p>");
			}else{
				$('#coupon-code-message').html("<p class='alert-danger'>The coupon code is wrong. Please check it to confirm</p>"); 
			}
		});
	}
});

function fetchClientCouponCodes(){
    $.post("/fetch/clientcoupons",{}, function(data){
	$.coupon_global_vars.client_coupons = data;
    },'json'); 
}

function fetchAdminCouponCodes(){
    $.post("/fetch/admincoupons",{}, function(data){
	$.coupon_global_vars.admin_coupons = data;
    },'json');
}