var four ={
	post:function(url, func) {
		$.post(url, function(data) {
			func(data);
		});
	},
	highChart:function(id , title, type, xSeries, xTitle, ySeries, yTitle){
		//if(type=='column') 
		Highcharts.chart(id, {
		    chart: {
		        type: type
		    },
		    title: {
		        text: title
		    },
		    xAxis: {
		        categories: xSeries,
		        crosshair: true
		    },
		    yAxis: {
		        min: 0,
		        title: {
		            text: xTitle
		        }
		    },
		    tooltip: {
		        headerFormat: '<span style="font-size:10px">{series.name}: {point.x} mm</span> <table>',
		        pointFormat: '<tr><td style="color:{series.color};padding:0"></td>' +
		            '<td style="padding:0"><b>库存量：{point.y:.1f} 吨</b></td></tr>',
		        footerFormat: '</table>',
		        shared: true,
		        useHTML: true
		    },
		    plotOptions: {
		        column: {
		            pointPadding: 0.2,
		            borderWidth: 0
		        }
		    },
		    series: [{
		        name: yTitle,
		        data: ySeries

		    }]
		});
	},
	lineChart:function(id , title, type, xSeries, xTitle, ySeries, yTitle){
		Highcharts.chart(id, {
		    chart: {
		        type: type
		    },
		    title: {
		        text: title
		    },
		    xAxis: {
		        categories: xSeries
		    },
		    yAxis: {
		        min: 0,
		        title: {
		            text: yTitle,
		            style: {
                        color: '#3E576F'
                    }
		        }
		    },
		    tooltip: {
		    	
		    },
		    plotOptions: {
		    	line: {
		    		dataLabels: {
						// 开启数据标签
						enabled: true          
					},
	                marker:{
	                    enabled: false,
	                    states: {
	                        hover: {
	                            enabled: true,
	                            symbol: 'circle',
	                            radius: 5,
	                            lineWidth: 1
	                        }
	                    }
	                }
	            }
	        },
		    series: [{
		        name: yTitle,
		        data: ySeries
		    }]
		});
	},
	table:function(id, url,  columns) {
		$("#" + id).bootstrapTable({
            url: url,   			//请求地址
            striped : true, 		//是否显示行间隔色
            pageNumber : 1, 		//初始化加载第一页
            pagination : true,		//是否分页
            sidePagination : 'server',			//server:服务器端分页|client：前端分页
            //search : true,
            singleSelect: false, 
            //showFooter: true,
            pageSize : 10,			//单页记录数
            pageList : [10, 50],	//可选择单页记录数
            showRefresh : false,		//刷新按钮
            queryParams : function(params) {	//上传服务器的参数
                var temp = {
                    offset :params.offset + 0,	// SQL语句起始索引
                    pageNumber : params.limit  	// 每页显示数量
                };
                return temp;
            },
            columns : columns
        });
	},
	editTable:function(id, url,  columns, callback) {
		$("#" + id).bootstrapTable({
	        url: url,
	        toolbar: '#toolbar',
	        clickEdit: true,
	        //showToggle: true,
	        pagination: true,       //显示分页条
	        //showColumns: true,
	        //showPaginationSwitch: true,     //显示切换分页按钮
	        //showRefresh: true,      //显示刷新按钮
	        //clickToSelect: true,  //点击row选中radio或CheckBox
	        columns: columns,
	        /**
	         * @param {点击列的 field 名称} field
	         * @param {点击列的 value 值} value
	         * @param {点击列的整行数据} row
	         * @param {td 元素} $element
	         */
	        onClickCell: function(field, value, row, $element) {
	            $element.attr('contenteditable', true);
	            $element.blur(function() {
	                var index = $element.parent().data('index');
	                var tdValue = $element.html();
	                four.saveData(id, index, field, tdValue);
	                callback(id, index, field, tdValue);
	            })
	        }
	    });
	},
	saveData:function(id,index, field, value){
		$("#" + id).bootstrapTable('updateCell', {
            index: index,       //行索引
            field: field,       //列名
            value: value        //cell值
        })
	},
	
	insertEditTable:function(id, row) {
		$("#" + id).bootstrapTable('insertRow', {
	            index: 0,
	            row: row
	        });
	},
	singleTable:function(title, titles, columns){
		var html = '<h4 id="tables-example">' + title + '</h4><table class="table table-bordered">';
        html +='<thead><tr>';
        for(var i = 0; titles && i < titles.length; i++) {
        	html+='<th>' + titles[i] + '</th>';
        }
        html +='</tr></thead><tbody><tr>';
        for(var i = 0; columns && i < columns.length; i++) {
        	html+='<td>' + columns[i] + '</th>';
        }
        html +='</tr></tbody></table>';
        return html;
	}
}