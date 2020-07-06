package org.openmrs.module.phr.service;


import org.openmrs.annotation.Authorized;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentService;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface PhrAppointmentServiceService {

 
    @Transactional
    @Authorized({"View Appointment Services"})
    AppointmentServiceType getAppointmentServiceTypeByUuid(String serviceTypeUuid);
    
    @Transactional
    @Authorized({"View Appointment Services"})
    AppointmentServiceType getAppointmentServiceTypeByName(int serviceId, String serviceTypeName);
    
	/*   @Transactional
	@Authorized({"View Appointment Services"})
	public AppointmentService getAppointmentServiceByUuid(String uuid);
	*/
    @Transactional
    @Authorized({"View Appointment Services"})
    public List<Appointment> getAllAppointments(Date forDate);
 }

