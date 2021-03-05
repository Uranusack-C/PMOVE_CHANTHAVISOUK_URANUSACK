package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;
	private Date inTime;
	private Date outTime;

	@Mock
	TicketDAO ticketDAO;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
		inTime = new Date();
		outTime = new Date();
	}

	@Test
	@DisplayName("30 minute gratuite pour une voiture")
	public void calculateFareCarForThirtyMinutesFree() {
		inTime.setTime( System.currentTimeMillis() - ( 30 * 60 * 1000 ) );
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), 0);
	}

	@Test
	@DisplayName("30 minute gratuite pour une moto")
	public void calculateFareBikeForThirtyMinutesFree() {
		inTime.setTime( System.currentTimeMillis() - ( 30 * 60 * 1000 ) );
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), 0);
	}

	@Test
	@DisplayName("Prix d'une voiture pour 1h")
	public void calculateFareCarForOneHour(){
		inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000 ) );
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	@DisplayName("Prix d'une moto pour 1h")
	public void calculateFareBikeForOneHour(){
		inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	@DisplayName("Prix d'un vehicule inconnu pour 1h")
	public void calculateFareUnkownTypeForOneHour(){
		inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
		ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	@DisplayName("Erreur lorsque l'heure n'est pas bonne pour une voiture")
	public void calculateFareCarWithFutureInTime(){
		inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	@DisplayName("Erreur lorsque l'heure n'est pas bonne pour une moto")
	public void calculateFareBikeWithFutureInTime(){
		inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	@DisplayName("Prix d'une moto pour 45min")
	public void calculateFareBikeWithLessThanOneHourParkingTime(){
		inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
	}

	@Test
	@DisplayName("Prix d'une voiture pour 45min")
	@Disabled
	public void calculateFareCarWithLessThanOneHourParkingTime(){
		inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertEquals( (Math.round(0.75 * Fare.CAR_RATE_PER_HOUR * 100.0) / 100.0) , ticket.getPrice() );
	}

	@Test
	@DisplayName("Prix d'une voiture pour 1 journée")
	public void calculateFareCarOneDayParkingTime(){
		inTime.setTime( System.currentTimeMillis() - ( 60 * 24 * 60 * 1000 ) );//24 hours parking time should give 24 * parking fare per hour
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
	}

	@Test
	@DisplayName("Prix d'une moto pour 1 journée")
	public void calculateFareBikeOneDayParkingTime(){
		inTime.setTime( System.currentTimeMillis() - ( 60 * 24 * 60 * 1000 ) );//24 hours parking time should give 24 * parking fare per hour
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals( (24 * Fare.BIKE_RATE_PER_HOUR) , ticket.getPrice());
	}

	@Disabled
	@Test
	@DisplayName("5% de réduction pour les utilisateurs récurrents en voiture")
	public void calculateFareReccuringUserCar() {
		inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehiculeRegNumber("123");

		fareCalculatorService.setTicketDAO(ticketDAO);
		when(ticketDAO.reccuringUser(ticket.getVehiculeRegNumber())).thenReturn(true);
		fareCalculatorService.calculateFare(ticket);
		assertEquals( (Math.round(Fare.CAR_RATE_PER_HOUR * 100.0 * 0.95)) / 100.0, ticket.getPrice());
	}

	@Test
	@DisplayName("5% de réduction pour les utilisateurs récurrents en moto")
	public void calculateFareReccuringUserBike() {
		inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehiculeRegNumber("123");

		fareCalculatorService.setTicketDAO(ticketDAO);
		when(ticketDAO.reccuringUser(ticket.getVehiculeRegNumber())).thenReturn(true);
		fareCalculatorService.calculateFare(ticket);
		assertEquals( (Math.round(Fare.BIKE_RATE_PER_HOUR * 100.0 * 0.95)) / 100.0, ticket.getPrice());
	}
}
