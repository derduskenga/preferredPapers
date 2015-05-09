$.order_global_vars = {};
$(document).ready(function(e){
  //global variable 
  detectCountrySelect();
  
  detectDocumentSelect();
  
  fetchSpacing();
  detectSpacingChange();
  
  setDocument();
  
  fetchCurrency(); 
  detectCurrencyChange();
  
  fetchAdditions();
  detectAdditionsClick();  
  
  detectSubjectChange();
  
  detectUrgencyChange();
  
  fetchLevelOfWriting();
  detectLevelOfWritingChange();
  
  detectNumberOfUnitsChange();
  
});

function detectCountrySelect(){
  $('#client_country').on('change',function(e){
    var selectedText = $('#client_country option:selected').text();
    var component = selectedText.split('-');
    var size = component.length;
    var code = component[size-1];
    $('#country_code').val(code);    
  });
}

function setDocument(){
  if($('#order-additions').length != 0){  
    fectDocument($('#document_type').val(),"xx");
  }
}

function detectDocumentSelect(){
  $('#document_type').on('change',function(e){
    setSpacingToDouble();
    //reset boostrap validation
    //$('#clientorderform').data('bootstrapValidator').resetForm(true);
    //var validator = $('#clientorderform').validate();
    //validator.resetForm();
    if($('#order-additions').length != 0){
      var selectedValue = $('#document_type').val();
      fectDocument(selectedValue,"documentChange");
    }
  });
}

function fectDocument(documentID,type){
  var newDataRequest = $.ajax({
    type: "POST",
    url: "/fetch/orderdocument/"+documentID,
    timeout: 30000, // timeout after 30 seconds
    dataType: "json",
  });
  newDataRequest.done(function(data){
    if(type == "documentChange"){
      handleDocumentTypeChange(data);
      initializeVarsForDocument(data);  
    }else{
      //initial document load order total updating
      updateInitialPrice(data);
      updateFieldVisibility(data);
      initializeVarsForDocument(data);
    }    
  });
  
  newDataRequest.fail(function(jqXHR, exception){
    if (jqXHR.status === 0){
	    alert('Sorry, could not establishing a network connection.');
    }else if (jqXHR.status == 404){
	    alert('Requested page not found. [404]');
    }else if (jqXHR.status == 500){
	    alert('Internal Server Error [500].');
    }else if (exception === 'parsererror'){
	    alert('Requested JSON parse failed...');
    }else if (exception === 'timeout'){
	    alert('Time out error.');
    }else if(exception === 'abort'){
	    alert('Sorry, Request was aborted.');
    }
  });
  
}

function initializeVarsForDocument (data){
    $.order_global_vars.document_details = data;
    $.order_global_vars.count_units = data['count_units'];
    $.order_global_vars.document_subjects = data['document_subjects'];
    $.order_global_vars.document_deadlines = data['document_deadlines'];
}

