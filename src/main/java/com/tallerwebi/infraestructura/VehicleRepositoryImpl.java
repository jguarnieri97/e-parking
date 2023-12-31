package com.tallerwebi.infraestructura;

import com.tallerwebi.model.MobileUser;
import com.tallerwebi.model.Vehicle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Transactional
@Repository("repositorioVehiculo")
public class VehicleRepositoryImpl implements VehicleRepository{

    private final SessionFactory sessionFactory;

    public VehicleRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Vehicle> findVehiclesByUser(MobileUser user) {
        Session session = sessionFactory.getCurrentSession();

        return (List<Vehicle>) session.createCriteria(Vehicle.class)
                .add(Restrictions.eq("user", user)).add(Restrictions.eq("isActive", true)).list();
    }

    @Override
    public Vehicle findVehicleByPatent(String patent) {
        Session session = sessionFactory.getCurrentSession();

        return (Vehicle) session.createCriteria(Vehicle.class)
                .add(Restrictions.eq("patent", patent)).uniqueResult();
    }

    @Override
    public void save(Vehicle vehicle) {
        sessionFactory.getCurrentSession().save(vehicle);
    }

    @Override
    public List<Vehicle> findVehiclesByPatents(List<String> patents) {
        Session session = sessionFactory.getCurrentSession();
        return (List<Vehicle>) session.createCriteria(Vehicle.class)
                .add(Restrictions.in("patent", patents)).list();
    }

    @Override
    public void deleteByPatent(String patent) {
        Session session = sessionFactory.getCurrentSession();
        Vehicle vehicle = findVehicleByPatent(patent);

        if (vehicle != null) {
            session.delete(vehicle);
        }
    }

    @Override
    public void disableVehicleByPatent(String patent) {
        Session session = sessionFactory.getCurrentSession();
        Vehicle vehicle = findVehicleByPatent(patent);

        if(vehicle != null){
            vehicle.setIsActive(false);
            save(vehicle);
        }
    }

    @Override
    public List<Vehicle> getAllVehicles(){
        return sessionFactory.getCurrentSession().createCriteria(Vehicle.class).list();
    }
}
