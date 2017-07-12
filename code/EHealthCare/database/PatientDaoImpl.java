package org.nimhans.EHealthCare.database;

import java.sql.ResultSet;
import java.sql.Statement;

import org.nimhans.EHealthCare.Loader;
import org.nimhans.EHealthCare.model.Patient;

public class PatientDaoImpl implements PatientDao 
{
	
	
	/* retrieves patient details for a given patientId */
	public Patient getPatientDetails(String patientID)
	{
		Patient patient = null;
		try
		{
			Statement st = Loader.getDbConnection().createStatement();
			String query = "select * from patient where uhid=" + patientID + ";";

			ResultSet rs = st.executeQuery(query);

			if(rs.next())
	    	{
	    		 String uhid = rs.getString("uhid");
	    		 String patientName = rs.getString("name");
	    		 int age = rs.getInt("age");
	    		 String sex = rs.getString("sex");
	    		 String hospName = rs.getString("hospital_name");
	    		 String neuro_no = rs.getString("neuro_no");
	    		 String doctor = rs.getString("doctor");
	    		 String deptName = rs.getString("department_name");
	    		 String unitName = rs.getString("unit_name");
	    		 String mrdNo = rs.getString("mrd_no");
	    		
	    		 patient = Loader.getLabFactory().getPatientObject(uhid, patientName, age, sex, hospName, neuro_no, doctor, deptName, unitName, mrdNo);
	    	}			
		}
		catch(Exception e)
		{
			System.out.println(e);		
		}
		return patient;
	}
}
