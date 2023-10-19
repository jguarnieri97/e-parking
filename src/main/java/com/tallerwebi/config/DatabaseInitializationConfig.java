package com.tallerwebi.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.infraestructura.ParkingPlaceRepository;
import com.tallerwebi.infraestructura.UserRepository;
import com.tallerwebi.model.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Configuration
public class DatabaseInitializationConfig {

    public static final String MAIL = "test@unlam.edu.ar";
    public static final String PASSWORD = "test";
    public static final String NOMBRE = "Admin";
    public static final String NICK_NAME = "admin";
    public static final String PATENTE = "ABC123";
    public static final String MARCA = "VOLKSWAGEN";
    public static final String MODELO = "FOX";
    public static final String COLOR = "Rojo";
    private final UserRepository userRepository;
    private final ParkingPlaceRepository parkingPlaceRepository;

    public DatabaseInitializationConfig(UserRepository userRepository, ParkingPlaceRepository parkingPlaceRepository) {
        this.userRepository = userRepository;
        this.parkingPlaceRepository = parkingPlaceRepository;
    }

    @Bean
    public void dataSourceInitializer() throws IOException {
        MobileUser user = new MobileUser(MAIL, PASSWORD, UserRole.ADMIN, NOMBRE, NICK_NAME);
        Vehicle vehicle = new Vehicle(PATENTE, MARCA, MODELO, COLOR);
        vehicle.setUser(user);
        user.registerVehicle(vehicle);

        createAndSaveParkingsPlaces();
        userRepository.save(user);
    }

    private void createAndSaveParkingsPlaces() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<PointSale> pointsSale = objectMapper.readValue(new File("src/main/resources/PointSaleList.json"), new TypeReference<List<PointSale>>() {});

        for (PointSale pointSale : pointsSale) {
        parkingPlaceRepository.save(pointSale);
        }
    }

}