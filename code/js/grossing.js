var app = angular.module('grossingStation', ['ui.bootstrap','ngCookies']);
app.controller('GrossController',function($scope,$http,$window,$cookieStore)
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
    
    $scope.getAssetDetails=function () {
   
    	//var tempString = $scope.npNumberValue + ";";
    	//var test = tempString.substring(0,tempString.indexOf(";"));
    	//$scope.npNumberValue=test;
        //var test= test1.replace("/","|").replace(" ","|");
    	//var test= $scope.npNumberValue.replace("/","|").replace(" ","|");
        
    	//console.log("8081 port");
        console.log("NP Number " +  $scope.npNumberValue);
        console.log(STATION_URL_BASE+"/scan?assetId="+$scope.npNumberValue+"&stationId=2");
        
        var assetId = $scope.npNumberValue;

        // Scan the asset
        $http.get(STATION_URL_BASE+"/scan?assetId="+encodeURIComponent(assetId)+"&stationId=2",{headers: {'Content-Type': 'application/json','Content-Type':'text/html'}})
            .then(function successCallback(response) {
                $scope.asset=response.data;
                console.log($scope.asset);
                $scope.populateTable();
                $scope.getPatientDetails($scope.asset.reqId);
                $scope.step1=false;
            },function errorCallBack(err) {
                if(err.status>=400 && err.status<=500)
                   alert("Invalid Scan. Please Scan Again");
                /*else
                   alert("Check your Internet Connection");*/
            })
    }

    // Retrieve the patient details
    $scope.getPatientDetails=function(request_id) {
        var root = $scope.npNumberValue.substring(0, $scope.npNumberValue.indexOf(":"));
        console.log($scope.npNumberValue);
        console.log(root);
        $http.get(REQUEST_URL_BASE+"/patientdetails?samplerequestid="+request_id)
            .success(function (data) {
                $scope.patient=data;
            });
    }

    $scope.alreadyExists=function() {
    /*    if($scope.asset.nextState!=2)
            alert("This is not tissue belonging to Grossing Station. Press Done to Refresh Page");
    */}
    $scope.number=1;
    
    // Add blocks
    $scope.generateBlocks=function() 
    {
        console.log($scope.npNumberValue+"hghg");
        var url=ASSET_URL_BASE+"/addblock?technician="+$scope.username+"&number="+$scope.number+"&stationId=2";
        console.log(url);
        $http.post(url,$scope.asset).success(function(data) {
        	console.log("successs");
            $scope.populateTable();
        })
        /*$scope.populateTable();*/
    }

    $scope.assetTable=[];

    $scope.populateTable=function () 
    {
    	var url = ASSET_URL_BASE+"/getassets?npBase="+$scope.npNumberValue;
    	console.log("entered populate " + url);
        $scope.assetTable=[];
        var test= $scope.npNumberValue;//.replace("/","|").replace(" ","|");
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
            if($scope.assetTable.length==0)
            	$scope.generateBlocks();
        });
    }

    
    $scope.edit = {};

    $scope.edit=function (editasset)
    {
    	console.log(editasset);
        //$('#editModal').modal('show');
        $scope.edit.npNumber = editasset.assetId;
        $scope.edit.fixative = editasset.fixative;
        $scope.edit.biopsy = editasset.biopsy;
        $scope.edit.assetType = editasset.assetType;
    }

    $scope.update=function(){
    	var biopsy = $scope.edit.biopsy;
        var fixative = $scope.edit.fixative;
        var assetId = $scope.edit.npNumber.id.value;
        console.log("III "+  $scope.edit.npNumber);
        var url = ASSET_URL_BASE + "/updateasset" + "?assetId=" + encodeURIComponent(assetId) + "&biopsy=" + biopsy + "&fixative=" + fixative ;
        
        console.log("URLupdate " +  url);
        $http.put(url ,{headers: {'Content-Type': 'application/json'}})
            .then(function successCallback(response) {
                $('#editModal').modal('hide');
                $scope.populateTable();
            	//window.location.reload();
            }, function errorCallback(response) {
            });
    }

    $scope.printAsset= function (printNp) {
       console.log("printing...");
        var array = [
            {
                Np_Number : printNp//.replace("|","/").replace("|"," ").replace("|"," ").replace("|"," ")
            }];
        console.log(array);  
        $scope.queue=$scope.queue.concat(array);
        //alasql('SELECT * INTO CSV("BlockNpNumber.csv",{headers:true}) FROM ?',[array]);
        
    };
    $scope.printAllAssets= function () {
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
    $scope.printQueue=function(){
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

    $scope.delete = function(asset){
    	
    	var deleteUser = $window.confirm('Are you absolutely sure you want to delete?');
        if(deleteUser){
        if(asset.currentState==2) // TODO
        {
        	
        	var assetId = asset.assetId.id.value; //.replace("/#/g", "-");
            var url = ASSET_URL_BASE + "/deleteasset" + "?assetId=" + encodeURIComponent(assetId);
            
            console.log("URL delete" +  url);
            $http.delete(url,{headers: {'Content-Type': 'application/json'}})
            .then(function successCallback(response) {
                $scope.populateTable();
                console.log("deleted...");
            }, function errorCallback(response) {
            });
        }
        else
            alert("The Block has already passed through Embedding. You are not allowed to delete it");
    }
    }

    $scope.scanScreen=function () {
        $scope.task = false;
        
        $scope.pendingTissue=false;
        $scope.completedTissue=false;
        $scope.scanTissue=true;
    }
    $scope.scanTissue=true;
    $scope.assetTasksTable=[];
    
    $scope.getPendingTasks=function ()
    {	
        $scope.scanTissue=false;
        $scope.pendingTissue=true;
        $scope.completedTissue=false;
        $scope.label="Pending Assets";
        $scope.assetTasksTable=[];
        $scope.task=true;
        var url=STATION_URL_BASE+"/pendingassets?stationId=2";
        console.log(url);
        $http.get(url)
            .success(function (data) {
                $scope.assetTasksTable=data;
                console.log($scope.assetTable);
            })
    }

    $scope.getCompletedTasks=function()
    {	
    	$scope.scanTissue=false;
        $scope.completedTissue=true;
        $scope.pendingTissue=false;
    	$scope.label="Completed Assets";
        $scope.assetTasksTable=[];
        $scope.task=true;
        var url=STATION_URL_BASE+"/completed?stationId=2";
        $http.get(url)
            .success(function (data) {
                $scope.assetTasksTable=data;
            })
    }

});
