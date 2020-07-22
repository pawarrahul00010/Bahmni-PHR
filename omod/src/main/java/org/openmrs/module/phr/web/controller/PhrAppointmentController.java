package org.openmrs.module.phr.web.controller;

import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentService;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.model.AppointmentStatus;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.openmrs.module.appointments.util.DateUtil;
import org.openmrs.module.phr.web.contract.AppointmentDefaultResponse;
import org.openmrs.module.phr.service.PhrAppointmentServiceService;
import org.openmrs.module.phr.web.contract.AppointmentCount;
import org.openmrs.module.phr.web.contract.AppointmentsSummary;
import org.openmrs.module.phr.web.contract.AppointmentPayload;
import org.openmrs.module.phr.web.mapper.PhrAppointmentMapper;
import org.openmrs.module.phr.web.mapper.OldAppointmentMapper;
import org.openmrs.module.phr.web.mapper.PhrAppointmentServiceMapper;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.openmrs.module.appointments.service.AppointmentServiceService;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/phrappointment")
public class PhrAppointmentController {

	private static final Logger logger = LoggerFactory.getLogger(PhrAppointmentController.class);
	
    @Autowired
    private AppointmentsService appointmentsService;
    
    @Autowired
    private AppointmentServiceService appointmentServiceService;

    @Autowired
    private PhrAppointmentMapper phrappointmentMapper;
    
    @Autowired
    private OldAppointmentMapper appointmentMapper;
    
    @Autowired
    PhrAppointmentServiceService phrAppointmentServiceService;
    
    @Autowired
    private PhrAppointmentServiceMapper appointmentServiceMapper;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> createAppointment(@Valid @RequestBody AppointmentPayload appointmentPayload) throws IOException {
        try {
        	
        	AppointmentService appointmentService = appointmentServiceService.getAppointmentServiceByUuid(appointmentPayload.getServiceUuid());
        	
        	if(appointmentService == null){
                throw new RuntimeException("Appointment Service does not exist");
            }
 
            Appointment appointment = phrappointmentMapper.getAppointmentFromPayload(appointmentPayload);
            appointmentsService.validateAndSave(appointment);
            phrAppointmentServiceService.sendMsg(appointment);
            return new ResponseEntity<>(appointmentMapper.constructResponse(appointment), HttpStatus.OK);
        }catch (RuntimeException e) {
            return new ResponseEntity<>(RestUtil.wrapErrorResponse(e, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    

	@RequestMapping(method = RequestMethod.GET, value = "all")
    @ResponseBody
    public List<AppointmentDefaultResponse> getAllAppointments(@RequestParam(value = "forDate", required = false) String forDate) throws ParseException {
        List<Appointment> appointments = phrAppointmentServiceService.getAllAppointments(DateUtil.convertToLocalDateFromUTC(forDate));
        if(appointments == null){
            throw new RuntimeException("Appointment does not exist");
        }
		 
        return appointmentMapper.constructResponse(appointments);
    }  

    
}
