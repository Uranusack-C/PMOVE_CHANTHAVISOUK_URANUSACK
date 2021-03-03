package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
    	parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("123");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    @DisplayName("Vérifie si le ticket est actuellement sauvé dans la BDD et si la table Parking est mis à jour avec la disponibilité")
    public void testParkingACar() throws Exception{
        parkingService.processIncomingVehicule();
        assertEquals(ticketDAO.getTicket(inputReaderUtil.readVehiculeRegistrationNumber()).getVehiculeRegNumber(),
        			 inputReaderUtil.readVehiculeRegistrationNumber());
        assertEquals(ticketDAO.getTicket(inputReaderUtil.readVehiculeRegistrationNumber()).getParkingSpot().isAvailable(),
        			 false);
    }

    @Test
    @DisplayName("Vérifie si le prix est généré et si la date de sortie est correctement renseignée dans la BDD")
    public void testParkingLotExit() throws Exception{
        testParkingACar();
        parkingService.processExitingVehicule();
        assertEquals(ticketDAO.getTicket(inputReaderUtil.readVehiculeRegistrationNumber()).getPrice(), 0);
        assertNotNull(ticketDAO.getTicket(inputReaderUtil.readVehiculeRegistrationNumber()).getOutTime());
    }

}
