package com.sky.service;


import com.sky.entity.AddressBook;
import org.springframework.stereotype.Service;

import java.util.List;


public interface AddressBookService {


    /**
     * get all addresses under the current user
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);


    /**
     * add new address
     * @param addressBook
     * @return
     */
    void save(AddressBook addressBook);


    /**
     * get address by id
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    void update(AddressBook addressBook);

    void setDefault(AddressBook addressBook);

    void deleteById(Long id);
}
