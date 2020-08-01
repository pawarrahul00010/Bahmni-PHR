package org.openmrs.module.phr.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.phr.dao.PhrAppointmentServiceDao;
import org.openmrs.module.appointments.model.*;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.openmrs.module.appointments.service.impl.AppointmentServiceServiceImpl;
import org.openmrs.module.appointments.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({Context.class})
@RunWith(PowerMockRunner.class)
public class PhrAppointmentServiceServiceImplTest{

    @Captor
    private ArgumentCaptor<AppointmentService> captor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private PhrAppointmentServiceDao appointmentServiceDao;

    @Mock
    private AppointmentsService appointmentsService;

    @InjectMocks
    PhrAppointmentServiceServiceImpl appointmentServiceService;

    private User authenticatedUser;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockStatic(Context.class);
        authenticatedUser = new User(8);
        PowerMockito.when(Context.getAuthenticatedUser()).thenReturn(authenticatedUser);
    }

    @Test
    public void testGetAllAppointments() throws Exception {
    	Date startDateTime = DateUtil.convertToLocalDateFromUTC("2108-08-14T18:30:00.0Z");
        appointmentServiceService.getAllAppointments(startDateTime);
        Mockito.verify(appointmentServiceDao, times(1)).getAllAppointments(startDateTime);
    }
    
    @Test
    public void TestGetAppointmentServiceTypeByName() {

    	boolean includeVoided = false;
        AppointmentService appointmentService = new AppointmentService();
        appointmentService.setUuid("service1Uuid");
        appointmentService.setAppointmentServiceId(1);

        AppointmentServiceType appointmentServiceType1 = new AppointmentServiceType();
        appointmentServiceType1.setName("Online Appointment");
        appointmentServiceType1.setDuration(15);
        appointmentServiceType1.setUuid("serviceTypeUuid1");
        appointmentServiceType1.setAppointmentService(appointmentService);
        appointmentServiceType1.setId(1);
       
        when(appointmentServiceDao.getAppointmentServiceTypeByName(1, "Online Appointment")).thenReturn(appointmentServiceType1);

        AppointmentServiceType returnedAppointmentServiceType = appointmentServiceService.getAppointmentServiceTypeByName(1, "Online Appointment");

        assertEquals("serviceTypeUuid1", returnedAppointmentServiceType.getUuid());
        assertNotNull(returnedAppointmentServiceType);
        assertEquals("Online Appointment", returnedAppointmentServiceType.getName());

    }
   
    @Test
    public void testGetAppointmentServiceTypeByUuid() throws Exception {
        appointmentServiceService.getAppointmentServiceTypeByUuid("uuid");
        Mockito.verify(appointmentServiceDao, times(1)).getAppointmentServiceTypeByUuid("uuid");
    }
    
    @Test
    public void testGetAppointmentPatientByUuid() {
    	
    	appointmentServiceService.getAppointmentPatientByUuid("uuid");
    	Mockito.verify(appointmentServiceDao, times(1)).getAppointmentPatientByUuid("uuid");	
    }
    
    @Test
    public void shouldCreatePatient() throws Exception{
    	AppointmentPatient appointmentPatient = new AppointmentPatient();
    	appointmentPatient.setUuid("uuid");
    	appointmentPatient.setFirstName("firstname");
    	appointmentPatient.setLastName("lastname");
    	 String mobileNumber = "9999999999";
    	 appointmentServiceDao.createPatient(appointmentPatient, mobileNumber);
    	verify(appointmentServiceDao, times(1)).createPatient(appointmentPatient, mobileNumber);
    }
    
}
