package com.phytel.patient.match.exceptions;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
/**
 * This class handles all custom exceptions 
 *
 */
@ControllerAdvice
public class PmsExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = AlgorithmDefinitionException.class)
	public final ResponseEntity<Object> handleSequenceNotFoundException(AlgorithmDefinitionException ex,
			WebRequest request) throws Exception {

		PatientMatchingErrorResponse errorResponse = new PatientMatchingErrorResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		logger.error(ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = CriteriaDefinitionException.class)
	public final ResponseEntity<Object> handleCriteriaDefinitionException(CriteriaDefinitionException ex,
			WebRequest request) throws Exception {

		PatientMatchingErrorResponse errorResponse = new PatientMatchingErrorResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		logger.error(ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = SqlQueryExecutionException.class)
	public final ResponseEntity<Object> handleSQLException(SqlQueryExecutionException ex,
			WebRequest request) throws Exception {

		PatientMatchingErrorResponse errorResponse = new PatientMatchingErrorResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		logger.error(ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = AttributeValueNotFoundException.class)
    public final ResponseEntity<Object> handleAttributeValueNotFoundException(AttributeValueNotFoundException ex,
            WebRequest request) throws Exception {
        PatientMatchingErrorResponse errorResponse = new PatientMatchingErrorResponse(new Date(), ex.getMessage(),
                request.getDescription(false));
        logger.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(value = DataStoreNotFoundException.class)
    public final ResponseEntity<Object> handleDataStoreNotFoundException(DataStoreNotFoundException ex,
            WebRequest request) throws Exception {
        PatientMatchingErrorResponse errorResponse = new PatientMatchingErrorResponse(new Date(), ex.getMessage(),
                request.getDescription(false));
        logger.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(value = ContractNumberNotFoundException.class)
    public final ResponseEntity<Object> handleDataStoreNotFoundException(ContractNumberNotFoundException ex,
            WebRequest request) throws Exception {
        PatientMatchingErrorResponse errorResponse = new PatientMatchingErrorResponse(new Date(), ex.getMessage(),
                request.getDescription(false));
        logger.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(value = RuntimeException.class)
    public final ResponseEntity<Object> handleRuntimeException(RuntimeException ex,
            WebRequest request) throws Exception {
        PatientMatchingErrorResponse errorResponse = new PatientMatchingErrorResponse(new Date(), ex.getMessage(),
                request.getDescription(false));
        logger.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
}
