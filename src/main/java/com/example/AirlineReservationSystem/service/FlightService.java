package com.example.AirlineReservationSystem.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.example.AirlineReservationSystem.model.Passenger;
import com.example.AirlineReservationSystem.model.Plane;
import com.example.AirlineReservationSystem.model.Reservation;
import com.example.AirlineReservationSystem.repos.Passengerrepo;
import com.example.AirlineReservationSystem.repos.Reservationrepo;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.AirlineReservationSystem.model.Flight;
import com.example.AirlineReservationSystem.repos.Flightrepo;

@Service
public class FlightService {
    @Autowired
    private Flightrepo flightrepo;
    @Autowired
    private Reservationrepo reservationrepo;
    @Autowired
    private Passengerrepo passengerrepo;

    private boolean checkValidUpdate(Flight currentFlight, Date currentFlightArrivalTime,
            Date currentFlightDepartureTime) {
        for (Passenger passenger : passengerrepo.findAll()) {
            Set<Flight> flights = new HashSet<>();
            for (Reservation reservation : passenger.getReservations()) {
                flights.addAll(reservation.getFlights());
            }
            if (flights.contains(currentFlight)) {
                flights.remove(currentFlight);
                for (Flight flight : flights) {
                    Date flightDepartureTime = flight.getDepartureTime();
                    Date flightArrivalTime = flight.getArrivalTime();
                    if (currentFlightArrivalTime.compareTo(flightDepartureTime) >= 0
                            && currentFlightDepartureTime.compareTo(flightArrivalTime) <= 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public ResponseEntity<?> getFlightByNumber(String flightNumber) throws NotFoundException {
        Optional<Flight> res = flightrepo.getFlightByFlightNumber(flightNumber);
        if (res.isPresent()) {
            Flight flight = res.get();
            return new ResponseEntity<>(flight, HttpStatus.OK);
        } else {
            throw new NotFoundException("Sorry, the requested flight with number" + flightNumber + "does not exist");
        }
    }

    public ResponseEntity<?> updateFlight(String flightNumber, int price, String origin, String destination,
            String departureTime, String arrivalTime, String description, int capacity, String model,
            String manufacturer, int yearOfManufacture) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
        Date dTime = formatter.parse(departureTime);
        Date aTime = formatter.parse(arrivalTime);
        if (origin.equals(destination) || aTime.compareTo(dTime) <= 0) {
            throw new IllegalArgumentException("Illegal arguments entered to create/update flight.");
        }
        Optional<Flight> res = flightrepo.getFlightByFlightNumber(flightNumber);
        Flight flight;
        Plane plane;
        if (res.isPresent()) {
            flight = res.get();
            Flight finalFlight = flight;
            List<Reservation> reservationList = reservationrepo.findAllByFlightsIn(
                    new ArrayList<Flight>() {
                        {
                            add(finalFlight);
                        }
                    });
            if (reservationList.size() > capacity) {
                throw new IllegalArgumentException("target capacity less than active reservations");
            }
            if (!checkValidUpdate(flight, aTime, dTime)) {
                throw new IllegalArgumentException("flight timings overlapping with other passenger reservations");
            }
            flight.setPrice(price);
            flight.setOrigin(origin);
            flight.setDestination(destination);
            flight.setDepartureTime(dTime);
            flight.setArrivalTime(aTime);
            flight.setDescription(description);
            flight.setSeatsLeft(capacity);
            flight.getPlane().setCapacity(capacity);
            flight.getPlane().setModel(model);
            flight.getPlane().setManufacturer(manufacturer);
            flight.getPlane().setYearOfManufacture(yearOfManufacture);
        } else {
            plane = new Plane(capacity, model, manufacturer, yearOfManufacture);
            flight = new Flight(flightNumber, price, origin, destination, dTime, aTime, capacity, description, plane,
                    new ArrayList<>());
        }
        flight = flightrepo.save(flight);
        return new ResponseEntity<>(flight, HttpStatus.OK);
    }

    public void deleteFlight(String flightNumber) throws NotFoundException {
        Optional<Flight> res = flightrepo.getFlightByFlightNumber(flightNumber);
        if (res.isPresent()) {
            Flight flight = res.get();
            List<Reservation> reservationList = reservationrepo.findAllByFlightsIn(
                    new ArrayList<Flight>() {
                        {
                            add(flight);
                        }
                    });
            if (!reservationList.isEmpty()) {
                throw new IllegalArgumentException("Flight " + flightNumber + " has active reservations");
            } else {
                flightrepo.delete(flight);
                new ResponseEntity<>(HttpStatus.OK);
            }
        } else {
            throw new NotFoundException("Sorry, the requested flight with number " + flightNumber + " does not exist");
        }
    }

    public List<Flight> getAllFlights() throws NotFoundException {
        List<Flight> flights = flightrepo.findAll();
        if (flights.isEmpty()) {
            throw new NotFoundException("No flights found");
        }
        return flights;
    }
}
