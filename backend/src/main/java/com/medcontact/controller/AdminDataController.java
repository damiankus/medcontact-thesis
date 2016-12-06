package com.medcontact.controller;

import java.util.Map;
import java.util.logging.Logger;

import com.medcontact.data.model.dto.PersonalDataPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.medcontact.controller.services.AdminService;
import com.medcontact.data.model.domain.Doctor;
import com.medcontact.exception.UnauthorizedUserException;

import lombok.Data;

@Controller
@RequestMapping("admins")
@Data
public class AdminDataController {
    private Logger logger = Logger.getLogger(AdminDataController.class.getName());

    @Autowired
    private AdminService adminService;
    
    @PostMapping(value = "{id}/doctors")
    public ResponseEntity<Map<String, Object>> addNewDoctor(
    		@PathVariable("id") Long adminId,
    		@RequestBody Doctor doctor) throws UnirestException, UnauthorizedUserException {
        
    	return adminService.addDoctor(adminId, doctor);
    }

    @PutMapping(value = "{id}")
    @ResponseBody
    public void changePersonalData(
            @PathVariable("id") Long adminId,
            @RequestBody PersonalDataPassword personalDataPassword) {

        adminService.changePersonalData(adminId, personalDataPassword);
    }
}
