package org.openmrs.module.phr.web.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.appointments.model.*;
import org.openmrs.module.appointments.service.AppointmentServiceService;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.openmrs.module.phr.web.contract.AppointmentDefaultResponse;
import org.openmrs.module.phr.web.contract.AppointmentPayload;
import org.openmrs.module.phr.web.contract.AppointmentQuery;
import org.openmrs.module.phr.web.extension.AppointmentResponseExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openmrs.module.phr.web.mapper.*;
import org.openmrs.module.phr.service.PhrAppointmentServiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

@Component
public class PhrAppointmentMapper {
	
	private static final Logger logger = LoggerFactory.getLogger(PhrAppointmentMapper.class);
	
    @Autowired
    LocationService locationService;

    @Autowired
    ProviderService providerService;

    @Autowired
    PatientService patientService;

    @Autowired
    AppointmentServiceService appointmentServiceService;

    @Autowired
    PhrAppointmentServiceService phrAppointmentServiceService;
    
    @Autowired
    AppointmentsService appointmentsService;

    @Autowired(required = false)
    AppointmentResponseExtension appointmentResponseExtension;

    public Appointment getAppointmentFromPayload(AppointmentPayload appointmentPayload) {
        Appointment appointment;
        if (!StringUtils.isBlank(appointmentPayload.getUuid())) {
            appointment = appointmentsService.getAppointmentByUuid(appointmentPayload.getUuid());
        } else {
            appointment = new Appointment();
            String[] names = getnames(appointmentPayload.getPatientUuid());
            
            if(names.length==1) {
            	
            	Patient patient = patientService.getPatientByUuid(appointmentPayload.getPatientUuid());
            	AppointmentPatient appointmentPatient = phrAppointmentServiceService.getAppointmentPatientByUuid(appointmentPayload.getPatientUuid());
            
            	if(patient !=null) {
            		
            		appointment.setPatient(patient);
            	
            	}else if(appointmentPatient !=null) {
            		
            		appointment.setAppointmentPatient(phrAppointmentServiceService.getAppointmentPatientByUuid(appointmentPayload.getPatientUuid()));
            	
            	}else {
            	
            		appointment.setAppointmentPatient(phrAppointmentServiceService.createPatient(appointmentPayload.getPatientUuid()));
            	}
            }else {
            
            	appointment.setAppointmentPatient(phrAppointmentServiceService.createPatient(appointmentPayload.getPatientUuid()));
            }
        }
        AppointmentService appointmentService = appointmentServiceService.getAppointmentServiceByUuid(appointmentPayload.getServiceUuid());
        AppointmentServiceType appointmentServiceType = null;
        
        appointmentServiceType = getServiceTypeByNameAndServiceId(appointmentService.getAppointmentServiceId());
		
        appointment.setServiceType(appointmentServiceType);
        appointment.setService(appointmentService);
        appointment.setProvider(providerService.getProviderByUuid(appointmentPayload.getProviderUuid()));
        appointment.setLocation(locationService.getLocationByUuid(appointmentPayload.getLocationUuid()));
        appointment.setStartDateTime(appointmentPayload.getStartDateTime());
        appointment.setEndDateTime(appointmentPayload.getEndDateTime());
        appointment.setAppointmentKind(AppointmentKind.valueOf(appointmentPayload.getAppointmentKind()));
        appointment.setComments(appointmentPayload.getComments());

        return appointment;
    }

    private AppointmentServiceType getServiceTypeByUuid(Set<AppointmentServiceType> serviceTypes, String serviceTypeUuid) {
        return serviceTypes.stream()
                .filter(avb -> avb.getUuid().equals(serviceTypeUuid)).findAny().get();
    }
    
    private AppointmentServiceType getServiceTypeByName(Set<AppointmentServiceType> serviceTypes, String serviceTypeUuid) {
        return serviceTypes.stream()
                .filter(avb -> avb.getName().equals("Online Appointment")).findAny().get();
    }
    
   AppointmentServiceType getServiceTypeByNameAndServiceId(int ServiceId){
    	
    	AppointmentServiceType serviceType = phrAppointmentServiceService.getAppointmentServiceTypeByName(ServiceId, "Online Appointment");
    	
    	return serviceType;
    	
    }
   
   String[] getnames(String name){
		String[] names = name.split(" ");
		return names;
   }

}
