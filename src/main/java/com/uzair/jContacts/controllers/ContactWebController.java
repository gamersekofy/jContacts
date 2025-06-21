package com.uzair.jContacts.controllers;

import com.uzair.jContacts.models.Contact;
import com.uzair.jContacts.repositories.ContactRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contacts")
public class ContactWebController {
    private final ContactRepository contactRepository;

    public ContactWebController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @GetMapping
    public String listContacts(Model model) {
        model.addAttribute("contacts", contactRepository.findAll());
        return "contacts/index";
    }

    @GetMapping("/{id}")
    public String showContact(@PathVariable Long id, Model model) {
        contactRepository.findById(id).ifPresent(contact -> model.addAttribute("contact", contact));
        return "contacts/show";
    }

    @GetMapping("/new")
    public String newContactForm(Model model) {
        model.addAttribute("contact", new Contact());
        return "contacts/form";
    }

    @GetMapping("/{id}/edit")
    public String editContactForm(@PathVariable Long id, Model model) {
        contactRepository.findById(id).ifPresent(contact -> model.addAttribute("contact", contact));
        return "contacts/form";
    }

    @PostMapping("/save")
    public String saveContact(@Valid @ModelAttribute Contact contact, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "contacts/form";
        }
        contactRepository.save(contact);
        return "redirect:/contacts";
    }

    @PostMapping("/{id}/delete")
    public String deleteContact(@PathVariable Long id) {
        contactRepository.deleteById(id);
        return "redirect:/contacts";
    }

    @GetMapping("/search")
    public String searchContacts(@RequestParam String q, Model model) {
        model.addAttribute("contacts", contactRepository.search(q));
        model.addAttribute("searchTerm", q);
        return "contacts/index";
    }
}
