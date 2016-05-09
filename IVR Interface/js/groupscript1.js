$(window).load(function() {

var ivr="";
var code="";

		var objIVR = new Object();
		objIVR.ivr=$("#ivrNo").val();
		
		var objGC = new Object();
		objGC.gC=$("#grpCode").val();

var requestStr0 = '{"json": {"request": {"servicetype": "31","functiontype": "2004","sourcetype": "ivr","data": {"ivrnumber": "'+objIVR.ivr+'","groupzCode": "'+objGC.gC+'"}}}}';
				
		var parsedData0 = JSON.parse(requestStr0);
		var durl0 = "http://prod1.groupz.in:7070/GroupzIVRAdmin/IVRgroupzAdminService?"
				
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
				
				
				var row='<table style="width:100%" cellspacing="0" cellpadding="1" border="0"><thead><tr><th style="font-size:15pt;">IVR Number</th><th style="font-size:15pt;">Groupz Code</th><th></th><th></th><th></th></tr></thead>';
				for (var i = 0; i < data20.json.response.data.length; i++) {
				ivr=data20.json.response.data[i].ivrnumber;
				code=data20.json.response.data[i].groupZCode;
				row+= '<tr><td>'+data20.json.response.data[i].ivrnumber+'</td><td>'+data20.json.response.data[i].groupZCode+'</td><td ><a href="#" style="">View</a></td><td ><a href="#" style="">Edit</a></td><td ><a href="index.html" style="">Groupz</a></td></tr>';
            
				}
	            row+='</table>';
    			$("#box").empty().append(row);
	            
	
			
			
			
			
			/*var row = '<tr class="header">';
			row += '</tr>'
			$(row).appendTo('table.data');
			row = '';
			for (var i in data.rows) {
			row += '<tr id="' + i + '">';
			row += '<td>' + data.rows[i].val + '</td>';
			row += '<td>' + data.rows[i].val + '</td>';
			row += '<td>' + data.rows[i].val + '</td>';
			row += '</tr>';
									
			}
			$(row).appendTo('table.data');

			function myFunction() {
			var table = document.getElementById("myTable");
			var row = table.insertRow(0);
			var cell1 = row.insertCell(0);
			var cell2 = row.insertCell(1);
			cell1.innerHTML = "NEW CELL1";
			cell2.innerHTML = "NEW CELL2";
			}*/					
			}

		}
		}
			
	});	



});
