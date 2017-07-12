
function showTree()
{
	var modal = document.getElementById('myModal');
	modal.style.display = "block";
	
}
function closemodal()
{
	var modal = document.getElementById('myModal');
	modal.style.display = "none";
	}



var app = angular.module('tree', ['ui.bootstrap','ngCookies']);
app.controller('TreeController',function($scope,$http,$window,$cookieStore)
{
	$scope.transactionFlag = false;
	$scope.searchFlag = false;
	//$scope.treeFlag = false;
	//$scope.modalShown = false;
    //var URL_BASE="http://10.11.3.3:8080/Sample_Tracker/webapi/";
	//var URL_BASE="http://pushd.healthelife.in:8080/Sample_Tracker/webapi/";
    var URL_BASE="http://localhost:8080/EHealthCare/webapi/";
    var REQUEST_URL_BASE = URL_BASE+ "requestservice";
    var ASSET_URL_BASE = URL_BASE + "assetservice";
    var STATION_URL_BASE = URL_BASE+"stationservice";
    var TECHNICIAN_URL_BASE = URL_BASE + "technicianservice";
    $scope.username=$cookieStore.get('username');
    $scope.task = false;
    // true if pending tasks or Completed Tasks else false for Scan Barcode Screen
    $scope.npNumberValue='';
    $scope.asset='';
    $scope.assetData = {};
    $scope.patient={};
    $scope.step1=true;
    $scope.label='';
    
    $scope.setUsername=function() {
       // console.log("here I am !");
        $cookieStore.put('username',$scope.username);
        console.log("username: "+$cookieStore.get('username'));
        //$('#loginModal').modal('hide');
    }
    $scope.techTable=[];
    $scope.tech={};
    $http.get(TECHNICIAN_URL_BASE)
    .success(function(data) {
       //alert("success loading technicians")
       $scope.techTable=data;
       console.log($scope.techTable);
    })
    .error(function(data) {
        //alert("Errors in loading technicians");
        
    });
    $scope.minimize = function(data) {
        data.nodes = [];
    };
    $scope.add = function(data) {
    	console.log("here");
        var post = data.nodes.length + 1;
        var newName = data.name + '-' + post;
        data.nodes.push({name: newName,nodes: []});
    };
   // $scope.tree = [{name: "Node", nodes: []}];
    
    $scope.getChildren=function (data) 
    {
    	data.nodes = [];
    	var assetId = data["id"];
    	var url = ASSET_URL_BASE + "/getchildrenstatus?assetId="+assetId;
    	console.log(data);
    	
    	$http.get(url).success(function(result) 
    	 {
    		var len = result["assetStatuses"].length;
    		
    		console.log(result["assetStatuses"].length);
    		console.log(result["assetStatuses"][0]["assetId"]);
    		 for(var i=0;i<len;i++)
          	{
    			 
    			 data.nodes.push({id: result["assetStatuses"][i]["assetId"], status :result["assetStatuses"][i]["status"], nodes: []});
    			 
          	}
    	    //var jsonData =  JSON.parse(data);
    	    
    	   
    	    console.log("requestId status success");
    	  })
    	        .error(function() {
    	            alert("error in requestId status");
    	            
    	});
    	
    	
    	console.log("child");
    	//var assetID = data;
    	//console.log(assetID);
    	//var post = data.nodes.length + 1;
       // var newName = data.name + '-' + post;
       // data.nodes.push({name: newName,nodes: []});
    }
    
    $scope.requestIdStatus=function (requestId) 
    {
    	var data;
    	$scope.tree=[];
    	console.log(" scope " +  $scope.requestId + "func val "+ requestId);
    	if(requestId == undefined || requestId == null || requestId == "")
    	{
    		data = $scope.requestId;
    	}
    	else
    	{
    		data = requestId;
    	}
    	var url = REQUEST_URL_BASE + "/getrequeststatus?requestId="+data;
    	
    	console.log("requestID status in js "+data);
    	 $http.get(url).success(function(result) 
    	{
    		 console.log(result);
    		 console.log(result["assetStatuses"][0]["assetId"]);
    		
    		 $scope.tree = [{id: result["assetStatuses"][0]["assetId"], status :result["assetStatuses"][0]["status"],  nodes: []}];
    		
            console.log("requestId status success");
         
            
        })
        .error(function() {
            alert("error in requestId status");
            
        });
    }
   
    $scope.transactionTable = [];
    $scope.getTransactions=function()
    {
    	$scope.transactionFlag = true;
    	var startDate = $scope.startDate;
    	var endDate = $scope.endDate; 
    	console.log("station id in js "+$scope.stationId);
    	console.log("time " + startDate + " " + endDate);
    	var stationId = $scope.stationId;
    	var url = STATION_URL_BASE + "/transactions?stationId=" + stationId + "&startDate="+startDate+"&endDate="+endDate;

    	//
	   	 $http.get(url).success(function(result) 
	   	{
	   		 console.log(result);
	   		var len = result["transactions"].length;
    		
    		console.log(result["transactions"].length);
    		console.log(result["transactions"][0]["assetId"]);
    		for(var i=0;i<len;i++)
          	{
    			 $scope.transactionTable.push(result["transactions"][i]);
    			 //data.nodes.push({name: result["assetStatuses"][i]["assetId"], status :result["assetStatuses"][i]["status"], nodes: []});
    			 
          	}
    		 
	       })
	       .error(function() {
	           alert("error in requestId status");
	           
	       });
    }
  

    $scope.searchResultTable = []
    $scope.getSearchResults=function()
    {
    	$scope.searchResultTable = []
    	$scope.searchFlag = true;
    	
    	console.log("search criteria id in js "+$scope.reqId);
    	console.log("search val " + $scope.patientName);
    	
    	if( $scope.rootId == undefined)
    	{
    		$scope.rootId  = "";
    	}
    	if( $scope.reqId == undefined)
    	{
    		$scope.reqId  = "";
    	}
    	if( $scope.patientName == undefined)
    	{
    		 $scope.patientName  = "";
    	}
    	if( $scope.patientUHID == undefined)
    	{
    		$scope.patientUHID  = "";
    	}
    	
    	if( $scope.doctorName == undefined)
    	{
    		$scope.doctorName  = "";
    	}
    	
    	
    	var url = ASSET_URL_BASE + "/search?rootId=" + $scope.rootId + "&requestId="+$scope.reqId + "&patientName="+$scope.patientName + "&patientUHID="+$scope.patientUHID + "&doctorName="+$scope.doctorName;

    	//
	   	 $http.get(url).success(function(result) 
	   	{
	   		var len = result["search"].length;
    		
    		console.log(result["search"].length);
    		console.log(result["search"][0]["assetId"]);
    		for(var i=0;i<len;i++)
          	{
    			 $scope.searchResultTable.push(result["search"][i]);
    			 
          	}
    		 
	       })
	       .error(function() {
	           alert("no data matched with given parameters");
	           
	       });	
    }

    $scope.redirect=function(data)
    {
    	//alert(data);
    	//var reqId = $scope.reqId;
    	//console.log("reqId= "+reqId+"in redirect");
    	//$window.location.href="TreeStructure.html";
    	//$scope.loc = "TreeStructure.html";
    	
    	$scope.requestId = data;
    	//$scope.req = reqId;
    	//document.getElementById("req").value = reqId;
    	console.log( data + "  "+$scope.requestId);
    	$scope.requestIdStatus(data);
    	console.log("after redirect");
    	//alert("new code");
    	//$scope.treeFlag = true;
    	//$scope.modalShown = true;
    	showTree();
    	//$document.getElementById("anchorId").href = "#dialogbox";
    	//$window.location.href="http://localhost:8080/EHealthCare/Search.html#dialogBox";
    	
    }
    
    
    $scope.assetTable=[];

    $scope.populateTable=function () 
    {
        $scope.assetTable=[];
        var test= $scope.npNumberValue;//.replace("/","|").replace(" ","|");
        $http.get(ASSET_URL_BASE+"/block?npBase="+test,{headers: {'Content-Type': 'application/json'}})
            .success(function (data) {
            $scope.assetTable=data;
            console.log("data & length: "+data,$scope.assetTable.length);
            if($scope.assetTable.length==0)
            	$scope.generateBlocks();
        });
    }

    

   
    $scope.printAsset= function (printNp) 
    {
       console.log("printing...");
        var array = [
            {
                Np_Number : printNp//.replace("|","/").replace("|"," ").replace("|"," ").replace("|"," ")
            }];
        console.log(array);  
        $scope.queue=$scope.queue.concat(array);
        //alasql('SELECT * INTO CSV("BlockNpNumber.csv",{headers:true}) FROM ?',[array]);
        
    };
    $scope.printAllAssets= function () 
    {
    	 console.log("printing...All");
        var array =[];
        	array=alasql('SELECT npNumber FROM ?',[$scope.assetTable])
        	var array2=[];
        for(var i=0;i<array.length;i++)
        	{
        	array2[i]={Np_Number : array[i].npNumber};
        	}
       // console.log(array);
        console.log(array2);
        
        alasql('SELECT * INTO CSV("BlockNpNumber.csv",{headers:true}) FROM ?',[array2]);
    };
    $scope.queue=[];
    $scope.addQueue=function(){
    	if($scope.assetTable.length==0)
    		alert("There is no bottles scaned")
    	else
    		{
    	console.log("adding to queue");
    	var array =[];
    	array=alasql('SELECT npNumber FROM ?',[$scope.assetTable])
    	var array2=[];
    	for(var i=0;i<array.length;i++)
    	{
    	array2[i]={Np_Number : array[i].npNumber};
    	}
    	// console.log(array);
    	console.log(array2);
    	$scope.queue=$scope.queue.concat(array2);
    	console.log($scope.queue);
    	$scope.clearAll();
    		}
    }
    $scope.printQueue=function()
    {
    	alasql('SELECT * INTO CSV("BlockNpNumber.csv",{headers:true}) FROM ?',[$scope.queue]);
    }
    $scope.clearAll= function(){
    	 	$scope.task = false;
    	 	$scope.npNumberValue='';
    	    $scope.asset='';
    	    $scope.assetData = {};
    	    $scope.patient={};
    	    $scope.step1=true;
    	    $scope.number=1;
    	    $scope.assetTable=[];
    	    
    }	


    

});



