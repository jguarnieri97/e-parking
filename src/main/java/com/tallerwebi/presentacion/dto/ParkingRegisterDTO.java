package com.tallerwebi.presentacion.dto;

import com.tallerwebi.model.ParkingPlace;
import com.tallerwebi.model.ParkingType;
import com.tallerwebi.model.PointSale;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.Instant;
import java.util.Date;

public class ParkingRegisterDTO {

    private ParkingType parkingType;
    private String vehicle;
    private byte[] vehiclePic;
    private byte[] ticketPic;
    private Double lat;
    private Double ln;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date parkingDate;
    private int ammountHs;
    private boolean isPaid;
    private ParkingPlace parkingPlace;

    public ParkingRegisterDTO() {
    }

    public ParkingRegisterDTO(ParkingType parkingType, String vehicle, byte[] vehiclePic, byte[] ticketPic, Double lat, Double ln, ParkingPlace parkingPlace) {
        this.parkingType = parkingType;
        this.vehicle = vehicle;
        this.vehiclePic = vehiclePic;
        this.ticketPic = ticketPic;
        this.lat = lat;
        this.ln = ln;
        this.parkingDate = Date.from(Instant.now());
        this.parkingPlace = parkingPlace;
    }

    public ParkingType getParkingType() {
        return parkingType;
    }

    public void setParkingType(ParkingType parkingType) {
        this.parkingType = parkingType;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public byte[] getVehiclePic() {
        return vehiclePic;
    }

    public void setVehiclePic(byte[] vehiclePic) {
        this.vehiclePic = vehiclePic;
    }

    public byte[] getTicketPic() {
        return ticketPic;
    }

    public void setTicketPic(byte[] ticketPic) {
        this.ticketPic = ticketPic;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLn() {
        return ln;
    }

    public void setLn(Double ln) {
        this.ln = ln;
    }

    public Date getParkingDate() {
        return parkingDate;
    }

    public void setParkingDate(Date parkingDate) {
        this.parkingDate = parkingDate;
    }

    public int getAmmountHs() {
        return ammountHs;
    }

    public void setAmmountHs(int ammountHs) {
        this.ammountHs = ammountHs;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
    public ParkingPlace getParkingPlace(){
        return parkingPlace;
    }

    public void setParkingPlace(ParkingPlace parkingPlace) {
        this.parkingPlace = parkingPlace;
    }
}