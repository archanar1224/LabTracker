package org.nimhans.EHealthCare.database;

import org.nimhans.EHealthCare.model.Patient;

public interface PatientDao 
{
	public Patient getPatientDetails(String patientID);
}
