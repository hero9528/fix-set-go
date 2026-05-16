package com.fixsetgo.lead;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadRepository leadRepository;
    private final LeadNotificationService leadNotificationService;

    public LeadController(LeadRepository leadRepository, LeadNotificationService leadNotificationService) {
        this.leadRepository = leadRepository;
        this.leadNotificationService = leadNotificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Lead create(@Valid @RequestBody LeadRequest request) {
        Lead lead = new Lead();
        lead.setName(request.name().trim());
        lead.setEmail(request.email().trim().toLowerCase());
        lead.setCompany(request.company() == null ? null : request.company().trim());
        lead.setService(request.service().trim());
        lead.setMessage(request.message().trim());
        Lead savedLead = leadRepository.save(lead);
        leadNotificationService.sendLeadNotification(savedLead);
        return savedLead;
    }

    @GetMapping
    public List<Lead> list() {
        return leadRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
