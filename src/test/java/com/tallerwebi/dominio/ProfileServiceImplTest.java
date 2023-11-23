package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.GarageNotFoundException;
import com.tallerwebi.dominio.excepcion.UserNotFoundException;
import com.tallerwebi.infraestructura.*;
import com.tallerwebi.model.*;
import com.tallerwebi.presentacion.dto.ProfileResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileServiceImplTest {

    private ProfileServiceImpl profileService;

    @Mock
    private VehicleRepository mockVehicleRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private ParkingPlaceRepository parkingPlaceRepository;
    @Mock
    private ReportRepository reportRepository;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profileService = new ProfileServiceImpl(mockVehicleRepository, mockUserRepository, notificationRepository, parkingPlaceRepository, reportRepository);
    }

    @Test
    void shouldGetVehiclesAndParkingList() {
        Long userId = 1L;
        MobileUser user = sinceItSavesAMobileUserWithVehiclesAndParkings();

        Mockito.when(mockUserRepository.findUserById(userId))
                .thenReturn(user);

        ProfileResponseDTO response = profileService.getVehiclesAndParkingsByMobileUser(userId);

        assertEquals(1, response.getVehicles().size());
        assertEquals(1, response.getParkings().size());
    }

    @Test
    void whenTryToGetVehiclesAndParkingsByMobileUser_IfUserNotExist_ShouldThrowUserNotFoundException(){
        Long userId = 1L;

        Mockito.when(mockUserRepository.findUserById(userId))
                .thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> profileService.getVehiclesAndParkingsByMobileUser(userId));
    }

    @Test
    void whenTryToGetAllNotificationsByMobileUser_ShouldGetAllNotifications() {
        MobileUser user = getNewMobileUserWithNotificationList();
        List<Notification> notifications = user.getNotifications();

        Mockito.when(mockUserRepository.findUserById(user.getId()))
                .thenReturn(user);
        Mockito.when(notificationRepository.findAllByUser(user))
                .thenReturn(user.getNotifications());

        assertEquals(notifications, notificationRepository.findAllByUser(user));
    }

    @Test
    void shouldRegisterNewReport() {
        Long idAdmin = 1L;
        String email = "jdoe@mail.com";

        createAndPersistMobileUser(email);
        createAndPersistGarage(idAdmin);

        profileService.registerReport(idAdmin, email, "some description");

        Mockito.verify(reportRepository).save(ArgumentMatchers.argThat(report ->
                report.getReportType().equals(ReportType.FRAUD) &&
                        report.isActive() &&
                        report.getDescription().equals("some description") &&
                        report.getReportStatus().equals(ReportStatus.IN_PROCESS)
        ));
    }

    @Test
    void whenRegisterReport_ifGarageNotExist_shouldThrowException() {
        Long idAdmin = 1L;
        String email = "jdoe@mail.com";
        createAndPersistMobileUser(email);

        Mockito.when(parkingPlaceRepository.findById(idAdmin))
                .thenReturn(null);

        assertThrows(GarageNotFoundException.class, () -> profileService.registerReport(idAdmin, email, "some desc"));
    }

    @Test
    void shouldGetReportsByUser() {
        Long id = 1L;
        MobileUser user = createAndPersistMobileUser(id);
        Mockito.when(reportRepository.getReportByUser(user))
                .thenReturn(List.of());

        List<Report> reports = profileService.getReportsByUser(id);

        assertTrue(reports.isEmpty());
    }

    private MobileUser sinceItSavesAMobileUserWithVehiclesAndParkings() {
        MobileUser user = new MobileUser();
        Vehicle vehicle = new Vehicle();
        Parking parking = new Parking();
        user.registerParking(parking);
        user.registerVehicle(vehicle);
        return user;
    }

    private MobileUser getNewMobileUserWithNotificationList(){
        Notification notification = new Notification();
        MobileUser user = new MobileUser();
        user.addNotification(notification);

        return user;
    }

    private void createAndPersistMobileUser(String email) {
        MobileUser user = new MobileUser();
        Mockito.when(mockUserRepository.findUserByMail(email))
                .thenReturn(user);
    }

    private MobileUser createAndPersistMobileUser(Long id) {
        MobileUser user = new MobileUser();
        Mockito.when(mockUserRepository.findUserById(id))
                .thenReturn(user);
        return user;
    }

    private void createAndPersistGarage(Long idAdmin) {
        Mockito.when(parkingPlaceRepository.findById(idAdmin))
                .thenReturn(new Garage());
    }
}