package com.tallerwebi.dominio;

import com.tallerwebi.helpers.NotificationService;
import com.tallerwebi.infraestructura.NotificationRepository;
import com.tallerwebi.infraestructura.ReportRepository;
import com.tallerwebi.infraestructura.UserRepository;
import com.tallerwebi.model.*;
import com.tallerwebi.presentacion.dto.EditReportDTO;
import com.tallerwebi.infraestructura.ParkingPlaceRepository;
import com.tallerwebi.infraestructura.ReportRepository;
import com.tallerwebi.infraestructura.UserRepository;
import com.tallerwebi.model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportServiceImplTest {
    private ReportService reportService;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @Captor
    private ArgumentCaptor<Report> reportCaptor;
    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;
    private ParkingPlaceRepository parkingPlaceRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reportService = new ReportServiceImpl(reportRepository, notificationService, userRepository, parkingPlaceRepository);
    }

    @Test
    public void shouldGetReportList(){
        List<Report> reports = new ArrayList<>();
        Report report = new Report();
        reports.add(report);

        Mockito.when(reportRepository.getAllReports())
                .thenReturn(reports);

        List<Report> reportsList = reportService.getAllReports();

        assertEquals(reports,reportsList);
    }


    @Test
    public void shouldEditReportSuccesfully(){
        Report report = new Report(ReportType.PAYMENT_PROBLEM,"fraude",new Garage(),new MobileUser());
        MobileUser user = new MobileUser();

        Mockito.when(reportRepository.getReportById(1L))
                .thenReturn(report);
        Mockito.when(userRepository.findUserById(1L))
                        .thenReturn(user);

        reportService.editReport(new EditReportDTO(true,ReportStatus.ACCEPTED,1L,1L));

        Mockito.verify(reportRepository).save(reportCaptor.capture());

        Report edited = reportCaptor.getValue();

        assertEquals(ReportStatus.ACCEPTED, edited.getReportStatus());
    }
}
