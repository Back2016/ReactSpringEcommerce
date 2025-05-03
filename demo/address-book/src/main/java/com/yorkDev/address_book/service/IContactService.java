package com.yorkDev.address_book.service;

import com.yorkDev.address_book.model.Contact;

import java.util.List;

public interface IContactService {
    Contact addContact(Contact request);
    Contact updateContact(Long id, Contact contact);
    Contact getContact(Long id);
    void deleteContact(Long id);
    List<Contact> getContacts();
}
