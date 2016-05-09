$(document).ready(function() {
$("#sub_but1").click(function(event) {	
		var objIVR = new Object();
		objIVR.ivr=$("#ivrNo").val();
		//alert(objIVR.ivr);
		
		var objGB = new Object();
		var str0=$("#grpBase").val();
		objGB.gB = str0.replace(/^\s+|\s+$/gm,'');
		//alert(objGB.gB);
		
		//console.log(objIVR, objGB,);
		
		var objGWN = new Object();
		var str=$("#grpWeNot").val();
		objGWN.gWN = str.trim().replace(/[./,]/g,'","');
		//alert(objGWN.gWN);
		console.log(objGWN);
		
		var objAGWU = new Object();
		objAGWU.aGWU=$("#audGrpWeU").val();
		 
		
		var objSHN = new Object();
		var str1=$("#selHanNot").val();
		objSHN.sHN = str1.replace(/[./,]/g,'","');
		//alert(objSHN.sHN);
		
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
		
		/*var objLSL = new Object();
		objLSL.lSL=$("#lanSelLis").val();
		
		var objLWU = new Object();
		objLWU.lWU=$("#lagWelU").val();*/
		
		var requestStr1 ='{"json": {"request": {"servicetype": "31","functiontype": "1001","sourcetype": "ivr","data": {"scope": "admin service","type": "personal","ivrnumber": "'+objIVR.ivr+'","grpzWelcomeNotes": {"dataList": ["'+objGWN.gWN+'"]},"audioGrpzWelcomeUrl": "'+objAGWU.aGWU+'","selectionHangupNotes": {"dataList": ["'+objSHN.sHN+'"]},"audioSelectionHangupUrl": {"dataList": ["'+objASHU.aSHU+'"]},"selectionEndNotes": {"dataList": ["'+objSEN.sEN+'"]},"selectionEndUrl": "'+objSEU.sEU+'","errorNotes": {"dataList": ["'+objEN.eN+'"]},"audioerrorUrl": "'+objAEU.aEU+'", "memberWelcomeNotes": {"dataList": ["'+objMWN.mWN+'"]},"audioMemberWelcomeUrl": {"dataList": ["'+objAMWU.aMWU+'"]},"notRegGroupzNotes": {"dataList": ["'+objNRGN.nRGN+'"]},"notRegGroupzUrl": "'+objNRGU.nRGU+'", "maintenanceNotes": {"dataList": ["'+objMN.mN+'"]},"maintenanceUrl": {"urlList": [ "'+objMU.mU+'"]},"generalHangupNotes": {"dataList": ["'+objGHN.gHN+'"]}, "generalHangupUrl": "'+objGHU.gHU+'", "numbersUrllist": {"urlList": { "3": "http://www.press3.wav", "2": "http://www.press2.wav", "1": "http://www.press1.wav", "7": "http://www.press7.wav", "6": "http://www.press6.wav", "5": "http://www.press5.wav", "4": "http://www.press4.wav","8": "http://www.press8.wav"}},"previousMenuSelectNotes": {"dataList": [ "'+objPMSN.pMSN+'" ]  },  "previousMenuSelectUrl": {  "dataList": [  "'+objPMSU.pMSU+'" ] }, "playspeed": "5", "settimeout": "6000",  "multiLanguageFlag": "0", "languageSelectionList": "","languageWelcomeURL": "", "groupzBase": "'+objGB.gB+'"}}}}';
		
		//var requestStr = '{"json": {"request": {"servicetype": "31","functiontype": "1002","sourcetype": "ivr","data": {"scope": "admin service","type": "personal","ivrnumber": "'+obj.ivr+'","grpzWelcomeNotes": {"dataList": ["'+obj.gWN+'"]},"audioGrpzWelcomeUrl": "'+obj.aGWU+'","selectionHangupNotes": {"dataList": ["'+obj.sHN+'"]},"audioSelectionHangupUrl": {"dataList": ["'+obj.aSHU+'"]},"selectionEndNotes": {"dataList": ["'+obj.sEN+'"]},"selectionEndUrl": "'+obj.sEU+'","errorNotes": {"dataList": ["'+obj.eN+'"]},"audioerrorUrl": "'+obj.aEU+'","memberWelcomeNotes": {"dataList": ["'+obj.mWN+'"]},"audioMemberWelcomeUrl": {"dataList": ["'+obj.aMWU+'"]},"notRegGroupzNotes": {"dataList": ["'+obj.nRGN+'"]},"notRegGroupzUrl": "'+obj.nRGU+'","maintenanceNotes": {"dataList": ["'+obj.mN+'"]},"maintenanceUrl": {"urlList": ["'+obj.mU+'"]},"generalHangupNotes": {"dataList": ["'+obj.gHN+'"]},"generalHangupUrl": "'+obj.gHU+'","numbersUrllist": {"urlList": {"3": "http://www.press3.wav","2": "http://www.press2.wav","1": "http://www.press1.wav","7": "http://www.press7.wav","6": "http://www.press6.wav","5": "http://www.press5.wav","4": "http://www.press4.wav","8": "http://www.press8.wav"}},"previousMenuSelectNotes": {"dataList": ["'+obj.pMSN+'"]},"previousMenuSelectUrl": {"dataList": ["'+obj.pMSU+'"]},"playspeed": "5","settimeout": "6000","multiLanguageFlag": "0","languageSelectionList": {"selectionList": {"2": "'+obj.lSL+'","1": "'+obj.lSL+'"}},"languageWelcomeURL": {"urlList": ["'+obj.lWU+'"]},"groupzBase": "'+obj.gB+'"}}}}';		
		//var requestStr = '{"json": {"request": {"servicetype": "31","functiontype": "1002","sourcetype": "ivr","data": {"scope": "admin service","type": "personal","ivrnumber": "'+obj.ivr+'","grpzWelcomeNotes": ["'+obj.gWN+'"],"audioGrpzWelcomeUrl": "'+obj.aGWU+'","selectionHangupNotes": ["'+obj.sHN+'"],"audioSelectionHangupUrl": "'+obj.aSHU+'","selectionEndNotes": ["'+obj.sEN+'"],"selectionEndUrl": "'+obj.sEU+'","errorNotes": ["'+obj.eN+'"],"audioerrorUrl": "'+obj.aEU+'","memberWelcomeNotes": ["'+obj.mWN+'"],"audioMemberWelcomeUrl": "'+obj.aMWU+'","notRegGroupzNotes": ["'+obj.nRGN+'"],"notRegGroupzUrl": "'+obj.nRGU+'","maintenanceNotes": "'+obj.mN+'","maintenanceUrl": "'+obj.mU+'","generalHangupNotes": ["'+obj.gHN+'"],"generalHangupUrl": "'+obj.gHU+'","numbersUrllist": {"3": "http://www.press3.wav","2": "http://www.press2.wav","1": "http://www.press1.wav","7": "http://www.press7.wav","6": "http://www.press6.wav","5": "http://www.press5.wav","4": "http://www.press4.wav","8": "http://www.press8.wav"},"previousMenuSelectNotes": ["'+obj.pMSN+'"],"previousMenuSelectUrl": "'+obj.pMSU+'","playspeed": "5","settimeout": "6000","multiLanguageFlag": "0","languageSelectionList": {"2": "kannada","1": "english"},"languageWelcomeURL": "'+obj.lWU+'","groupzBase": "'+obj.gB+'"}}}';		
		var parsedData1 = JSON.parse(requestStr1);
		var durl = "http://prod1.groupz.in:7070/GroupzIVRAdmin/IVRgroupzbaseAdminService?"
        $.ajax({//Process the form using $.ajax()
			url: durl, //Your form processing file url
            type: 'POST', //Method type
         	dataType: 'json',
			
            data: {"request" : JSON.stringify(parsedData1)}, //Forms name
            complete: function(data) {
			console.log(data);
			
            if (data.status == "200") {
				var result1= data.responseText;
				var data2 = JSON.parse(result1);
				//console.log(JSON.stringify(data2));
				var rescode=data2.statuscode;
				var result1 = JSON.stringify(data2);
				if(rescode==0){
					//sessionStorage.setItem('key', result);
					//alert(result);			
					
				} 
				else {
					alert('Groupzbase added successfully');
					window.location.reload(true);
				}
				//console.log("data status : "+data2.json.response.statuscode);
				}else {
					alert('Connection Error');
				}
				
			}
		});	 
		event.preventDefault(); //Prevent the default submit
});









});
	
	


				
				
	