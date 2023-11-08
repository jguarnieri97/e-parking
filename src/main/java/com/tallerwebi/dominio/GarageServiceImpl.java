package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.OTPNotFoundException;
import com.tallerwebi.dominio.excepcion.VehicleAlreadyParkException;
import com.tallerwebi.dominio.excepcion.VehicleNotFoundException;
import com.tallerwebi.helpers.EmailService;
import com.tallerwebi.infraestructura.*;
import com.tallerwebi.model.*;
import com.tallerwebi.presentacion.dto.OTPDTO;
import com.tallerwebi.presentacion.dto.ParkingRegisterDTO;
import com.tallerwebi.presentacion.dto.VehicleIngressDTO;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Transactional
@Service
public class GarageServiceImpl implements GarageService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingPlaceRepository parkingPlaceRepository;
    private final ParkingRepository parkingRepository;
    private final EmailService emailService;
    private final OTPRepository otpRepository;

    public GarageServiceImpl(UserRepository userRepository, VehicleRepository vehicleRepository, ParkingPlaceRepository parkingPlaceRepository, ParkingRepository parkingRepository, EmailService emailService, OTPRepository otpRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingPlaceRepository = parkingPlaceRepository;
        this.parkingRepository = parkingRepository;
        this.emailService = emailService;
        this.otpRepository = otpRepository;
    }

    @Override
    public void registerVehicle(VehicleIngressDTO vehicleIngressDTO, OTPDTO otpDto, Long garageAdminUserId) {
        if(otpRepository.exists(vehicleIngressDTO.getUserEmail(),garageAdminUserId, otpDto.getOtpKey())){
            addToGarage(vehicleIngressDTO, garageAdminUserId);
            Parking parking = createNewParking(vehicleIngressDTO, garageAdminUserId);
            parkingRepository.save(parking);
        }
       else{
           throw new OTPNotFoundException("No se encontro el OTP.");
        }
    }

    @Override
    public void sendOtp(String email, Long idGarage) throws MailException {
        int min = 100000;
        int max = 999999;
        int randomNum = ThreadLocalRandom.current().nextInt(min, max);
        OTP otp = new OTP(String.valueOf(randomNum),email, idGarage);
        otpRepository.save(otp);
        emailService.sendSimpleMessage(email, "Clave de ingreso:", String.valueOf(randomNum));
    }

    @Override
    public void egressVehicle(String vehiclePatent, Long garageAdminUserId) {
        Garage garage = getGarageByAdminUserId(garageAdminUserId);
        removeFromGarage(vehiclePatent, garage);
        Vehicle vehicle = vehicleRepository.findVehicleByPatent(vehiclePatent);
        MobileUser user = vehicle.getUser();

        ParkingRegisterDTO parkingRegisterDTO = new ParkingRegisterDTO(ParkingType.GARAGE, vehiclePatent, null, null, garage.getGeolocation().getLat(), garage.getGeolocation().getLn(), garage.getId());

        List<Parking> parkingList = user.getParkings();

        Parking latestParking = parkingList.get(0);

        for (Parking parking : parkingList) {
            if (parking.getDateArrival().after(latestParking.getDateArrival())) {
                latestParking = parking;
            }
        }

        latestParking.setDateExit(Date.from(Instant.now()));
        garage.generateTicket(parkingRegisterDTO);
        parkingRepository.save(latestParking);
    }

    @Override
    public List<Vehicle> getRegisteredVehicles(Long garageAdminUserId) {
        MobileUser user = userRepository.findUserById(garageAdminUserId);
        Garage garage = (Garage) parkingPlaceRepository.findGarageByUser(user);
        List<String> patents = new ArrayList<>(garage.getPatents());

        return vehicleRepository.findVehiclesByPatents(patents);
    }

    @Override
    public MobileUser getUserByPatent(String patent) {
        Vehicle vehicle = vehicleRepository.findVehicleByPatent(patent);
        return vehicle.getUser();
    }

    @Override
    public Vehicle getVehicleByPatent(String patent) {
        return vehicleRepository.findVehicleByPatent(patent);
    }

    @Override
    public Garage getGarageByAdminUserId(Long garageAdminUserId) {
        return (Garage) parkingPlaceRepository.findGarageByUser(userRepository.findUserById(garageAdminUserId));
    }

    private void addToGarage(VehicleIngressDTO vehicleIngressDTO, Long garageAdminUserId) {
        Garage garage = getGarageByAdminUserId(garageAdminUserId);
        if (!garage.addVehicle(vehicleIngressDTO.getPatent())) {
            throw new VehicleAlreadyParkException();
        }
    }

    private void removeFromGarage(String vehiclePatent, Garage garage) {
        if(!garage.removeVehicle(vehiclePatent)){
            throw new VehicleNotFoundException();
        }
    }

    private Parking createNewParking(VehicleIngressDTO vehicleIngressDTO, Long garageAdminUserID){
        Garage garage = getGarageByAdminUserId(garageAdminUserID);
        Vehicle vehicle = vehicleRepository.findVehicleByPatent(vehicleIngressDTO.getPatent());
        MobileUser user = vehicle.getUser();
        Parking parking = new Parking(ParkingType.GARAGE, null, null, garage.getGeolocation(), Date.from(Instant.now()));
        parking.setMobileUser(user);

        return parking;
    }
}