function handleDocumentTypeChange(data){
  $.order_global_vars.selected_units = $('#number_of_units').val();
  if(data != null){
    initializeVarsForDocument (data);
    //alert(data['deadline_category']);
    $('#deadline_category_tracker').val(data['cpp_mode']);
    //alert("value has been set here " + $('#deadline_category_tracker').val());
    $("#document_subject").attr('disabled', 'disabled');
    $("#document_deadline").attr('disabled', 'disabled');
    if(data['cpp_mode'] == "perassignment"){      
      //hide the subject select box and set some default value
      //remove hidden class from os-proglanguage-databade id
      //hide spacinng region
      $("#order-subject-area").addClass("hidden");
      //$("#order-subject-area").val("5");
      $('#spacing-area').addClass("hidden");
      $('#os-language-database').removeClass("hidden");//values must be set to satisfy validation
      $("#document_deadline").removeAttr('disabled');

      //set fetched deadlines
      var deadlinesStr = "";
      var deadlines = data['document_deadlines'];
      //deadlines = sortArray(deadlines,"desc");
      for(var i = 0; i<deadlines.length;i++){
	deadlinesStr = deadlinesStr + "<option id='" + deadlines[i]['i'] + "' value='" + deadlines[i]['time_in_seconds'] + "'>" + deadlines[i]['deadline_labels'] + "</option>"
      }
      $("#document_deadline").html(deadlinesStr);
      $("#document_deadline").removeAttr('disabled');       
    }else if (data['cpp_mode'] == "perpage"){
      $("#order-subject-area").removeClass("hidden");      
      $('#spacing-area').removeClass("hidden");
      $('#os-language-database').addClass("hidden");
      
      
      //set fetched deadlines
      var deadlinesStr = "";
      var deadlines = data['document_deadlines'];
      for(var i = 0; i<deadlines.length;i++){
	deadlinesStr = deadlinesStr + "<option id='" + deadlines[i]['id'] + "' value='" + deadlines[i]['time_in_seconds'] + "'>" + deadlines[i]['deadline_labels'] + "</option>"
      }
      $("#document_deadline").html(deadlinesStr);
      $("#document_deadline").removeAttr('disabled');
      //set fetched subjects
      var docSubjectsStr = "<option class='' value=''>--select subject--</option>";
      var subjects = data['document_subjects'];
      for(var j = 0; j<subjects.length; j++){
	docSubjectsStr = docSubjectsStr + "<option id='" + subjects[j]['id'] + "' value='" + subjects[j]['id'] + "'>" + subjects[j]['subject_name'] + "</option>"
      }
      $('#document_subject').html(docSubjectsStr);
      $('#document_subject').removeAttr('disabled');
      
    }else{//per question
      $('#spacing-area').addClass("hidden");   
      $("#order-subject-area").removeClass("hidden");      
      $('#os-language-database').addClass("hidden");
      
      
      //set fetched deadlines
      var deadlinesStr = "";
      var deadlines = data['document_deadlines'];
      for(var i = 0; i<deadlines.length;i++){
	deadlinesStr = deadlinesStr + "<option id='" + deadlines[i]['id'] + "' value='" + deadlines[i]['time_in_seconds'] + "'>" + deadlines[i]['deadline_labels'] + "</option>"
      }
      $("#document_deadline").html(deadlinesStr);
      $("#document_deadline").removeAttr('disabled');
      //set fetched subjects
      var docSubjectsStr = "<option class='' value=''>--select subject--</option>";
      var subjects = data['document_subjects'];
      for(var j = 0; j<subjects.length; j++){
	docSubjectsStr = docSubjectsStr + "<option id='" + subjects[j]['id'] + "' value='" + subjects[j]['id'] + "'>" + subjects[j]['subject_name'] + "</option>"
      }
      $('#document_subject').html(docSubjectsStr);
      $('#document_subject').removeAttr('disabled');
    }
    
    //append number of units
    var number_of_units = "<option class='blank' value=''>--select--</option>";
    var order_units = data['count_units'];
    for(var k = 0; k<order_units.length; k++){
      if(k+1 == $.order_global_vars.selected_units){
	number_of_units = number_of_units + "<option id='" + order_units[k]['value'] + "' value='" + order_units[k]['value'] + "' selected>" + order_units[k]['text'] + "</option>";
      }else{
	number_of_units = number_of_units + "<option id='" + order_units[k]['value'] + "' value='" + order_units[k]['value'] + "'>" + order_units[k]['text'] + "</option>";
      }
      
    }
    $('#number_of_units').html(number_of_units);
    $('#number_of_units_label').html(order_units[1]['label']);
  }
  //updateAditionsValues();
  updatePriceLabel();
  updateAditionsValues();
}

function updateFieldVisibility(data){
  if(data['cpp_mode'] == "perassignment"){
      $("#order-subject-area").addClass("hidden");
      $('#spacing-area').addClass("hidden");
      $('#os-language-database').removeClass("hidden");//values must be set to satisfy validation
  }else{
      $("#order-subject-area").removeClass("hidden");      
      $('#spacing-area').removeClass("hidden");
      $('#os-language-database').addClass("hidden");
  }
}

function ifHasClass(selector,theClass){
  if(('#' + selector).hasClass("" + theClass + "")){
    return true;
  }
  return false;
}

// function initialPriceLabel(){
//   fectDocument($('#document_type').val(),"initialpriceupdate");
// }

