/*$(window).load(function() {

var ivr="";
var base="";




		var objIVR = new Object();
		objIVR.ivr=$("#ivrNo").val();
		
		
		var objGB = new Object();
		objGB.gB=$("#grpBase").val();
		
		
		console.log(objIVR, objGB);
		var objGWN = new Object();
		var str=$("#grpWeNot").val();
		objGWN.gWN = str.replace(/[./,]/g,'","');
		
		var objAGWU = new Object();
		objAGWU.aGWU=$("#audGrpWeU").val();
		 
		
		var objSHN = new Object();
		var str1=$("#selHanNot").val();
		objSHN.sHN = str1.replace(/[./,]/g,'","');
		
		
		var objASHU = new Object();
		objASHU.aSHU=$("#AudSelHanU").val();
		
		var objSEN = new Object();
		var str2=$("#SelEnNot").val();
		objSEN.sEN=str2.replace(/[./,]/g,'","');
		
		
		var objSEU = new Object();
		objSEU.sEU=$("#selEnU").val();
		
		var objEN = new Object();
		var str3=$("#ErrNot").val();
		objEN.eN=str3.replace(/[./,]/g,'","');
		
		
		var objAEU = new Object();
		objAEU.aEU=$("#audErrU").val();
		
		var objMWN = new Object();
		var str4=$("#memWelNot").val();
		objMWN.mWN=str4.replace(/[./,]/g,'","');
		
		var objAMWU = new Object();
		objAMWU.aMWU=$("#audMemWelU").val();
		
		var objNRGN = new Object();
		var str5=$("#notRegGrpNot").val();
		objNRGN.nRGN=str5.replace(/[./,]/g,'","');
		
		var objNRGU = new Object();
		objNRGU.nRGU=$("#notRegGrpU").val();
		
		var objMN = new Object();
		var str6=$("#maiNot").val();
		objMN.mN=str6.replace(/[./,]/g,'","');
		
		var objMU = new Object();
		objMU.mU=$("#maiU").val();
		
		var objGHN = new Object();
		var str7=$("#genHanNot").val();
		objGHN.gHN=str7.replace(/[./,]/g,'","');
		
		var objGHU = new Object();
		objGHU.gHU=$("#genHanU").val();
		
				
		var objPMSN = new Object();
		var str8=$("#preMenSelNot").val();
		objPMSN.pMSN=str8.replace(/[./,]/g,'","');
		
		var objPMSU = new Object();
		objPMSU.pMSU=$("#preMenSelU").val();
		
		var obj = new Object();
		obj.plS=$("#plaSpe").val();
		
		var obj = new Object();
		obj.sTO=$("#setTimO").val();
		
		var obj = new Object();
		obj.mLF=$("#mulLanFl").val();



		var requestStr0 = '{"json":{"request":{"servicetype":"31","functiontype":"1002","sourcetype":"ivr","data":{"scope":"admin service","type":"personal","ivrnumber":"'+objIVR.ivr+'","grpzWelcomeNotes":{"dataList":["'+objGWN.gWN+'"]},"audioGrpzWelcomeUrl":"'+objAGWU.aGWU+'","selectionHangupNotes":{"dataList":["'+objSHN.sHN+'"]},"audioSelectionHangupUrl":{"dataList":["'+objASHU.aSHU+'"]},"selectionEndNotes":{"dataList":["'+objSEN.sEN+'"]},"selectionEndUrl":"'+objSEU.sEU+'","errorNotes":{"dataList":["'+objEN.eN+'"]},"audioerrorUrl":"'+objAEU.aEU+'","memberWelcomeNotes":{"dataList":["'+objMWN.mWN+'"]},"audioMemberWelcomeUrl":{"dataList":["'+objAMWU.aMWU+'"]},"notRegGroupzNotes":{"dataList":["'+objNRGN.nRGN+'"]},"notRegGroupzUrl":"'+objNRGU.nRGU+'","maintenanceNotes":{"dataList":["'+objMN.mN+'"]},"maintenanceUrl":{"urlList":["'+objMU.mU+'"]},"generalHangupNotes":{"dataList":["'+objGHN.gHN+'"]},"generalHangupUrl":"'+objGHU.gHU+'","numbersUrllist":{"urlList":{"3":"http://www.press3.wav","2":"http://www.press2.wav","1":"http://www.press1.wav","7":"http://www.press7.wav","6":"http://www.press6.wav","5":"http://www.press5.wav","4":"http://www.press4.wav","8":"http://www.press8.wav"}},"previousMenuSelectNotes":{"dataList":["'+objPMSN.pMSN+'"]},"previousMenuSelectUrl":{"dataList":["'+objPMSU.pMSU+'"]},"playspeed":"5","settimeout":"6000","multiLanguageFlag":"0","groupzBase":"'+objGB.gB+'"}}}}';
				
		var parsedData0 = JSON.parse(requestStr0);
		var durl0 = "http://prod1.groupz.in:7070/GroupzIVRAdmin/IVRgroupzbaseAdminService?"
				
		$.ajax({//Process the form using $.ajax()
		url: durl0, //Your form processing file url
		type: 'POST', //Method type
		dataType: 'json',
						
		data: {"request" : JSON.stringify(parsedData0)}, //Forms name
						
		complete: function(data) {
		console.log(data);
		
		//alert(data.responseText);
		if (data.status == "200") {
		var result0= data.responseText;
		var data20 = JSON.parse(result0);
		var rescode=data20.json.response.statuscode;	
		var result0 = JSON.stringify(data20);
		
		if(rescode==0){
			
				sessionStorage.setItem('key', result0);
				
				
				var row='<table style="width:100%" cellspacing="0" cellpadding="1" border="0"><thead><tr><th style="font-size:15pt;">IVR Number</th><th style="font-size:15pt;">Groupz Base</th><th></th><th></th><th></th></tr></thead>';
				for (var i = 0; i < data20.json.response.data.length; i++) {
				ivr=data20.json.response.data[i].ivrnumber;
				base=data20.json.response.data[i].groupzBase;
				row+= '<tr><td>'+data20.json.response.data[i].ivrnumber+'</td><td>'+data20.json.response.data[i].groupzBase+'</td><td ><a href="viewbase.html" style="">View</a></td><td ><a href="#" style="">Edit</a></td><td ><a href="index.html" style="">Groupz</a></td></tr>';
				
				}
	            row+='</table>';
    			$("#box").empty().append(row);
	            
				
			
			
			}

		}
		}
			
	});	



});*/

$(window).load(function() {
	console.log(window.listData);
	alert('test', listData);
	member();
});


function member(ivr,base){
	
 
	
	alert('abc');
	var ivr="";
	var base="";
	var this_select_member = data20.json.response.data[i];
	for (var i = 0; i < data20.json.response.data.length; i++) {
		ivr=data20.json.response.data[i].ivrnumber;
		base=data20.json.response.data[i].groupzBase;
	}
	$("#ivrNo").empty().append(this_select_member);
	console.log(listData);
}

