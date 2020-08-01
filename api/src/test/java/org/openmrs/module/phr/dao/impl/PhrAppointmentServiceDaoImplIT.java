package org.openmrs.module.phr.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.phr.BaseIntegrationTest;
import org.openmrs.module.phr.dao.PhrAppointmentServiceDao;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentPatient;
import org.openmrs.module.appointments.model.AppointmentService;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.model.AppointmentStatus;
import org.openmrs.module.appointments.model.ServiceWeeklyAvailability;
import org.openmrs.module.appointments.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.LinkedHashSet;

import static org.junit.Assert.*;

public class PhrAppointmentServiceDaoImplIT extends BaseIntegrationTest {

    @Autowired
    private PhrAppointmentServiceDao phrAppointmentServiceDao;

    @Before
    public void setUp() throws Exception {
        executeDataSet("appointmentServicesTestData.xml");
    }

    @Test
    public void shouldGetAppointmentPatientByUuid() throws Exception{
    	String appointmentPatientUuid = "a4d84b28-421e-406a-a455-cf0f20938799";
    	AppointmentPatient appointmentPatient = phrAppointmentServiceDao.getAppointmentPatientByUuid(appointmentPatientUuid);
    	assertNotNull(appointmentPatient);
        assertEquals(appointmentPatientUuid, appointmentPatient.getUuid());	
    }
    
    @Test
    public void shouldGetAppointmentServiceTypeByUuid() throws Exception {
    	AppointmentServiceType appointmentServiceType = phrAppointmentServiceDao.getAppointmentServiceTypeByUuid("c36006d5-9fcc-4f20-866b-0ece245615b1");
        assertNotNull(appointmentServiceType);
        assertEquals("c36006d5-9fcc-4f20-866b-0ece245615b1", appointmentServiceType.getUuid());
    }
    
    @Test
    public void shouldGetAppointmentServiceTypeByName() throws Exception {
    	int serviceId = 1;
    	String name = "Online Appointment";
    	AppointmentService appointmentService = new AppointmentService();
    	appointmentService.setAppointmentServiceId(serviceId);
    	AppointmentServiceType appointmentServiceType = phrAppointmentServiceDao.getAppointmentServiceTypeByName(serviceId, name);
    	assertNotNull(appointmentServiceType);
        assertEquals("Online Appointment", appointmentServiceType.getName());
    }
    
    @Test
    public void shouldGetAppointmentServiceByUuid() throws Exception{
    	String uuid = "c36006d4-9fbb-4f20-866b-0ece245615a1";
    	
    	AppointmentService appointmentService = phrAppointmentServiceDao.getAppointmentServiceByUuid(uuid);
    	assertNotNull(appointmentService);
        assertEquals(uuid, appointmentService.getUuid());
    }
    
    @Test
    public void shouldGetAllAppointments() throws Exception {
    	Date forDate = DateUtil.convertToDate("2108-08-14T00:00:00.0Z", DateUtil.DateFormatType.UTC);
            List<Appointment> allAppointments = phrAppointmentServiceDao.getAllAppointments(forDate);
            assertEquals(3, allAppointments.size());
        }
    
    @Test
    public void createPatient() {
    	
    	AppointmentPatient patient = new AppointmentPatient();
    	patient.setFirstName("firstname");
    	patient.setLastName("lastname");
    	String mobileNumber = "9999999999";
    	patient.setMobileNumber(mobileNumber);
    	AppointmentPatient appointmentPatient = phrAppointmentServiceDao.createPatient(patient, mobileNumber);
    	assertNotNull(appointmentPatient);
        assertEquals("firstname", appointmentPatient.getFirstName());
        assertEquals("lastname", appointmentPatient.getLastName());
        assertEquals("9999999999", appointmentPatient.getMobileNumber());
    }
    

}
