$(document).ready(function(){
  preventDefaultOnLinkClick();
});

function preventDefaultOnLinkClick(){
  $('.where-i-go-from-here').click(function(event){
      event.preventDefault();
  });
}