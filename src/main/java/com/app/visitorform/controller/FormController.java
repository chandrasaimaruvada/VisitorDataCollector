package com.app.visitorform.controller;


import com.app.visitorform.model.VisitorForm;
import com.app.visitorform.service.GoogleSheetService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class FormController {

    @Autowired
    private GoogleSheetService sheetService;

    @GetMapping("/")
    public String showForm(Model model, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

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
        data.put("anniversary", visitorFormDTO.getAnniversary());
        data.put("visitPreference", visitorFormDTO.getVisitPreference());
        data.put("feedback", visitorFormDTO.getFeedback());

        sheetService.saveToSheet(data);
        return "thankyou";
    }

    @PostMapping("/submit-form")
    public String submitForm(@RequestParam Map<String, String> formData, RedirectAttributes redirectAttributes) {
        sheetService.saveToSheet(formData);
        redirectAttributes.addFlashAttribute("success", "Thank you! Your response has been recorded.");
        return "redirect:/thank-you";
    }

    @GetMapping("/thank-you")
    public String thankYouPage() {
        return "thank-you";
    }
}
