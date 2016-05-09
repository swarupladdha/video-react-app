window.listData = {};
$(window).load(function() {
	getDataForListView();
	
});

function getDataForListView() {
	var ivr="";
	var base="";

		var requestStr0 = '{"json":{"request":{"servicetype":"31","functiontype":"1004","data":{}}}}';
				
		var parsedData0 = JSON.parse(requestStr0);
		var durl0 = "http://prod1.groupz.in:7070/GroupzIVRAdmin/IVRgroupzbaseAdminService?"
				
		$.ajax({//Process the form using $.ajax()
		url: durl0, //Your form processing file url
		type: 'POST', //Method type
		dataType: 'json',
						
		data: {"request" : JSON.stringify(parsedData0)}, //Forms name
						
		complete: function(data) {
		
		//alert(data.responseText);
		if (data.status == "200") {
		var result0= data.responseText;
		var data20 = JSON.parse(result0);
		var rescode=data20.json.response.statuscode;	
		var result0 = JSON.stringify(data20);
		window.listData = result0;
		
		if(rescode==0){
			
				sessionStorage.setItem('key', result0);
				
				
				var row='<table style="width:100%" cellspacing="0" cellpadding="1" border="0"><thead><tr><th style="font-size:15pt;">IVR Number</th><th style="font-size:15pt;">Groupz Base</th><th></th><th></th><th></th></tr></thead>';
				for (var i = 0; i < data20.json.response.data.length; i++) {
				ivr=data20.json.response.data[i].ivrnumber;
				base=data20.json.response.data[i].groupzBase;
				row+= '<tr><td>'+data20.json.response.data[i].ivrnumber+'</td><td>'+data20.json.response.data[i].groupzBase+'</td><td ><a href="viewbase.html" onclick="member(ivr,base)" style="">View</a></td><td ><a href="editBase.html" style="">Edit</a></td><td ><a href="index.html" style="">Groupz</a></td></tr>';
            
				}
	            row+='</table>';
    			$("#box").empty().append(row);
	            
	
			}

		}
		}
			
	});	

}


