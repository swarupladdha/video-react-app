$(document).ready(function() {
$("#sub_but1").click(function(event) {


		
	
		var objIVR = new Object();
		objIVR.ivr=$("#ivrNo").val();
		//alert(objIVR.ivr);
		
		var objGB = new Object();
		objGB.gB=$("#grpBase").val();
		//alert(objGB.gB);
		
		var objGC = new Object();
		objGC.gC=$("#grpCode").val();
		//alert(objGC.gC);
		
		
		var objGNU = new Object();
		objGNU.gnu =$("#grpNamU").val();
		//alert(objGNU.gnu);
		
		var objWN = new Object();
		var str1 =$("#welNot").val();
		objWN.wn =str1.replace(/[./,]/g,'","');
		//alert(objWN.wn);
		
		var objAWU = new Object();
		objAWU.wu =$("#audWelU").val();
		//alert(objAWU.wu);
		
		var objSLU = new Object();
		objSLU.wu =$("#selLisU").val();
		//alert(objSLU.wu);
		
		
		
		
		
		var json = "";
		var otArr = [];
		var tbl2 = $('#hidden tbody tr').each(function(i) {        
		x = $(this).children();
	  
		  var itArr = [];
		  x.each(function() {
			 itArr.push('"' + $(this).find('input').val() + '"');
		  });
		  //console.log(itArr);
		  otArr.push( itArr.join(':') );
	   })
	   json += otArr.join(",");

	   //alert(json);
		
		
		//var requestStr ='{"json": {"request": {"servicetype": "31","functiontype": "2001","sourcetype": "ivr","data": {"scope": "admin service","type": "personal","ivrnumber": "'+objIVR.ivr+'","groupzCode": "'+objGC.gC+'","welcomeNotes": {"welcomenotesList": ["'+objWN.wn+'"]},"audioWelcomeUrl": "'+objAWU.wu+'","selectionlist": {"selectionList": {""}},"selectionlistUrl": "'+objSLU.wu+'","groupzNameUrl": "'+objGNU.gnu+'","multiLanguageFlag": "0","endDate": "","address": ""}}}}';
		var requestStr ='{"json": {"request": {"servicetype": "31","functiontype": "2001","sourcetype": "ivr","data": {"scope": "admin service","type": "personal","ivrnumber": "'+objIVR.ivr+'","groupzCode": "'+objGC.gC+'","welcomeNotes": {"welcomenotesList": ["'+objWN.wn+'"]},"audioWelcomeUrl": "'+objAWU.wu+'","selectionlist": {"selectionList": {'+json+'}},"selectionlistUrl": "'+objSLU.wu+'","groupzNameUrl": "'+objGNU.gnu+'","multiLanguageFlag": "0","recmultilanguageSelectionList": {"selectionList": {"2": "kannada","1": "english"}},"recmultilanguageSelectionWelcomeURL": {"urlList": ["http://english.wav","http://kannada.wav"]},"endDate": "","address": ""}}}}';
		
		var parsedData = JSON.parse(requestStr);
		var durl = " http://prod1.groupz.in:7070/GroupzIVRAdmin/IVRgroupzAdminService?"
        $.ajax({//Process the form using $.ajax()
			url: durl, //Your form processing file url
            type: 'POST', //Method type
         	dataType: 'json',
			
            data: {"request" : JSON.stringify(parsedData)}, //Forms name
            complete: function(data) {
			console.log(data);
			
            if (data.status == "200") {
				var result= data.responseText;
				var data2 = JSON.parse(result);
				var rescode=data2.statuscode;
				var result = JSON.stringify(data2);
				if(rescode==0){
					//sessionStorage.setItem('key', result);
					//alert(result);			
					
				} 
				else {
					alert('Groupz added successfully');
					window.location.reload(true);
				}
				//console.log("data status : "+data2.json.response.statuscode);
				}
				else {
					alert("Connection Error.");
				}
			}
					});	 
					event.preventDefault(); //Prevent the default submit
});
	

});
