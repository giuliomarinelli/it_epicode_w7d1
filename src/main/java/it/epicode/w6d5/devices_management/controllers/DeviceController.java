package it.epicode.w6d5.devices_management.controllers;


import it.epicode.w6d5.devices_management.Models.entities.Device;
import it.epicode.w6d5.devices_management.Models.reqDTO.AssignDeviceToEmployeeDTO;
import it.epicode.w6d5.devices_management.Models.reqDTO.DeviceDTO;
import it.epicode.w6d5.devices_management.Models.resDTO.DeleteRes;
import it.epicode.w6d5.devices_management.exceptions.BadRequestException;
import it.epicode.w6d5.devices_management.exceptions.NotFoundException;
import it.epicode.w6d5.devices_management.exceptions.ValidationMessages;
import it.epicode.w6d5.devices_management.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
public class DeviceController {
    @Autowired
    private DeviceService deviceSvc;

    @GetMapping("/devices")
    public Page<Device> getEmployees(Pageable pageable, @RequestParam(required = false) UUID employeeId,
                                     @RequestParam(required = false) Boolean assigned) {
        if ((employeeId != null) ^ (assigned != null)) {
            if (employeeId != null) {
                return deviceSvc.getByEmployeeId(pageable, employeeId);
            } else {
                return deviceSvc.getByAssigned(pageable, assigned);
            }

        }
        return deviceSvc.getAll(pageable);
    }

    @GetMapping("/devices/{id}")
    public Device getById(@PathVariable UUID id) throws NotFoundException {
        return deviceSvc.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/devices")
    public Device create(@RequestBody @Validated DeviceDTO deviceDTO, BindingResult validation) throws BadRequestException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        return deviceSvc.create(deviceDTO);
    }

    @PutMapping("/devices/{id}")
    public Device update(@RequestBody @Validated DeviceDTO deviceDTO, BindingResult validation, @PathVariable UUID id) throws BadRequestException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        return deviceSvc.update(deviceDTO, id);
    }

    /* NON è possibile fare una chiamata patch con body vuoto, quindi almeno un valore deve essere passato via body,
        altrimenti andrebbe usato il metodo get che però è incoerente con il tipo di operazione
     */
    @PatchMapping("/devices/{id}/assign-employee")
    public Device assignDeviceToEmployee(@RequestBody @Validated AssignDeviceToEmployeeDTO assignDeviceToEmployeeDTO,
                                         BindingResult validation, @PathVariable UUID id) throws BadRequestException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        try {
            return deviceSvc.assignDeviceToEmployee(UUID.fromString(assignDeviceToEmployeeDTO.employeeId()), id);
        } catch (IllegalArgumentException e) { // controllo aggiuntivo alla validazione
            throw new BadRequestException("'employeeId' field is malformed since it doesn't respect the Universal Unique ID pattern");
        }
    }

    @DeleteMapping("/devices/{id}")
    public DeleteRes delete(@PathVariable UUID id) throws BadRequestException {
        return deviceSvc.delete(id);
    }

}
