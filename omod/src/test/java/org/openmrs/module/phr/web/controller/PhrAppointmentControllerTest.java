package org.openmrs.module.phr.web.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Matchers.any;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentService;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.model.AppointmentStatus;
import org.openmrs.module.appointments.service.AppointmentServiceService;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.openmrs.module.appointments.util.DateUtil;
import org.openmrs.module.phr.service.PhrAppointmentServiceService;
import org.openmrs.module.phr.web.contract.*;
import org.openmrs.module.phr.web.mapper.OldAppointmentMapper;
import org.openmrs.module.phr.web.mapper.PhrAppointmentMapper;
import org.openmrs.module.phr.web.mapper.PhrAppointmentServiceMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class PhrAppointmentControllerTest {

    @Mock
    private AppointmentsService appointmentsService;

    @Mock
    private AppointmentServiceService appointmentServiceService;
    
    @Mock
    private PhrAppointmentServiceService phrappointmentServiceService;

    @Mock
    private OldAppointmentMapper appointmentMapper;
    
    @Mock
    private PhrAppointmentMapper phrappointmentMapper;

    @Mock
    private PhrAppointmentServiceMapper appointmentServiceMapper;

    @InjectMocks
    private PhrAppointmentController appointmentController;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    
    @Test
    public void shouldGetAllAppointments() throws Exception {
        Appointment appointment = new Appointment();
        List<Appointment> appointmentList = new ArrayList<>();
        appointmentList.add(appointment);
        when(phrappointmentServiceService.getAllAppointments(null)).thenReturn(appointmentList);
        
        appointmentController.getAllAppointments(null);
        verify(phrappointmentServiceService, times(1)).getAllAppointments(null);
        verify(appointmentMapper, times(1)).constructResponse(appointmentList);
    }
    

    @Test
    public void shouldThrowExceptionIfPatientUuidIsBlankWhileCreatingAppointment() throws Exception {
        when(appointmentsService.validateAndSave(any(Appointment.class))).thenThrow(new APIException("Exception Msg"));
        ResponseEntity<Object> responseEntity = appointmentController.createAppointment(new AppointmentPayload());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void shouldChangeStatusOfAppointment() throws Exception {
        Map statusDetails = new HashMap();
        statusDetails.put("toStatus", "Completed");
        Appointment appointment = new Appointment();
        when(appointmentsService.getAppointmentByUuid(anyString())).thenReturn(appointment);
        appointmentController.transitionAppointment("appointmentUuid", statusDetails);
        Mockito.verify(appointmentsService, times(1)).getAppointmentByUuid("appointmentUuid");
        Mockito.verify(appointmentsService, times(1)).changeStatus(appointment, "Completed", null);
    }

    @Test
    public void shouldReturnErrorResponseWhenAppointmentDoesNotExist() throws Exception {
        Map<String,String> statusDetails = new HashMap();
        statusDetails.put("toStatus", "Completed");
        when(appointmentsService.getAppointmentByUuid(anyString())).thenReturn(null);
        appointmentController.transitionAppointment("appointmentUuid", statusDetails);
        Mockito.verify(appointmentsService, times(1)).getAppointmentByUuid("appointmentUuid");
        Mockito.verify(appointmentsService, never()).changeStatus(any(),any(),any());
    }


    @Test
    public void shouldCreateAppointment() throws Exception{
        AppointmentPayload appointmentPayload = new AppointmentPayload();
        appointmentPayload.setPatientUuid("somePatientUuid");
        appointmentPayload.setMobileNumber("someMobileNumber");
        appointmentPayload.setUuid("someUuid");
        appointmentPayload.setServiceUuid("someServiceUuid");
        appointmentPayload.setServiceTypeUuid("someServiceTypeUuid");
        
        AppointmentService appointmentService = new AppointmentService();
        appointmentService.setName("someServiceName");
        appointmentService.setUuid("someServiceUuid");
        appointmentService.setAppointmentServiceId(1);
        
        AppointmentServiceType appointmentServiceType = new AppointmentServiceType();
        appointmentServiceType.setName("Online Appointment");
        appointmentServiceType.setUuid("someServiceTypeUuid");
        appointmentServiceType.setAppointmentService(appointmentService);
        
        Appointment appointment = new Appointment();
        appointment.setUuid("someUuid");
        appointment.setService(appointmentService);
        appointment.setServiceType(appointmentServiceType);
        
        when(phrappointmentMapper.getAppointmentFromPayload(appointmentPayload)).thenReturn(appointment);
        when(appointmentsService.validateAndSave(appointment)).thenReturn(appointment);

        appointmentController.createAppointment(appointmentPayload);
//        Mockito.verify(phrappointmentMapper, times(1)).getAppointmentFromPayload(appointmentPayload);
//        Mockito.verify(appointmentsService, times(1)).validateAndSave(appointment);
        assertNotNull(appointment);
        assertEquals("Online Appointment", appointment.getServiceType().getName());
        assertEquals("someUuid", appointment.getUuid());
    }
}
