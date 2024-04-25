package com.example.AirlineReservationSystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/updateFlights")
    public String updateFlight() {
        return "addUpdateFlights";
    }

    @GetMapping("/cancelReservation")
    public String cancelRes() {
        return "cancelReservation";
    }

    @GetMapping("/createPassenger")
    public String createPass() {
        return "createPassenger";
    }

    @GetMapping("/createReservation")
    public String createRes() {
        return "createReservation";
    }

    @GetMapping("/deleteFlight")
    public String delFlight() {
        return "deleteFlight";
    }

    @GetMapping("/deletePassenger")
    public String delPass() {
        return "deletePassenger";
    }

    @GetMapping("/getPassenger")
    public String getPassDeets() {
        return "getPassengerDetails";
    }

    @GetMapping("/getReservation")
    public String getRes() {
        return "getReservation";
    }

    @GetMapping("/displayFlights")
    public String dispFlights() {
        return "showAllFlights";
    }

    @GetMapping("/dispPartFlight")
    public String dispPart() {
        return "showParticularFlightDetails";
    }

    @GetMapping("/updatePassenger")
    public String updatepass() {
        return "updatePassenger";
    }

    @GetMapping("/updateReservation")
    public String updateRes() {
        return "updateReservation";
    }
}
