var app = angular.module('reportingStation', ['ui.bootstrap','ngCookies']);
app.controller('ReportController',function($scope,$http,$window,$cookieStore)
{

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
        //console.log("here I am !");
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
   
    
    $scope.scanRoot=function () 
    {
    	console.log(STATION_URL_BASE+"/scan?assetId="+$scope.rootValue+"&stationId=2");
    	//var url = STATION_URL_BASE+"/scan?assetId="+$scope.rootValue+"&stationId=2";
    	
    	var assetId = $scope.rootValue;

        $http.get(STATION_URL_BASE+"/scan?assetId="+encodeURIComponent(assetId)+"&stationId=7",{headers: {'Content-Type': 'application/json','Content-Type':'text/html'}})
            .then(function successCallback(response) {
            	alert("reporting successful");
                $scope.asset=response.data;
                console.log($scope.asset);
               // $scope.populateTable();
               // $scope.getPatientDetails($scope.asset.reqId);
                $scope.step1=false;
            },function errorCallBack(err) {
                if(err.status>=400 && err.status<=500)
                   alert("Invalid Scan. Please Scan Again");
                /*else
                   alert("Check your Internet Connection");*/
            })
    }
   

  

    $scope.assetTable=[];

    $scope.populateTable=function () 
    {
    	$scope.task = true;
    	var url = ASSET_URL_BASE+"/getassets?npBase="+$scope.rootValue;
    	console.log("entered populate " + url);
        $scope.assetTable=[];
        var test= $scope.rootValue;//.replace("/","|").replace(" ","|");
        $http.get(url)
            .success(function (data) {
            	
                for(var asset in data)
                { 	
                	// TODO : Changed this
            		//if(data[asset].npNumber.match(/^X?[0-9]*[/][0-9]{2}[:][0][0]/) || data[asset].npNumber.match(/^X?[0-9]*[/][0-9]{2}[:]\w+$/)){
            			//console.log($scope.assetTable[asset].npNumber.substring($scope.assetTable[asset].npNumber.indexOf(":")+1,$scope.assetTable[asset].npNumber.indexOf(":")+3));
                	
            			$scope.assetTable.push(data[asset]);
            		
            		//else
            			console.log(data[asset]);
                }
            console.log("data & length: "+data,$scope.assetTable.length);

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
