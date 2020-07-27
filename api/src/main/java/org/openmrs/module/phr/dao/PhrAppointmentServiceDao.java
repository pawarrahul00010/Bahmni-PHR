package org.openmrs.module.phr.dao;

import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.model.AppointmentService;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentPatient;

import java.util.Date;
import java.util.List;

public interface PhrAppointmentServiceDao {

    AppointmentServiceType getAppointmentServiceTypeByUuid(String uuid);
    
    AppointmentServiceType getAppointmentServiceTypeByName(int serviceId, String serviceTypeName);
    
    public AppointmentService getAppointmentServiceByUuid(String uuid);
    
    public List<Appointment> getAllAppointments(Date forDate);
    
    public AppointmentPatient createPatient(AppointmentPatient patient, String mobileNumber);
    
    public AppointmentPatient getAppointmentPatientByUuid(String uuid);
    

}