function updateInitialPrice(data){
  $.order_global_vars.document_details = data;
  $.order_global_vars.count_units = data['count_units'];
  $.order_global_vars.document_subjects = data['document_subjects'];
  $.order_global_vars.document_deadlines = data['document_deadlines'];
  $('#deadline_category_tracker').val(data['cpp_mode']);
  var per_unit_label = "";
  var cppMode = data['cpp_mode'];
  if(cppMode == "perassignment"){
    per_unit_label = "Cost per assignment";
  }else if(cppMode == "perpage"){
    per_unit_label = "Cost per page";
  }else{//for perquestion
    per_unit_label = "Cost per question";
  }  
  //update the price region
  $('#per-unit-label').html(per_unit_label);
  $('#per-unit-value').html("$" + data['base_price']);
  //$('#order-total-value').html("$" + data['base_price']);
  //$('#order-total-value').val("$" + data['base_price']);
  
  //assi
}

function fetchSpacing(){
  $.post("/fetch/spacing",{}, function(data){
      $.order_global_vars.spacing_details = data;
  },'json'); 
}

function detectSpacingChange(){
  $('.spacing-shared-class').click(function(e){
    var cppMode = $('#deadline_category_tracker').val();
    getSelectedCurrency();
    
    if(cppMode == "perpage"){
      if(this.id == "spacing-1"){
	//double spacing is selected
	//get and store the currently selected page count
	var selectedVal = $('#number_of_units').val();
	var numberOfUnitsStr = "";
	if(selectedVal == ""){
	  numberOfUnitsStr = "<option class='blank' value='' selected>--select--</option>";
	}else{
	  numberOfUnitsStr = "<option class='blank' value=''>--select--</option>";
	}	
	var optionProperty = "";
	for(var i = 1; i<=200; i++){
	  if(selectedVal == i + ""){
	    optionProperty="selected";
	  }else{
	    optionProperty = "c='c'";
	  }
	   numberOfUnitsStr = numberOfUnitsStr + "<option id='" + i + "' value='" + i + "' " + optionProperty + ">" + i + " pages/" + i*280 + " words</option>";
	}
	$('#number_of_units').html(numberOfUnitsStr);
	//do stuuf and finally call costing function
      }else if(this.id=="spacing-2"){
	//alert(this.id);
	//single spacing is selected
	//get and store the currently selected page count 
	var selectedVal = $('#number_of_units').val();
	//alert(selectedVal);
	var numberOfUnitsStr = "";
	if(selectedVal == ""){
	  numberOfUnitsStr = "<option class='blank' value='' selected>--select--</option>";
	}else{
	  numberOfUnitsStr = "<option class='blank' value=''>--select--</option>";
	}
	//alert(numberOfUnitsStr);
	var optionProperty = "";
	for(var i = 1; i<=200; i++){
	  if(selectedVal == i + ""){
	    optionProperty="selected";
	  }else{
	    optionProperty = "c='c'";
	  }
	   numberOfUnitsStr = numberOfUnitsStr + "<option id='" + i + "' value='" + i + "' " + optionProperty + ">" + i + " pages/" + i*560 + " words</option>";
	}
	$('#number_of_units').html(numberOfUnitsStr);
	//do stuff and finally call the costing function
      }
      updateAditionsValues();
      updatePriceLabel();
    }
    
  });  
}

function setSpacingToDouble(){
  $('#spacing-1').prop("checked", true);
}

