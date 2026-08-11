package com.zimgo.colog.operation;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationService {
    public OperationRepository operationRepository;
    public OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }


    public void createOperation(Operation operation){
        operationRepository.save(operation);
    }

    public Operation readOperation(Long operationId){

        if(!operationRepository.existsById(operationId)){
            throw new RuntimeException("Operation does not exist!");
        }
        return operationRepository.findById(operationId).get();
    }

    public void updateOperation(Operation operation, Long operationId){
        if(!operationRepository.existsById(operationId)){
            throw new RuntimeException("Operation does not exist!");
        }
    }

    public void deleteOperation(Long operationId){
        if(!operationRepository.existsById(operationId)){
            throw new RuntimeException("Operation does not exist!");
        }
    }
    public void saveOperation(Operation operation){
        operationRepository.save(operation);
    }

    public List<Operation> getMissingOperations(
            Long documentId,
            Long baseRevision
    ){
        return operationRepository.findByDocumentDocumentIdAndAppliedRevisionGreaterThanOrderByAppliedRevisionAsc(
                documentId,
                baseRevision
        );
    }


}
