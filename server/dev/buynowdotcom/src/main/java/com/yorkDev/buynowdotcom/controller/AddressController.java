package com.yorkDev.buynowdotcom.controller;

import com.yorkDev.buynowdotcom.dtos.AddressDto;
import com.yorkDev.buynowdotcom.model.Address;
import com.yorkDev.buynowdotcom.model.User;
import com.yorkDev.buynowdotcom.repository.AddressRepository;
import com.yorkDev.buynowdotcom.response.ApiResponse;
import com.yorkDev.buynowdotcom.service.address.IAddressService;
import com.yorkDev.buynowdotcom.service.user.IUserService;
import com.yorkDev.buynowdotcom.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/addresses")
public class AddressController {
    private final AddressRepository addressRepository;
    private final IAddressService addressService;
    private final IUserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse> getUserAddresses() {
        User user = userService.getAuthenticatedUser();
        List<AddressDto> savedAddresses = addressService.getUserAddresses(user.getId());
        return ResponseEntity.ok(new ApiResponse("Address book found: ", savedAddresses));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addAddress(@Valid @RequestBody AddressDto dto) {
        User user = userService.getAuthenticatedUser();
        Address savedAddress = addressService.addAddress(user.getId(), dto);
        return ResponseEntity.ok(new ApiResponse("Address added successfully", savedAddress));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse> updateAddress(@PathVariable Long addressId,
                                                     @Valid @RequestBody AddressDto dto) {
        User user = userService.getAuthenticatedUser();
        if (!addressService.verifyOwner(addressId, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Access denied", null));
        }
        Address updatedAddress = addressService.updateAddress(addressId, dto);
        AddressDto responseDto = addressService.convertToDto(updatedAddress);
        return ResponseEntity.ok(new ApiResponse("Address updated successfully", responseDto));
    }

    @PutMapping("/defaultShipping/{addressId}")
    public ResponseEntity<ApiResponse> updateDefaultShipping(@PathVariable Long addressId) {
        User user = userService.getAuthenticatedUser();
        if (!addressService.verifyOwner(addressId, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Access denied", null));
        }
        addressService.resetDefaultShippingAddresses(user.getId());
        Address curDefaultShippingAddress = addressRepository
                .findById(addressId).orElseThrow(() -> new EntityNotFoundException("Address not found!"));

        curDefaultShippingAddress.setDefaultShipping(true);
        addressRepository.save(curDefaultShippingAddress);

        AddressDto responseDto = addressService.convertToDto(curDefaultShippingAddress);
        return ResponseEntity.ok(new ApiResponse("Your default shipping address:", responseDto));
    }

    @PutMapping("/defaultBilling/{addressId}")
    public ResponseEntity<ApiResponse> updateDefaultBilling(@PathVariable Long addressId) {
        User user = userService.getAuthenticatedUser();
        if (!addressService.verifyOwner(addressId, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Access denied", null));
        }
        addressService.resetDefaultBillingAddresses(user.getId());
        Address curDefaultBillingAddress = addressRepository
                .findById(addressId).orElseThrow(() -> new EntityNotFoundException("Address not found!"));

        curDefaultBillingAddress.setDefaultBilling(true);
        addressRepository.save(curDefaultBillingAddress);

        AddressDto responseDto = addressService.convertToDto(curDefaultBillingAddress);
        return ResponseEntity.ok(new ApiResponse("Your default billing address:", responseDto));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable Long addressId) {
        User user = userService.getAuthenticatedUser();
        if (!addressService.verifyOwner(addressId, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Access denied", null));
        }
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok(new ApiResponse("Address deleted successfully", null));
    }
}
