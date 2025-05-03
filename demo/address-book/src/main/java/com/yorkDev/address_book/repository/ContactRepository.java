package com.yorkDev.address_book.repository;

import com.yorkDev.address_book.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
