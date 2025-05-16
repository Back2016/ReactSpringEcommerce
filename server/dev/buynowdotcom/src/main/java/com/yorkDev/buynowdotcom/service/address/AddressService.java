package com.yorkDev.buynowdotcom.service.address;

import com.yorkDev.buynowdotcom.dtos.AddressDto;
import com.yorkDev.buynowdotcom.model.Address;
import com.yorkDev.buynowdotcom.model.User;
import com.yorkDev.buynowdotcom.repository.AddressRepository;
import com.yorkDev.buynowdotcom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService {
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public List<AddressDto> getUserAddresses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Address addAddress(Long userId, AddressDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Address address = convertToEntity(dto);
        address.setUser(user);
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(Long addressId, AddressDto dto) {
        Address existing = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        existing.setRecipientName(dto.getRecipientName());
        existing.setStreet(dto.getStreet());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        existing.setCountry(dto.getCountry());
        existing.setPhone(dto.getPhone());
        existing.setDefaultShipping(dto.isDefaultShipping());
        existing.setDefaultBilling(dto.isDefaultBilling());
        existing.setZipcode(dto.getZipcode());
        return addressRepository.save(existing);
    }

    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    @Override
    public AddressDto convertToDto(Address address) {
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public boolean verifyOwner(Long addressId, User user) {
        Long addressOwnerId = addressRepository.findById(addressId)
                .map(Address::getUser)
                .map(User::getId)
                .orElse(-1L);

        return user.getId().equals(addressOwnerId);
    }

    @Override
    public void resetDefaultShippingAddresses(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Address> updatedAddresses = addressRepository.findByUser(user).stream()
                .peek(address -> address.setDefaultShipping(false))
                .toList();

        addressRepository.saveAll(updatedAddresses);
    }

    @Override
    public void resetDefaultBillingAddresses(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Address> updatedAddresses = addressRepository.findByUser(user).stream()
                .peek(address -> address.setDefaultBilling(false))
                .toList();

        addressRepository.saveAll(updatedAddresses);
    }

    private Address convertToEntity(AddressDto dto) {
        return modelMapper.map(dto, Address.class);
    }
}
