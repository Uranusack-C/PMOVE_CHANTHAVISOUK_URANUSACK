package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
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

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static ParkingService parkingService;

	private static Ticket ticket;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception{
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();

		ticket = new Ticket();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
		lenient().when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("123");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown(){

	}

	@Test
	@DisplayName("Vérifie si le ticket est actuellement sauvé dans la BDD et si la table Parking est mis à jour avec la disponibilité pour une voiture")
	public void testParkingACar() throws Exception{
		parkingService.processIncomingVehicule();
		assertEquals(ticketDAO.getTicket(inputReaderUtil.readVehiculeRegistrationNumber()).getVehiculeRegNumber(),
				inputReaderUtil.readVehiculeRegistrationNumber());
		assertEquals(ticketDAO.getTicket(inputReaderUtil.readVehiculeRegistrationNumber()).getParkingSpot().isAvailable(),
				false);
	}

	@Test
	@DisplayName("Vérifie si le ticket est actuellement sauvé dans la BDD et si la table Parking est mis à jour avec la disponibilité pour une moto")
	public void testParkingABike() throws Exception{
		when(inputReaderUtil.readSelection()).thenReturn(2);
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

	@Test
	@DisplayName("Vérifie si le prix est généré et si la date de sortie est correctement renseignée dans la BDD")
	public void testIfTheVehiculeRegistrationNumberIsUnknow() throws Exception
	{
		lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
		lenient().when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn(null);
		assertEquals(ticketDAO.getTicket(inputReaderUtil.readVehiculeRegistrationNumber()), null);
	}

	@Test
	public void getNextAvailableSlotWhenAvailableTest() {
		int ourNum = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		assertNotEquals(ourNum, -1);
	}



	@Test
	public void updateParkingTestWithNullValueOfParkingSpotShouldReturnFalse() {

		assertEquals(parkingSpotDAO.updateParking(null), false);
	}

	@Test
	public   void savingTicketTest() {
		ParkingSpot parkingSpot = new ParkingSpot(3, ParkingType.CAR,false); 
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
		Date outTime = new Date();

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehiculeRegNumber("REGNUMBER");
		ticket.setPrice(15.0);

		boolean isSaved = ticketDAO.saveTicket(ticket);

		assertEquals(isSaved, false);
	}

	@Test
	public void getTicketTestWithExistingTicket() {

		savingTicketTest();
		Ticket ourTicket = ticketDAO.getTicket("REGNUMBER");

		assertEquals(ticket.getParkingSpot(), ourTicket.getParkingSpot());
	}

	@Test
	public void updateTicketTest() {

		savingTicketTest();
		ticket.setPrice(1);
		assertEquals(ticketDAO.updateTicket(ticket),true);
	}

}