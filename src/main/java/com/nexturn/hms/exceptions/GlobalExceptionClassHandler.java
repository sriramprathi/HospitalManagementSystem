package com.nexturn.hms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionClassHandler {
	
	@ExceptionHandler(value=InvalidCredentialsException.class)
	public ResponseEntity<String> invalidCredentialsException(InvalidCredentialsException e){
		return new ResponseEntity<String> (e.getMessage(),HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(value=UserNotFoundException.class)
	public ResponseEntity<String> userNotFoundException(UserNotFoundException e){
		return new ResponseEntity<String> (e.getMessage(),HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value=PatientNotFoundException.class)
	public ResponseEntity<String> patientNotFoundException(PatientNotFoundException e){
		return new ResponseEntity<String> (e.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value=DoctorNotFoundException.class)
	public ResponseEntity<String> doctorNotFoundException(DoctorNotFoundException e){
		return new ResponseEntity<String> (e.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	   @ExceptionHandler(value=AppointmentNotFoundException.class)
	   public ResponseEntity<String> appointmentNotFound(AppointmentNotFoundException e){
	   	return new ResponseEntity<String> (e.getMessage(), HttpStatus.NOT_FOUND);
	   }

	   @ExceptionHandler(value=SlotNotAvailableException.class)
	   public ResponseEntity<String> slotNotAvailable(SlotNotAvailableException e){
	   	return new ResponseEntity<String> (e.getMessage(), HttpStatus.CONFLICT);
	   }

	   @ExceptionHandler(value=InvalidAppointmentException.class)
	   public ResponseEntity<String> invalidAppointment(InvalidAppointmentException e){
	   	return new ResponseEntity<String> (e.getMessage(), HttpStatus.BAD_REQUEST);
	   }
	   
	   @ExceptionHandler(value=PrescriptionNotFoundException.class)
	   public ResponseEntity<String> prescriptionNotFound(PrescriptionNotFoundException e){
	   	return new ResponseEntity<String> (e.getMessage(), HttpStatus.NOT_FOUND);
	   }

	   @ExceptionHandler(value=MedicineNotFoundException.class)
	   public ResponseEntity<String> medicineNotFound(MedicineNotFoundException e){
	   	return new ResponseEntity<String> (e.getMessage(), HttpStatus.NOT_FOUND);
	   }

	   @ExceptionHandler(value=PrescriptionAlreadyExistsException.class)
	   public ResponseEntity<String> prescriptionExists(PrescriptionAlreadyExistsException e){
	   	return new ResponseEntity<String> (e.getMessage(), HttpStatus.CONFLICT);
	   }

	   @ExceptionHandler(value=InvalidPrescriptionException.class)
	   public ResponseEntity<String> invalidPrescription(InvalidPrescriptionException e){
	   	return new ResponseEntity<String> (e.getMessage(), HttpStatus.BAD_REQUEST);
	   }
	   
}
