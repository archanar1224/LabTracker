package org.nimhans.EHealthCare.model.derived;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.database.IDGeneratorDaoImpl;
import org.nimhans.EHealthCare.database.RequestDaoImpl;
import org.nimhans.EHealthCare.model.AssetId;
import org.nimhans.EHealthCare.model.IDGenerator;
import org.nimhans.EHealthCare.model.Request;

public class NimhansIDGenerator implements IDGenerator{

	@Override
	public AssetId generateAssetID() {
		// TODO Auto-generated method stub
		return new AssetId("1");
	}
	
	/* Get the last root generated and calculates the next root and return it */
	public String generateNextRoot()
	{
		IDGeneratorDaoImpl idi = Loader.getLabFactory().getIdDao();
		/* Get current root (Last generated root) */
		String currentRoot = idi.getCurrentRoot();
		System.out.println("current root"+currentRoot);
		StringTokenizer st = new StringTokenizer(currentRoot,"/");  
	    int currentRootNumber = Integer.parseInt(st.nextToken());
	    String year = st.nextToken();
	    int nextRootNumber = currentRootNumber + 1;
	    System.out.println("in request class " + nextRootNumber);
	    /* check whether it is in database */
	    while(idi.isInDatabase(nextRootNumber+"/"+year))
	    {
	    	System.out.println("while " + nextRootNumber);
	    	nextRootNumber++;
	    }
	
		//Year handling 
		
		Calendar now=Calendar.getInstance();
		String currentYear = String.valueOf(now.get(Calendar.YEAR)).substring(2, 4);
		String result;
		/* If the year of last root and current year are same */
		if(year.equals(currentYear))
		    result =  String.valueOf(nextRootNumber)+"/"+year;
		/* If the year of last root and current year are not same */
		else
			result = "1/"+currentYear;
		
		return result;
	}
	
	/* If technician enters the root validate it */
	public Boolean validateRoot(Request request)
	{
		Boolean isValid = true;
		String rootID =  request.getRootID();
		//validate and update the currentNpbase
		IDGeneratorDaoImpl idi = Loader.getLabFactory().getIdDao();
		/*if(idi.isInDatabase(rootID))
		{
			System.out.println("Already in DB");
			isValid = false;
		}*/
		//else
		//{
			  //System.out.println("inside validate " + this.rootID);
			  String pattern = "^[1-9][0-9]*/[0-9][0-9]$";

		      // Create a Pattern object
		      Pattern r = Pattern.compile(pattern);

		      // Now create matcher object.
		      Matcher m = r.matcher(rootID);
		      /* validates the pattern */
		      if(rootID.matches(pattern))
		      {
		    	  StringTokenizer st = new StringTokenizer(rootID,"/");
		    	  int currentRootNumber = Integer.parseInt(st.nextToken());
		    	  String year = st.nextToken();
		    	  Calendar now = Calendar.getInstance();
		    	  String currentYear = String.valueOf(now.get(Calendar.YEAR)).substring(2, 4);
		    	  /* validates year */
		    	  if(!(year.equals(currentYear)))
		    	  {
		    		  System.out.println("wrong year");
		    		  isValid = false;
		    	  }
		      }
		      else
		      {
		    	  //wrong format
		    	  isValid = false;
		      }
			//}
		
		System.out.println(isValid);
		return isValid;
	}
	
	/* Map the root and requestId and saves the mapping */
	public boolean saveRoot(Request request)
	{
		String rootID =  request.getRootID();
		String reqID =  request.getReqID();
		IDGeneratorDaoImpl idi = Loader.getLabFactory().getIdDao();

		if(generateNextRoot().equals(rootID))
		{
			//System.out.println("before updating");
			idi.updateCurrentRoot(rootID);
		}
		
		idi.addLabRequest(rootID, reqID);
		return true;
	}
	

}