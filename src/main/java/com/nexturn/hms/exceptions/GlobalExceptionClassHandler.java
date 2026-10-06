package com.nexturn.hms.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
	   
	   @ExceptionHandler(BillNotFoundException.class)
		public ResponseEntity<String> billNotFound(BillNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	 
		@ExceptionHandler(InvalidBillException.class)
		public ResponseEntity<String> invalidBill(InvalidBillException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	 
		@ExceptionHandler(AddressNotFoundException.class)
		public ResponseEntity<String> addressNotFound(AddressNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		 
		
		@ExceptionHandler(MethodArgumentNotValidException.class)
		public ResponseEntity<String> validationError(MethodArgumentNotValidException e) {
			StringBuilder message = new StringBuilder();
			for (FieldError error : e.getBindingResult().getFieldErrors()) {
				message.append(error.getField()).append(": ").append(error.getDefaultMessage()).append(". ");
			}
			return new ResponseEntity<>(message.toString().trim(), HttpStatus.BAD_REQUEST);
		}
	 
		@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
		public ResponseEntity<String> badRequest(Exception e) {
			return new ResponseEntity<>("Invalid request: please check the values you entered", HttpStatus.BAD_REQUEST);
		}
	 
		
		@ExceptionHandler(DataIntegrityViolationException.class)
		public ResponseEntity<String> dataIntegrity(DataIntegrityViolationException e) {
			return new ResponseEntity<>("This record is linked to other records, so the action cannot be completed",
					HttpStatus.CONFLICT);
		}
	 
		@ExceptionHandler(Exception.class)
		public ResponseEntity<String> anyOtherError(Exception e) {
			return new ResponseEntity<>("Something went wrong on the server. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	   
}
