package com.yorkDev.buynowdotcom.service.address;

import com.yorkDev.buynowdotcom.dtos.AddressDto;
import com.yorkDev.buynowdotcom.model.Address;
import com.yorkDev.buynowdotcom.model.User;

import java.util.List;

public interface IAddressService {
    List<AddressDto> getUserAddresses(Long userId);
    Address addAddress(Long userId, AddressDto dto);
    Address updateAddress(Long addressId, AddressDto dto);
    void deleteAddress(Long addressId);
    boolean verifyOwner(Long addressId, User user);
    void resetDefaultShippingAddresses(Long userId);
    void resetDefaultBillingAddresses(Long userId);
    AddressDto convertToDto(Address address);
}