function getSpacingSelected(){
  var spacingSelected = $('input[name=page_spacing]:checked', '#clientorderform').val();
  for(var i=0; i<$.order_global_vars.spacing_details.length;i++){
    if(spacingSelected == $.order_global_vars.spacing_details[i]['id']){
      $.order_global_vars.selected_spacing= $.order_global_vars.spacing_details[i];
    }
  }
  
}
function updatePriceLabel(){
  /*It is called when events that affect price change
  *the events include
  * 1. Document type change
  * 2. Subject change
  * 3. Urgency chance
  * 4. Level of writing change
  * 5. Number of order units (pages/assignments/questions)
  * 6. Order additions===============================================
  * 7. Currency selected
  * 8. spacing for perpage orders
  */
  
  var cpp_mode = $.order_global_vars.document_details['cpp_mode'];
  
  var document_base_price = $.order_global_vars.document_details['base_price'];
  var subject_additional_price = getSubjectAdditionalPrice();
  var deadline_additional_price = getDeadlineAdditionalPrice();
  var level_of_writing_additional_price = getLevelOfWritingAdditionalPrice();
  var number_of_units = getSelectedUnits();
  var additions_factor = $.order_global_vars.document_details['additions_factor'];
  //alert("deadline_additional_price " + deadline_additional_price);
  //currency 
  getSelectedCurrency();    
  var currency_id = $.order_global_vars.selected_currency['id'];
  var convertion_rate = $.order_global_vars.selected_currency['convertion_rate'];
  var currency_symbol = $.order_global_vars.selected_currency['currency_symbol'];
  var currency_symbol_2 = $.order_global_vars.selected_currency['currency_symbol_2'];
  
  //spacing
  getSpacingSelected();
  
  var spacing_factor = $.order_global_vars.selected_spacing['factor'];
  //additions
  var total_additions = getAdditions();
  //alert("total_additions " + total_additions);
  if(cpp_mode == "perpage"){   
    //alert(cpp_mode);
  var cost_per_page = ((document_base_price + subject_additional_price + deadline_additional_price + level_of_writing_additional_price)*convertion_rate)*spacing_factor;
  var order_total = cost_per_page*number_of_units + (total_additions*additions_factor*number_of_units*convertion_rate*spacing_factor);
  var per_unit_label = "Cost per page";
  updateLabel(cost_per_page,order_total,per_unit_label,currency_symbol_2);
  
  }else if(cpp_mode == "perquestion"){
    //no spacing factor applied here
    //alert(cpp_mode);
    var cost_per_question = ((document_base_price + subject_additional_price + deadline_additional_price + level_of_writing_additional_price)*convertion_rate);
    var order_total  = cost_per_question*number_of_units + (total_additions*additions_factor*number_of_units*convertion_rate);
    
    var per_unit_label = "Cost per problem/question";
    updateLabel(cost_per_question,order_total,per_unit_label,currency_symbol_2);
  }else if(cpp_mode == "perassignment"){
    //for programming
    //alert(cpp_mode);
    var cost_per_assignment = ((document_base_price + deadline_additional_price + level_of_writing_additional_price)*convertion_rate);
    var order_total = cost_per_assignment*number_of_units + (total_additions*additions_factor*number_of_units*convertion_rate);
    var per_unit_label = "Cost per assignment";
    updateLabel(cost_per_assignment,order_total,per_unit_label,currency_symbol_2);
  }
}

function updateLabel(cost_per_page,order_total,per_unit_label,currency_symbol_2){
  //update the price region
  //$('#per-unit-label').html(per_unit_label);
  //$('#per-unit-value').html(currency_symbol_2 + "" + cost_per_page.toFixed(2));
  $('#order-total-label').html("Order Total " + currency_symbol_2 + "");
  $('#order_total').val(order_total.toFixed(2));  
}

function getAdditions(){
  var additions_size = $.order_global_vars.additions_details.length;  
  var additional_price = 0;  
  for(var i=1; i<=additions_size; i++){
    if($('#additions-' + i).is(":checked")){
      for(var j=0;j<additions_size;j++){
	if(i == $.order_global_vars.additions_details[j]['id']){
	  additional_price = additional_price + $.order_global_vars.additions_details[j]['additional_price'];
	}
      }
    }
  }
  return additional_price; 
}

function detectCurrencyChange(){
  $('.currency-shared-class').click(function(e){
    //alert($.order_global_vars.currency_details[1]['currency_name']);
    var selectedValue = this.value;
    //alert(selectedValue);
    var selectedCurrency = {};
    for(var i=0; i<$.order_global_vars.currency_details.length;i++){
      if($.order_global_vars.currency_details[i]['id'] == selectedValue){
	$.order_global_vars.selected_currency = $.order_global_vars.currency_details[i];
      }      
    }
    //update the additions values
    updateAditionsValues();
    updatePriceLabel();
  }); 
}

function fetchAdditions(){
  $.post("/fetch/additions",{}, function(data){
      $.order_global_vars.additions_details = data;
  },'json'); 
}

