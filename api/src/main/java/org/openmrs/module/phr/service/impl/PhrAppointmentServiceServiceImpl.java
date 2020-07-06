package org.openmrs.module.phr.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.phr.dao.PhrAppointmentServiceDao;
import org.openmrs.module.phr.service.PhrAppointmentServiceService;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
@Component
@Transactional
public class PhrAppointmentServiceServiceImpl implements PhrAppointmentServiceService {

    private PhrAppointmentServiceDao phrAppointmentServiceDao;
    

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

    

}
