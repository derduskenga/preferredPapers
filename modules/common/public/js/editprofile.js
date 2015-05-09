$(document).ready(function(event){
  handleEditProfileButton();
});

function handleEditProfileButton(){
  $('#edit-my-profile-btn').click(function(event){
    if($('#profile-edit-div').hasClass("hidden")){
      $('#profile-edit-div').removeClass("hidden");
      $('#edit-my-profile-btn').html("Cancel editing");
      $('#my-complete-profile').addClass("hidden");
    }else{
      $('#profile-edit-div').addClass("hidden");
      $('#edit-my-profile-btn').html("Edit Profile");
      $('#my-complete-profile').removeClass("hidden");
    }
  })
}