package com.uzair.jContacts.controllers;

import com.uzair.jContacts.models.Contact;
import com.uzair.jContacts.repositories.ContactRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
List of API endpoints defined here:
    POST /api/contacts -> Create contact
    GET /api/contacts -> List all contacts
    GET /api/contacts/{id} -> Get single contact
    PUT /api/contacts/{id} -> Update contact
    DELETE /api/contacts/{id} -> Delete contact
    GET /api/contacts/search?q={term} -> Search contacts
 */

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    private final ContactRepository contactRepository;

    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @PostMapping
    public ResponseEntity<Contact> createContact(@Valid @RequestBody Contact contact) {
        Contact savedContact = contactRepository.save(contact);
        return new ResponseEntity<>(savedContact, null, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Contact> allContacts() {
        return contactRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        return contactRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @Valid @RequestBody Contact contactDetails) {
        return contactRepository.findById(id)
                .map(existingContact -> {
                    existingContact.setFirstName(contactDetails.getFirstName());
                    return ResponseEntity.ok(contactRepository.save(existingContact));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Contact> searchContacts(@RequestParam String q) {
        return contactRepository.search(q);
    }
}