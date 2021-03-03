package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	
	private TicketDAO ticketDAO = new TicketDAO();
	
	public TicketDAO getTicketDAO() {
		return ticketDAO;
	}

	public void setTicketDAO(TicketDAO ticketDAO) {
		this.ticketDAO = ticketDAO;
	}

	public void calculateFare(Ticket ticket){

		if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) )
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		
        float parkingtime = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        float duration = parkingtime / (1000*60*60);
        boolean reccuringUser = ticketDAO.reccuringUser(ticket.getVehiculeRegNumber());
        double price = 0;
        
        if (ticket.getParkingSpot().getParkingType() == null)
        	throw new NullPointerException();
		if (duration > 0.5)
		{
			switch (ticket.getParkingSpot().getParkingType()) {
				case CAR: {
					price = Math.round(duration * Fare.CAR_RATE_PER_HOUR * 100.0) / 100.0;
					if (reccuringUser)
						price =  Math.round(price * 0.95 * 100.0) / 100.0;
					ticket.setPrice(price);
					break;
				}
				case BIKE: {
					price = Math.round(duration * Fare.BIKE_RATE_PER_HOUR * 100.0) / 100.0;
					if (reccuringUser)
						price =  Math.round(price * 0.95 * 100.0) / 100.0;
					ticket.setPrice(price);
					break;
				}
				default: throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
		else
			ticket.setPrice(0);
	}
}