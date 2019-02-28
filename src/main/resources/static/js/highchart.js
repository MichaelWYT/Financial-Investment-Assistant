/**
 * JavaScript to handle the front-end and handle the data source from back-end. Puts the data into the chart in
 * the correct format.
 */

var symbol, indicator;

$.ajax({
	url:'/getJson',
	async: false,
	success: function(jsonData) {
		
		var ohlc = [], volume = [];
		var symbol;
		
		// Last part of the data sent is the options.
		for(var i=0;i<jsonData.length-1;i++){
			ohlc.push([
				jsonData[i][0],
				jsonData[i][1],
				jsonData[i][2],
				jsonData[i][3],
				jsonData[i][4]
			]);
			volume.push([
				jsonData[i][0],
				jsonData[i][5]
			]);
		}
		
		symbol = jsonData[jsonData.length-1][0];
		name = jsonData[jsonData.length-1][1];
		
		Highcharts.stockChart('chartID',{
			rangeSelector: {
				selected: 4
			},
			plotOptions: {
				candlestick: {
					
				}
			},
			chart :{
				displayErrors: true
			},
			title: {
				text: name
			},
			yAxis: [{
	            labels: {
	                align: 'right',
	                x: -3
	            },
	            title: {
	                text: 'OHLC'
	            },
	            height: '60%',
	            lineWidth: 2,
	            resize: {
	                enabled: true
	            }
	        }, {
	            labels: {
	                align: 'right',
	                x: -3
	            },
	            title: {
	                text: 'Volume'
	            },
	            top: '65%',
	            height: '35%',
	            offset: 0,
	            lineWidth: 2
	        }],
	        series: [{
	        	id: 'main',
	            type: 'candlestick',
	            name: name,
	            data: ohlc
	        }, {
	            type: 'column',
	            name: 'Volume',
	            data: volume,
	            yAxis: 1
	        }, {
	        	type: 'bb',
	        	linkedTo: 'main'
	        }]
		})
	}
});

// This works now, data sent is json format includes "" therefore back end will process one more time to remove them.
$("#apply").click(function() {
	
	if($("#symbols").val() == "default"){
		alert("Please pick a valid selection.");
	} else {
		
		var obj = JSON.stringify("{year:"+$("#yearSelector").val()+", symbol:"+$("#symbols").val()+
				", name: "+$("#symbols option:selected").text()+"}"); 
		
		$.ajax({
			method: 'POST',
			url: '/postJson',
			contentType: "application/json",
			dataType: 'json',
			data: obj,
			async: false,
			error: function(jqXHR, status, error){
				alert("jqXHR-> "+jqXHR+" with error: "+error+": Given status -> "+status);
			}
		});
	}
	
});

