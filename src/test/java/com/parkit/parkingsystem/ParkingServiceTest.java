package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
        	lenient().when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("ABCDEF");
            
            
            
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehiculeRegNumber("ABCDEF");
            
            lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
           
            
            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }
    
    @Test
    @DisplayName("Arrivé d'un voiture")
    public void ProcessIncomingVehiculeTest() {
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    	when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

    	parkingService.processIncomingVehicule();

    	verify(inputReaderUtil, times(1)).readSelection();
    	verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
    	verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
    	verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
    }    

    @Test
    @DisplayName("Sortie d'une voiture avec le ticket mis a jour")
    public void processExitingVehiculeWithUpdateTicketReturnTrue() {
        parkingService.processExitingVehicule();
        verify(ticketDAO, times(1)).getTicket(anyString());
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
    }
    
    
    
    @Test
    @DisplayName("Sortie d'une voiture avec le ticket non mis a jour")
    public void processExitingVehiculeWithUpdateTicketReturnFalse() {
    	when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
    	parkingService.processExitingVehicule();
    	verify(ticketDAO, times(1)).getTicket(anyString());
    	verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
    }

    @Test
    @DisplayName("Vérifie que la place de parking est bien libre pour une voiture")
    public void whenGetNextParkingNumberIsAvailableForACar() {
    	
    	ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.CAR, false);
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(2);
    	ParkingSpot parkingSpotTest = parkingService.getNextParkingNumberIfAvailable();

    	verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
       
    	assertEquals(parkingSpot, parkingSpotTest);
    }

    @Test
    @DisplayName("Vérifie que la place de parking est bien libre pour une moto")
    public void whenGetNextParkingNumberIsAvailableForABike() {

    	ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.BIKE, false);
    	when(inputReaderUtil.readSelection()).thenReturn(2);
    	when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(2);
    	ParkingSpot parkingSpotTest = parkingService.getNextParkingNumberIfAvailable();

    	verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
    	
    	assertEquals(parkingSpot, parkingSpotTest);
    }
    
    @Test
    @DisplayName("Vérifie que la place de parking n'est pas libre pour une voiture")
    public void whenGetNextParkingNumberIsNotAvailableForACare() {

    	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    	ParkingSpot parkingSpotTest = parkingService.getNextParkingNumberIfAvailable();

    	verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
       
    	assertEquals(parkingSpot, parkingSpotTest);
    }
    
    @Test
    @DisplayName("Vérifie qu'il n'y a plus de place de parking pour une voiture")
    public void whenGetNextParkingNumberIsNotAvailableForAnything() {
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);
    	ParkingSpot parkingSpotTest = parkingService.getNextParkingNumberIfAvailable();

    	verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
       
    	assertEquals(null, parkingSpotTest);
    }    
    
    @Test
    @DisplayName("Vérifie qu'il n'y a plus de place de parking pour une moto")
    public void whenGetNextParkingNumberIsNotAvailableForAnyng() {
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(0);
    	ParkingSpot parkingSpotTest = parkingService.getNextParkingNumberIfAvailable();

    	verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
       
    	assertEquals(null, parkingSpotTest);
    }
    
}
