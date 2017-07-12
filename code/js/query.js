var app = angular.module('query', ['ui.bootstrap','ngCookies']);
app.controller('QueryController',function($scope,$http,$window,$cookieStore)
{

	$scope.query1 = false;
	$scope.query2 = false;
	$scope.stationFlag = false;
	$scope.pendingFlag = false;
	
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
        console.log("here I am !");
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
    
    $scope.stationTable = [];
    
    // Gets the station statistics
    $scope.assetCountStation=function () 
    {
    	$scope.stationTable = [];
    	console.log("coming here");
    	$scope.stationFlag = true;
    	var url = STATION_URL_BASE + "/stationstatistics";
    	$http.get(url).success(function(result) 
    	{
    		var len = result["stations"].length;
    		
    		console.log(result["stations"].length);
    		console.log(result["stations"][0]["stationName"]);
    		for(var i=0;i<len;i++)
          	{
    			 $scope.stationTable.push(result["stations"][i]);
    			 //data.nodes.push({name: result["assetStatuses"][i]["assetId"], status :result["assetStatuses"][i]["status"], nodes: []});
    			 
          	}
    		 
    	 })
       .error(function() 
       {
    	   alert("error in station count");	            
       });
    	
    	
    	console.log("here");
    	
    }
    
    $scope.requestsReceivedToday=function () 
    {
    	$scope.startDate = "";
    	$scope.endDate = "";
    	var today = new Date();
    	var curr_date = today.getDate();
        var curr_month = today.getMonth() + 1; //Months are zero based
        var curr_year = today.getFullYear();
        var date = curr_year + "-" + curr_month + "-" + curr_date;
        console.log(date);
    	var url = REQUEST_URL_BASE + "/noofrequestsreceived?startDate="+date+"&endDate="+date;
    	//$scope.myVar=true;
    	$scope.query1 = $scope.query1 ? false : true;
    	 $http.get(url).success(function(result) 
    	{
    		 console.log(result);
    		 $scope.query1Result = result;
    	 })
       .error(function() 
       {
    	   alert("error in query1");	            
       });
    	
    	
    	console.log("here");
    }

    $scope.requestsCompletedToday=function () 
    {
    	$scope.startDate = "";
    	$scope.endDate = "";
    	var today = new Date();
    	var curr_date = today.getDate();
        var curr_month = today.getMonth() + 1; //Months are zero based
        var curr_year = today.getFullYear();
        var date = curr_year + "-" + curr_month + "-" + curr_date;
        console.log(date);
    	var url = REQUEST_URL_BASE + "/noofrequestscompleted?startDate="+date+"&endDate="+date;
    	//$scope.myVar=true;
    	$scope.query2 = $scope.query2 ? false : true;
    	 $http.get(url).success(function(result) 
    	{
    		 console.log(result);
    		 $scope.query2Result = result;
    	 })
       .error(function() 
       {
    	   alert("error in query1");	            
       });
    	
    	
    	console.log("here");
    }
    $scope.toggleQuery=function(inp) 
    {
    	//$scope.inp = $scope.inp ? false : true;
    	
    	console.log(inp);
    	console.log($scope[inp]); 
    	if ($scope[inp] === undefined) {
    		   //Valid in my application for first usage
    		   $scope[inp] = true;
    		} else {
    		   $scope[inp] = !$scope[inp];
    		}
    	
    }
    $scope.requestsReceived=function () 
    {
    
    	var startDate = $scope.startDate;
    	var endDate = $scope.endDate;
    	
    	console.log( $scope.startDate);
    	console.log( $scope.endDate);
    	var url = REQUEST_URL_BASE + "/noofrequestsreceived?startDate="+startDate+"&endDate="+endDate;
    	//$scope.myVar=true;
    	$scope.query3result = true;
    	 $http.get(url).success(function(result) 
    	{
    		 console.log(result);
    		 $scope.query3Result = result;
    	 })
       .error(function() 
       {
    	   alert("error in query3");	            
       });
    	
    	
    	console.log("here");
    }
    
  
    
    $scope.requestsCompleted=function () 
    {
    
    	var startDate = $scope.startDate;
    	var endDate = $scope.endDate;
    	
    	console.log( $scope.startDate);
    	console.log( $scope.endDate);
    	var url = REQUEST_URL_BASE + "/noofrequestscompleted?startDate="+startDate+"&endDate="+endDate;
    	//$scope.myVar=true;
    	$scope.query4result = true;
    	 $http.get(url).success(function(result) 
    	{
    		 console.log(result);
    		 $scope.query4Result = result;
    	 })
       .error(function() 
       {
    	   alert("error in query4");	            
       });
    	
    	
    	console.log("here");
    }
    
    
    $scope.getPendingAssets=function()
    {
    	$scope.pendingTable = [];
    	$scope.pendingFlag = true;
    	console.log("station id in js "+$scope.stationId);
    	var stationId = $scope.stationId;
    	var url = STATION_URL_BASE + "/pendingassets?stationId=" + stationId;
    	
    	$http.get(url).success(function(result) 
    		   	{
    		   		var len = result.length;
    	    		
    	    		console.log(len);
    	    		console.log(result[0]);
    	    		for(var i=0;i<len;i++)
    	          	{
    	    			 $scope.pendingTable.push(result[i]);
    	    			 //data.nodes.push({name: result["assetStatuses"][i]["assetId"], status :result["assetStatuses"][i]["status"], nodes: []});
    	    			 
    	          	}
    	    		 
    		       })
    		       .error(function() {
    		           alert("error in requestId status");
    		           
    		       });
    	
    	
    }
    
   
    

})

