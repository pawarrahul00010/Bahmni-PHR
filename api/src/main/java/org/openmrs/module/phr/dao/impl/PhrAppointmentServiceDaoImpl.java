package org.openmrs.module.phr.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.phr.dao.PhrAppointmentServiceDao;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.model.AppointmentStatus;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentService;
import org.openmrs.module.appointments.model.AppointmentPatient;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;



public class PhrAppointmentServiceDaoImpl implements PhrAppointmentServiceDao{

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public AppointmentServiceType getAppointmentServiceTypeByUuid(String uuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AppointmentServiceType.class, "appointmentServiceType");
        criteria.add(Restrictions.eq("uuid", uuid));
        return (AppointmentServiceType) criteria.uniqueResult();
    }
    
    @Override
    public AppointmentPatient getAppointmentPatientByUuid(String uuid) {
    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AppointmentPatient.class, "appointmentPatient");
        criteria.add(Restrictions.eq("uuid", uuid));
        return (AppointmentPatient) criteria.uniqueResult();

    }
    
    @Override
    public AppointmentServiceType getAppointmentServiceTypeByName(int serviceId,String name) {
    	AppointmentService appointmentService = new AppointmentService();
    	appointmentService.setAppointmentServiceId(serviceId);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AppointmentServiceType.class, "appointmentServiceType");
        criteria.add(Restrictions.eq("name", name));
        criteria.add(Restrictions.eq("appointmentService", appointmentService));
        return (AppointmentServiceType) criteria.uniqueResult();
    }
    
    @Override
    public AppointmentService getAppointmentServiceByUuid(String uuid) {
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(AppointmentService.class, "appointmentService");
        criteria.add(Restrictions.eq("uuid", uuid));
        AppointmentService appointmentService = (AppointmentService) criteria.uniqueResult();
        evictObjectFromSession(currentSession, appointmentService);
        return appointmentService;
    }
    
    private void evictObjectFromSession(Session currentSession, AppointmentService appointmentService) {
        if (appointmentService != null) {
            currentSession.evict(appointmentService);
        }
    }
    
    @Override
    public List<Appointment> getAllAppointments(Date forDate) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Appointment.class);
        criteria.add(Restrictions.eq("voided", false));
        if (forDate != null) {
            Date maxDate = new Date(forDate.getTime() + TimeUnit.DAYS.toMillis(1));
            criteria.add(Restrictions.ge("startDateTime", forDate));
            
        }
        criteria.add(Restrictions.eq("status", AppointmentStatus.Scheduled));
        criteria.createAlias("serviceType", "type");
        criteria.add(Restrictions.eq("type.name", "Online Appointment"));
        criteria.addOrder(Order.asc("startDateTime"));

        return criteria.list();
    }

    @Transactional
    @Override
    public AppointmentPatient createPatient(AppointmentPatient patient, String mobileNumber) {
    	patient.setMobileNumber(mobileNumber);
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(patient);
        return patient;
    }

}
