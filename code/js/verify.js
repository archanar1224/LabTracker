var app = angular.module('receivingStation', ['ui.bootstrap','ngCookies']);
app.controller('VerifyController',function($scope,$http,$window,$cookieStore)
{
    //var URL_BASE="http://10.11.3.3:8080/Sample_Tracker/webapi/";
	//var URL_BASE="http://pushd.healthelife.in:8080/Sample_Tracker/webapi/";
    var URL_BASE="http://localhost:8080/EHealthCare/webapi/";
    
    
    var REQUEST_URL_BASE = URL_BASE+ "requestservice";
    var ASSET_URL_BASE = URL_BASE + "assetservice";
    var TECHNICIAN_URL_BASE = URL_BASE + "userservice";
   // console.log("PPP " +serverURL);
    $scope.barcodeValue='';
    $scope.patient='';
    $scope.username=$cookieStore.get('username');

    $scope.receivingStationStep=0;
    $scope.linkingStep=0;
    //Step =0 Scan Barcode
    //Step >=1 Verify patient details
    //Step >= 2 Verify NP Number and edit option
    //Step >=3 Only Verify Np Number

    $scope.updateNpBase =0;
    //1 if chance to update

    $scope.setUsername=function() {
        //console.log("here I am !");
        //console.log("PPP " +serverURL);
        $cookieStore.put('username',$scope.username);
        console.log("username: "+$cookieStore.get('username'));
        $('#loginModal').modal('hide');
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
       // alert("Errors in loading technicians");  //Modified : CHECK!!!!
        
    });


    $scope.myVar=false;
    
    $scope.getPatientDetails=function () {
    	$scope.myVar=true;
        //console.log("Hey I am");
        $http.get(REQUEST_URL_BASE+"/patientdetails?samplerequestid="+$scope.barcodeValue)
        .success(function(data) {
            $scope.patient=data;
            console.log($scope.patient);
            $scope.receivingStationStep=1;
            //$scope.getSuggestion();
            $scope.linkingStep = 1;
            $scope.myVar=false;
            /*if($scope.patient.uniqueNpBase!=null)
            	$scope.getSuggestion();*/
            
            console.log($scope.receivingStationStep)
        })
        .error(function(data) {
            alert("Please Scan the Barcode Again");
            $scope.myVar=false;
            //console.log($scope.receivingStationStep)
        });
    }

    $scope.npSuggestion='';
    $scope.root='';
    $scope.oldRequests={};
    $scope.getSuggestion=function() {
        if($scope.patient.npBase==null){
            $http.get(REQUEST_URL_BASE+"/nextroot?requestid="+$scope.barcodeValue)
                .success(function(data) {
                	console.log($scope.receivingStationStep)
                    $scope.receivingStationStep=2;
                    $scope.npSuggestion=data['nextRoot'];
                    $scope.npNumber=data['nextRoot'];
                    //console.log("heyyyy")
                    $scope.updateNpNumber(data['isRequestPresent']);
                    console.log($scope.npNumber)
                	if($scope.patient.uniqueNpBase!=null){
                		//alert("This patient already has an npNumber "+$scope.patient.uniqueNpBase);
                		$http.get(REQUEST_URL_BASE+"/withUhid?uhid="+$scope.patient.UHID)
                		.success(function(data) {
                		$scope.root = data;
                		//if($scope.root.isPresent)
                			//$scope.receivingStationStep = 5;
                    	//console.log(data);
                    	$scope.oldRequests=data;
                		//$scope.oldRequests=$scope.root.root;
                    	$('#oldNpModal').modal('show');
                		})
                	}
                    
                })
        }
        else{
            $scope.receivingStationStep=4;
            $scope.npSuggestion=$scope.patient.npBase;
            $scope.npNumber=$scope.patient.npBase;
            $scope.populateTable();
            $scope.checkNpNumber();
            flag=false;
        }
    
    }
    $scope.npNum = "Create new";
    $scope.setNpNumber = function (){
    	
    	if($scope.npNum=="Create new")
    		$('#oldNpModal').modal('hide');
    	else
    		$scope.putPatient();
    	$('#oldNpModal').modal('hide');
    }
    $scope.getUpdatedDetails = function(){
    	$scope.myVar=true;
       // console.log("Hey I am");
        $http.get(REQUEST_URL_BASE+"/soap?samplerequestid="+$scope.barcodeValue)
        .success(function(data) {
            $scope.patient=data;
            console.log($scope.patient);
            $scope.receivingStationStep=1;
            $scope.linkingStep = 1;
            $scope.myVar=false;
            $scope.getSuggestion();
        })
        .error(function(data) {
            alert("Not Updated");
            $scope.myVar=false;
        });
    }
    $scope.verify = function (){
    	$scope.linkingStep=2;
    }
    $scope.unLinked = {};
    $scope.getUnlinked = function(){
        $http.get(REQUEST_URL_BASE+"/unlinked")
        .success(function(data) {
        	$scope.unLinked = data;
        	console.log($scope.unLinked); 
        	if($scope.unLinked.length==0){
        		$('#unlinked').removeClass('btn-danger');
        		$('#unlinked').addClass('btn-success');
        	}
        	else
        	{	
        		$('#unlinked').removeClass('btn-success');
        		$('#unlinked').addClass('btn-danger');
        	}
        	
        		
        		
        })
    	
    };
    $scope.getUnlinked();

    $scope.pat = {};
    $scope.selectThis = function(pat){
    	
    	$('#readBarcode').removeClass('hidden');
    	$('#patientDetails').removeClass('hidden');
        $scope.patient='';
    	$scope.npNum=pat.npBase;
    	$scope.barcodeValue='';
    	$scope.receivingStationStep=0;
    	
    	
    }
    $scope.putPatient = function(){
    	
    	var requestData={
                npBase: $scope.npNum.toString(),
                sampleRequestId: $scope.barcodeValue,
                UHID: $scope.patient.UHID,
                doctor: $scope.patient.doctor,
                patientName: $scope.patient.patientName,
                patientAge: $scope.patient.age,
                patientSex: $scope.patient.sex
            };
    	$http.put(REQUEST_URL_BASE, requestData ,{headers: {'Content-Type': 'application/json'}})
        .then(function successCallback(response) {
        	console.log(requestData);
        	$scope.getUnlinked();
        	$scope.getUpdatedDetails();
        	$('#readBarcode').addClass('hidden');
        	$('#patientDetails').addClass('hidden');

        }, function errorCallback(response) {
        });
    	
    	
    }
    
    $scope.patient = {};
    $scope.checkboxModel = false;
    $scope.unlinkPerson = function()
    {	
    	//alert($scope.checkboxModel);
    	if($scope.checkboxModel==false)
    	{
    	//alert("check box clicked");
    	$scope.patient.UHID = "Unlinked";
    	$scope.patient.npBase==null;
    	$scope.patient.external=1;
    	$scope.barcodeValue = "Unlinked";
    	$scope.getSuggestion();
    	}else{
    		$scope.patient.UHID = '';
    		$scope.npNumber = '';
    		$scope.npSuggestion = '';
    		$scope.barcodeValue = '';
    	}
    	
    }
    $scope.regex= /^X?[1-9]\d*[/][0-9]{2}$/;
    console.log($scope.regex);
    $scope.npNumber='';
    var npBaseFlag = false;
    $scope.checkNpNumber=function()
    {	
    	$scope.npNumber=$scope.npNumber.toUpperCase();
    	//console.log("testing");
    	//console.log("num : "+ $scope.npNumber + " Sugg " + $scope.npSuggestion);
    	//alert($scope.regex.test($scope.npNumber));
    	//if($scope.regex.test($scope.npNumber)){
    	if(true){
    	if($scope.npNumber=='')
            $scope.npNumber=$scope.npSuggestion;
    	//console.log("num : "+ $scope.npNumber + " Sugg " + $scope.npSuggestion);
    	if($scope.npNumber != $scope.npSuggestion)
    	{
    		console.log("Both not same");
    		npBaseFlag = true;
    	}
        if($scope.patient.npBase==null){  
        	var confirm = $window.confirm('Are you sure to assign this NP Number : '+$scope.npNumber);
            $scope.updateNpBase =0;
            var noOfslashs = $scope.npNumber.replace(/[^/]/g, '').length
            //alert(noOfslashs);
            var requestData={
                rootID: $scope.npNumber.toString(),
                reqID: $scope.barcodeValue,
                //patientUHID: $scope.patient.UHID,
               // doctor: $scope.patient.doctor,
                //patientName: $scope.patient.patientName,
                //patientAge: $scope.patient.age,
                //patientSex: $scope.patient.sex
            };
           //console.log(npBaseFlag)
           if(confirm){
            $http.post(REQUEST_URL_BASE+"?stationId=1", requestData ,{headers: {'Content-Type': 'application/json'}})
                .then(function successCallback(response) {
                	console.log("npBase is null,data saved");
                    console.log(response);
                    $scope.receivingStationStep=4;
                    $scope.getUnlinked();
                    //$scope.create();
                }, function errorCallback(response) {
                    $scope.getSuggestion();
                    flag=true;
                    alert("Invalid NP Number !!! ");
                    $scope.asset.biopsy = '';
                    $scope.asset.specimen = "Tissue";
                    $scope.asset.fixative = '';
                });
            }
            else
            	$scope.updateNpBase =1;
        }
        else{
            $scope.receivingStationStep=4;
            $scope.populateTable();
        }
    	}
    	else
    		alert("Invalid NP Number");
        
    }
    $scope.disableProceed=0;
    $scope.updateNpNumber=function(flag) {
        //This variable allows the data field to be updated.
   
        $scope.updateNpBase =1;
        if(flag)
        {
        	//$scope.disableProceed=1;
        	$scope.receivingStationStep=4;
        }
        //else
        	//$scope.disableProceed=0;
        $scope.populateTable();
    }

    $scope.assetTable=[];
    $scope.populateTable=function() {
    	console.log("Inside populate")
        //Get the data from server of this NpBase and keep updating the table
        $http.get(ASSET_URL_BASE+"/getassets?npBase="+$scope.npNumber)
            .success(function(data) {
            	console.log("after get from populate")
                console.log(data);
                //$scope.assetTable=data;
                $scope.assetTable=[];
                for(var asset in data)
                { 	
                	// TODO : Changed this
            		//if(data[asset].npNumber.match(/^X?[0-9]*[/][0-9]{2}[:][0][0]/) || data[asset].npNumber.match(/^X?[0-9]*[/][0-9]{2}[:]\w+$/)){
            			//console.log($scope.assetTable[asset].npNumber.substring($scope.assetTable[asset].npNumber.indexOf(":")+1,$scope.assetTable[asset].npNumber.indexOf(":")+3));
                	
            			$scope.assetTable.push(data[asset]);
            		
            		//else
            			console.log(data[asset]);
                }
            })
            
            
    };
    var flag=true;
    $scope.save=function(){
    	/*for(var i=1;i<=$scope.asset.quantity;i++)
    	{
    	if(flag==true){
    		$scope.checkNpNumber();
    		flag=false;
    	}
    	else
    		$scope.create();
    	}*/
    	console.log("Inside save");
    	$scope.create();
    	console.log("after create");
    	$scope.asset.quantity=1;
    	//$scope.populateTable();
    }
    $scope.asset={};
    $scope.asset.quantity=1;
    $scope.asset.biopsy = '';
    $scope.asset.specimen = "Tissue";
    $scope.asset.fixative = '';
    $scope.asset.npNumber = '';
    $scope.asset.comment = '';
    
    //Create new Asset
    $scope.create=function() {
    	
    	//console.log("hey");
        var url = "";
        if (!($scope.asset.specimen == "" || $scope.asset.biopsy == "" )) {
            if((($scope.asset.specimen=="Block" || $scope.asset.specimen=="Stained Slide" || $scope.asset.specimen=="Unstained Slide") && $scope.asset.fixative == "")||$scope.asset.fixative != "")
            {
               console.log("creating ... ");
               url = ASSET_URL_BASE;
               var assetType;
               if ($scope.asset.specimen == "Tissue" || $scope.asset.specimen == "Fluid" || $scope.asset.specimen == "Frozen")
               {
            	   console.log("inside tissue");
            	   assetType = "tissue"
               }
           	   else if ($scope.asset.specimen == "Block")
           	   {
           		console.log("inside block");
           		   assetType = "block";
           	   }
           	   else if ($scope.asset.specimen == "Stained Slide" || $scope.asset.specimen == "Unstained Slide")
           	   {
           		console.log("inside slide");
           		   assetType = "slide";
           	   }
               
              	url = ASSET_URL_BASE + "?stationId=1&parent="+$scope.npNumber+"&np=" + $scope.npNumber+"&quantity="+$scope.asset.quantity+"&assetType="+assetType;
              	console.log("assetType"+assetType);
                var assetDetails = 
                {
                    biopsy: $scope.asset.biopsy,
                    fixative: $scope.asset.fixative,
                    specimen: $scope.asset.specimen,
                    technician: $scope.username,
                    remarks: $scope.asset.comment,
                    quantity: $scope.asset.quantity,
                    assetType: assetType,
                    reqId: $scope.barcodeValue
                };
                console.log("quantity" + $scope.asset.quantity);
                var assetDetailsJSON = JSON.stringify(assetDetails);
                console.log(url);
                $http.post(url, assetDetails, {headers: {'Content-Type': 'application/json'}})
                    .then(function successCallback(response) {
                        $('#insertModal').modal('hide');
                        console.log("printing assetDetails")
                        console.log(assetDetails);
                        $scope.populateTable();
                        //$scope.asset.biopsy = $scope.asset.specimen = $scope.asset.fixative = "";
                        $scope.asset.fixative = "";
                    }, function errorCallback(response) {
                    	console.log(response);
                    });
            }
            //$scope.asset.biopsy = $scope.asset.specimen = $scope.asset.fixative = "";
            $scope.asset.fixative = "";
        }
        else {alert("Complete all fields! ");}

    }

    $scope.delete = function(asset){
        // Found that # cannot pass as query parameter
        //Delete only if currentStation is 1
    	var deleteUser = $window.confirm('Are you absolutely sure you want to delete?');

       if(deleteUser){
        if(asset.currentFlowIndex == 0) //TODO check this part
        {
            //var npNumber = asset.npNumber.replace("#","-").replace("#","-").replace("#","-");
        	var assetId = asset.assetId.id.value; //.replace("/#/g", "-");
            var url = ASSET_URL_BASE + "/deleteasset" + "?assetId=" + encodeURIComponent(assetId);
            
            console.log("URL delete" +  url);
            $http.delete(url,{headers: {'Content-Type': 'application/json'}})
                .then(function successCallback(response) {
                    $scope.populateTable();
                    console.log("deleted...");
                }, function errorCallback(response) {
                });
            $scope.populateTable();
        }
        else 
            alert("The asset has already passed this Station. You are not allowed to delete");
       }
    }
    $scope.edit = {};

    $scope.edit=function (editasset)
    {
    	console.log(editasset);
        $('#editModal').modal('show');
        $scope.asset.npNumber = editasset.assetId;
        $scope.edit.fixative = editasset.fixative;
        $scope.edit.biopsy = editasset.biopsy;
        $scope.edit.assetType = editasset.assetType;
    }

    $scope.update=function(){
       /* var assetDetails={
        	assetId: $scope.asset.npNumber,
            biopsy: $scope.edit.biopsy,
            fixative: $scope.edit.fixative,
            type: $scope.edit.specimen
        };*/
        var biopsy = $scope.edit.biopsy;
        var fixative = $scope.edit.fixative;
        var assetId = $scope.asset.npNumber.id.value;
        console.log("III "+  $scope.asset.npNumber);
        var url = ASSET_URL_BASE + "/updateasset" + "?assetId=" + encodeURIComponent(assetId) + "&biopsy=" + biopsy + "&fixative=" + fixative ;
        //url = (url);
        console.log("URLupdate " +  url);
        $http.put(url ,{headers: {'Content-Type': 'application/json'}})
            .then(function successCallback(response) {
                $('#editModal').modal('hide');
                $scope.populateTable();
                $scope.biopsy = $scope.specimen = $scope.fixative = '';
            }, function errorCallback(response) {
            });
    }

    $scope.printAllAssets=function()
    {	var d= new Date();
		var mon=d.getMonth();
		if ($scope.assetTable[0].specimen == "Stained Slide" || $scope.assetTable[0].specimen == "Unstained Slide")
		    var type="Sli ("+$scope.assetTable.length+") "+$scope.assetTable[0].npNumber.charAt($scope.assetTable[0].npNumber.length-1)+"-"+$scope.assetTable[$scope.assetTable.length-1].npNumber.charAt($scope.assetTable[$scope.assetTable.length-1].npNumber.length-1);
		else if($scope.assetTable[0].specimen == "Block")
			var type=$scope.assetTable[0].specimen.substring(0, 3)+" ("+$scope.assetTable.length+") "+$scope.assetTable[0].npNumber.charAt($scope.assetTable[0].npNumber.length-1)+"-"+$scope.assetTable[$scope.assetTable.length-1].npNumber.charAt($scope.assetTable[$scope.assetTable.length-1].npNumber.length-1);
		else
			var type=$scope.assetTable[0].biopsy.substring(0, 3)+" ("+$scope.assetTable.length+") "+$scope.assetTable[0].npNumber.charAt($scope.assetTable[0].npNumber.length-1)+"-"+$scope.assetTable[$scope.assetTable.length-1].npNumber.charAt($scope.assetTable[$scope.assetTable.length-1].npNumber.length-1);
		mon++;
		var date=d.getDate()+"-"+mon+"-"+d.getFullYear();    
    	var array=[];
    	var tissue=[];
    	var slide=[];
    	var block=[];
    	console.log($scope.assetTable);
    	for(var asset in $scope.assetTable)
    	{	if($scope.assetTable[asset].specimen=="Stained Slide" || $scope.assetTable[asset].specimen == "Unstained Slide")
    			slide.push($scope.assetTable[asset]);
    		else if($scope.assetTable[asset].specimen == "Block")
    			block.push($scope.assetTable[asset]);
    		else
    			tissue.push($scope.assetTable[asset]);
    	
    		//console.log(asset);
    		array.push({
                    Np_Number : $scope.assetTable[asset].npNumber,//.replace("|","/").replace("|"," ").replace("|"," ").replace("|"," "),
                    UHID : $scope.patient.UHID,
                    Name : $scope.patient.patientName,
                    Date : date,
                    Age : $scope.patient.age,
                    Sex : $scope.patient.sex,
                    Type : $scope.assetTable[asset].biopsy.substring(0, 3),
                });
    	}
    	array.push({
    			Np_Number : $scope.assetTable[0].npNumber.substring(0,$scope.assetTable[0].npNumber.indexOf(":")),
    			UHID : $scope.patient.UHID,
                Name : $scope.patient.patientName,
                Date : date,
                Age : $scope.patient.age,
                Sex : $scope.patient.sex,
                Type : type
    		
    	})
    	array.push({
    			Np_Number : $scope.assetTable[0].npNumber.substring(0,$scope.assetTable[0].npNumber.indexOf(":")),
    			UHID : $scope.patient.UHID,
                Name : $scope.patient.patientName,
                Date : date,
                Age : $scope.patient.age,
                Sex : $scope.patient.sex,
                Type : type
    		
    	})
    	alasql('SELECT * INTO CSV("ReceivingStation.csv",{headers:true}) FROM ?',[array]);
    	
    }
    
    $scope.printAsset= function (printNp,index) {

    	var d= new Date();
    	var mon=d.getMonth();
    	mon++;
    	var date=d.getDate()+"-"+mon+"-"+d.getFullYear();    
    	console.log("type:"+$scope.assetTable[index].biopsy.substring(0, 3));
    	var array = [
                {
                	NP_Number : printNp,//.replace("|","/").replace("|"," ").replace("|"," ").replace("|"," "),
                    UHID : $scope.patient.UHID,
                    Name : $scope.patient.patientName,
                    Date : date,
                    Age : $scope.patient.age,
                    Sex : $scope.patient.sex.charAt(0),
                    Type : $scope.assetTable[index].biopsy.substring(0, 3)
                }];
            alasql('SELECT * INTO CSV("ReceivingStation.csv",{headers:true}) FROM ?',[array]);
        };
});