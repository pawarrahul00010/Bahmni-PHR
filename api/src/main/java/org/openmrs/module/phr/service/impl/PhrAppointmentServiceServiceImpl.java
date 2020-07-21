package org.openmrs.module.phr.service.impl;

import org.openmrs.api.context.Context;
import java.util.regex.Matcher; 
import java.util.regex.Pattern; 
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.model.AppointmentPatient;
import org.openmrs.module.phr.dao.PhrAppointmentServiceDao;
import org.openmrs.module.phr.service.PhrAppointmentServiceService;
import org.openmrs.module.phr.util.SendSMS;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.openmrs.module.phr.util.SendSMS;
import org.openmrs.module.phr.util.SendMail;

import java.util.*;
import java.util.stream.Collectors;
@Component
@Transactional
public class PhrAppointmentServiceServiceImpl implements PhrAppointmentServiceService {

    private PhrAppointmentServiceDao phrAppointmentServiceDao;
    
    @Autowired
	private SendSMS sms;
    
    @Autowired
	private SendMail mail;
    

    public void setPhrAppointmentServiceDao(PhrAppointmentServiceDao appointmentServiceDao) {
        this.phrAppointmentServiceDao = appointmentServiceDao;
    }
 
    @Override
    public AppointmentServiceType getAppointmentServiceTypeByUuid(String serviceTypeUuid) {
        return phrAppointmentServiceDao.getAppointmentServiceTypeByUuid(serviceTypeUuid);
    }
    
    @Override
    public AppointmentServiceType getAppointmentServiceTypeByName(int serviceId, String serviceTypeName) {
        return phrAppointmentServiceDao.getAppointmentServiceTypeByName(serviceId , serviceTypeName);
    }
    
	/*    @Override
	public AppointmentService getAppointmentServiceByUuid(String uuid) {
	    AppointmentService appointmentService = phrAppointmentServiceDao.getAppointmentServiceByUuid(uuid);
	    return appointmentService;
	}*/
    
    @Override
    public List<Appointment> getAllAppointments(Date forDate) {
        List<Appointment> appointments = phrAppointmentServiceDao.getAllAppointments(forDate);
        return appointments.stream().filter(appointment -> !isServiceOrServiceTypeVoided(appointment)).collect(Collectors.toList());
    }
    
    private boolean isServiceOrServiceTypeVoided(Appointment appointment){
        return (appointment.getService() != null && appointment.getService().getVoided()) ||
               (appointment.getServiceType() != null && appointment.getServiceType().getVoided());
    }
    
    @Override
    public AppointmentPatient createPatient(String name) {
    	AppointmentPatient patient = new AppointmentPatient();
    	Map<String, String> nameMapping = getnames(name);
    	patient.setFirstName(nameMapping.get("firstname"));
    	patient.setLastName(nameMapping.get("lastname"));
    	return phrAppointmentServiceDao.createPatient(patient);
    }

    Map<String, String> getnames(String name){
    	Map<String, String> nameMap = new HashMap<String, String>();
		String[] names = name.split(" ");
		nameMap.put("firstname", names[0]);
		nameMap.put("lastname", names[names.length-1]);
		

		return nameMap;
    }

    @Override
    public AppointmentPatient getAppointmentPatientByUuid(String uuid) {
    	
    	AppointmentPatient appointmentPatient = phrAppointmentServiceDao.getAppointmentPatientByUuid(uuid);
    	return appointmentPatient;	
    }

	@Override
	public void sendMsg(Appointment appointment) {
		
        String email = "bahmniphr@gmail.com"; 
        String msg = getMsg(appointment);
        
        if (isValid(email)) {
        	String subject = "Bahmni PHR appointment";
         
        	mail.notify(email, subject, msg);
        }
        else { 
        	//sms.sendSms(String.valueOf(mobileNumber), msg);
        }
	}
	private String getMsg(Appointment a) {
		
	    String patientname = null;
	    if(a.getPatient() != null) {
	    	
	    	patientname = a.getPatient().getPersonName().getFullName();
	    }
	    if(a.getAppointmentPatient() != null) {
	    	
	    	patientname = a.getAppointmentPatient().getFirstName()+" "+a.getAppointmentPatient().getLastName();
	    }

		if(a.getProvider()!=null)
	    	return "Dear "+patientname+", your appointment has been successfully booked at Bahmni Hospital"
			    		+"\n"+a.getStartDateTime()
						+"\n"+a.getService().getName()
						+"\n"+a.getProvider().getName();
	    else
	    	return "Dear "+patientname+", your appointment has been successfully booked at Bahmni Hospital"
			    		+"\n"+a.getStartDateTime()
						+"\n"+a.getService().getName();      
		
	}

	public static boolean isValid(String email) 
    { 
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
                            "[a-zA-Z0-9_+&*-]+)*@" + 
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                            "A-Z]{2,7}$"; 
                              
        Pattern pat = Pattern.compile(emailRegex); 
        if (email == null) 
            return false; 
        return pat.matcher(email).matches(); 
    } 

}
