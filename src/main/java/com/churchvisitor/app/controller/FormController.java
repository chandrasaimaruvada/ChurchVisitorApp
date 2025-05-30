package com.churchvisitor.app.controller;

import com.churchvisitor.app.model.VisitorForm;
import com.churchvisitor.app.service.GoogleSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class FormController {

    @Autowired
    private GoogleSheetService sheetService;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("visitor", new VisitorForm());
        return "form";
    }

    @PostMapping("/submit")
    public String submitForm(@ModelAttribute("visitor") VisitorForm visitorFormDTO) {
        visitorFormDTO.setSubmissionDate(LocalDate.now().toString());

        Map<String, String> data = new LinkedHashMap<>();
        data.put("name", visitorFormDTO.getName());
        data.put("mobile", visitorFormDTO.getMobile());
        data.put("place", visitorFormDTO.getPlace());
        data.put("knowUs", visitorFormDTO.getKnowUs());
        data.put("birthday", visitorFormDTO.getBirthday());
        data.put("prayerPoints", visitorFormDTO.getPrayerPoints());
        data.put("submissionDate", visitorFormDTO.getSubmissionDate());

        sheetService.saveToSheet(data);
        return "thankyou";
    }
}
