package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.GarageNotFoundException;
import com.tallerwebi.dominio.excepcion.UserNotFoundException;
import com.tallerwebi.helpers.NotificationService;
import com.tallerwebi.infraestructura.ParkingPlaceRepository;
import com.tallerwebi.infraestructura.ReportRepository;
import com.tallerwebi.infraestructura.UserRepository;
import com.tallerwebi.model.*;
import com.tallerwebi.presentacion.dto.EditReportDTO;
import com.tallerwebi.presentacion.dto.NotificationRequestDTO;
import com.tallerwebi.presentacion.dto.ReportDTO;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final ParkingPlaceRepository parkingPlaceRepository;

    public ReportServiceImpl(ReportRepository reportRepository, NotificationService notificationService, UserRepository userRepository, ParkingPlaceRepository parkingPlaceRepository) {
        this.reportRepository = reportRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.parkingPlaceRepository = parkingPlaceRepository;
    }
    @Override
    public List<Report> getAllReports() {
        List<Report> list = reportRepository.getAllReports();
        for (Report report:list) {
            Hibernate.initialize(report.getParkingPlace());
            Hibernate.initialize(report.getUser());
        }
        return list;
    }

    @Override
    public void editReport(EditReportDTO editReportDTO) {
        Report report = reportRepository.getReportById(editReportDTO.getId());

        report.setReportStatus(editReportDTO.getReportStatus());
        if(editReportDTO.getReportStatus() == ReportStatus.ACCEPTED || editReportDTO.getReportStatus() == ReportStatus.REJECTED) report.setActive(false);

        reportRepository.save(report);
        notificationService.registerAndSendNotification(new NotificationRequestDTO(
                "Su denuncia fue revisada por un administrador",
                "Puede ver el estado de su denuncia en su perfil",
                NotificationType.REPORT,
                editReportDTO.getUserId()
        ));
    }

    @Override
    public void registerReport(ReportDTO reportDTO) {
        Garage garage = (Garage) parkingPlaceRepository.findById(reportDTO.getGarageId());
        if(garage == null) throw new GarageNotFoundException();
        MobileUser user = userRepository.findUserByMail(reportDTO.getUserEmail());
        if(user == null) throw new UserNotFoundException();

        Report report = new Report(reportDTO.getReportType(), reportDTO.getDescription(), garage, user);
        report.setActive(true);
        report.setReportStatus(ReportStatus.IN_PROCESS);

        reportRepository.save(report);
    }

    @Override
    public List<Report> getUserReport(Long userId) {
        MobileUser user = userRepository.findUserById(userId);
        List<Report> list = reportRepository.getReportByUser(user);

        for (Report report:list) {
            Hibernate.initialize(report.getParkingPlace());
            Hibernate.initialize(report.getUser());
        }
        return list;
    }

    @Override
    public Report findReportById(Long id) {
        Report report = reportRepository.getReportById(id);
        Hibernate.initialize(report.getUser());
        Hibernate.initialize(report.getParkingPlace());
        return report;
    }
}