function updateAditionsValues(){
  //Get spacing and number of units
  getSpacingSelected();
  var number_of_additions = $.order_global_vars.additions_details.length;
  for(var i=0;i<number_of_additions;i++){
    $('#addition-currency-symbol-' + $.order_global_vars.additions_details[i]['id']).html("+ &nbsp;" + $.order_global_vars.selected_currency['currency_symbol_2']);
    if($('#deadline_category_tracker').val() == "perpage"){
      $('#addition-price-' + $.order_global_vars.additions_details[i]['id']).html(($.order_global_vars.additions_details[i]['additional_price'] * $.order_global_vars.selected_currency['convertion_rate']*$.order_global_vars.selected_spacing['factor']*getSelectedUnits()*$.order_global_vars.document_details['additions_factor']).toFixed(2));
    }else{//perassignment and perquestion
      $('#addition-price-' + $.order_global_vars.additions_details[i]['id']).html(($.order_global_vars.additions_details[i]['additional_price'] * $.order_global_vars.selected_currency['convertion_rate']*getSelectedUnits()*$.order_global_vars.document_details['additions_factor']).toFixed(2));
    }    
  }
}

function fetchCurrency(){   
  $.post("/fetch/currency",{}, function(data){
      $.order_global_vars.currency_details = data;
  },'json'); 
}

function getSelectedCurrency(){ 
  var initialCurrencyValue = $('input[name=order_currency]:checked', '#clientorderform').val();  
  for(var i=0; i<$.order_global_vars.currency_details.length; i++){
    if(initialCurrencyValue == $.order_global_vars.currency_details[i]['id']){
      $.order_global_vars.selected_currency = $.order_global_vars.currency_details[i];
    }
  }
}

function detectSubjectChange(){
  $('#document_subject').on('change',function(e){
    //calculate order cost
    updatePriceLabel();
  });
}

function getSubjectAdditionalPrice(){
  var selected_subject_id = $('#document_subject').val();
  var subject_additional_price=0;
  if(selected_subject_id == ""){
    return subject_additional_price;
  }else{
    for(var i = 0; i<$.order_global_vars.document_subjects.length;i++){
      if(selected_subject_id == $.order_global_vars.document_subjects[i]['id']){
	subject_additional_price = $.order_global_vars.document_subjects[i]['additional_price'];
      }
    }
    if(($('#document_type').val() == "7"  || $('#document_type').val() == "8") && subject_additional_price !=0){
	      return subject_additional_price/20;
    }
    return subject_additional_price;
  }  
}

function detectUrgencyChange(){
  $('#document_deadline').on('change',function(e){
    //calculate order cost
    updatePriceLabel();
  });
}

function getDeadlineAdditionalPrice(){
  var selected_deadline_value = $('#document_deadline').val();
  var deadline_additional_price = 0.0;
  for(var i=0; i<$.order_global_vars.document_deadlines.length;i++){
    if(selected_deadline_value == $.order_global_vars.document_deadlines[i]['time_in_seconds']){
      deadline_additional_price = $.order_global_vars.document_deadlines[i]['additional_price'];
    }
  }
  
    if($('#document_type').val() == "7" || $('#document_type').val() == "8"){
	      return deadline_additional_price/6;
    }
  
  return deadline_additional_price;
}

function fetchLevelOfWriting(){
   $.post("/fetch/level",{}, function(data){
      $.order_global_vars.level_of_writing = data;
  },'json'); 
}
function detectLevelOfWritingChange(){
  $('#level_of_writing').on('change',function(e){
    //calculate order cost
    updatePriceLabel();
  });
}

function getLevelOfWritingAdditionalPrice(){
  var selected_Level = $('#level_of_writing').val();
  var level_additional_price = 0;
  for(var i=0;i<$.order_global_vars.level_of_writing.length;i++){
    if(selected_Level == $.order_global_vars.level_of_writing[i]['id']){
      level_additional_price = $.order_global_vars.level_of_writing[i]['additional_price'];
    }
  }
  return level_additional_price;
}

function detectAdditionsClick(){
  $('.shared-order-additions').click(function(e){
    updatePriceLabel();
  });
}

function detectNumberOfUnitsChange(){
  $('#number_of_units').on('change',function(e){
    updatePriceLabel();
    updateAditionsValues();
  });
}

function getSelectedUnits(){
  var number_of_units = $('#number_of_units').val();
  if(number_of_units == ""){
    return 1;
  }else{
    return number_of_units;
  }
}