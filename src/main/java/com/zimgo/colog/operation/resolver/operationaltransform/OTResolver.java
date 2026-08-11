package com.zimgo.colog.operation.resolver.operationaltransform;

import com.zimgo.colog.deltaLog.DeltaLogService;
import com.zimgo.colog.document.Document;
import com.zimgo.colog.operation.Operation;
import com.zimgo.colog.operation.OperationService;
import org.springframework.stereotype.Service;

import java.util.List;

//Operational Transform Conflict Resolver
@Service
public class OTResolver {

    public DeltaLogService deltaLogService;
    public OperationService operationService;

    public OTResolver(DeltaLogService deltaLogService, OperationService operationService){
        this.deltaLogService = deltaLogService;
        this.operationService = operationService;
    }

    public Operation initOTResolver(Operation incoming, Document doc) {

        // 1.) check if it has the same revision, if it does, skip resolver
        if (isSameBaseRevision(doc, incoming)){
            return incoming;
        };

        List<Operation> missing =
                operationService.getMissingOperations(
                        doc.getDocumentId(),
                        incoming.getBaseRevision()
                );

        Operation transformed = incoming;



        // 3.) repeat until all previous document have been sync up so revisions will at latest
        for(Operation previous : missing){
            //transform - current from past
           transformed = transform(transformed,previous);
        }



        //4.) end and return
        return transformed;
    }

    private boolean isSameBaseRevision(Document doc, Operation op){
        if (doc.getCurrentRevision() == op.getBaseRevision()){
            return true;
        }else{
            return false;
        }
    }

    public Operation transform(Operation incoming, Operation previous){

        //Truth Table
        return switch (incoming.getOperationType()) {

            case INSERT -> switch (previous.getOperationType()) {
                case INSERT -> transformInsertAgainstInsert(incoming, previous);
                case DELETE -> transformInsertAgainstDelete(incoming, previous);
                default -> incoming;
            };

            case DELETE -> switch (previous.getOperationType()) {
                case INSERT -> transformDeleteAgainstInsert(incoming, previous);
                case DELETE -> transformDeleteAgainstDelete(incoming, previous);
                default -> incoming;
            };

            default -> incoming;
        };
    }

    // INSERT
    private Operation transformInsertAgainstInsert(Operation incoming, Operation previous){
        int incomingStart = incoming.getIndex();
        int incomingEnd = incomingStart + incoming.getLength();

        int previousStart = previous.getIndex();
        int previousEnd = previousStart + previous.getLength();

        if(previousStart <= incomingStart){
            incoming.setIndex(previous.getLength() + incoming.getIndex());
        }

        return  incoming;
    }

    private Operation transformInsertAgainstDelete(Operation incoming, Operation previous){

        int incomingStart = incoming.getIndex();
        int incomingEnd = incomingStart + incoming.getLength();

        int previousStart = previous.getIndex();
        int previousEnd = previousStart + previous.getLength();


        //if inserting after all deleted characters
        if (incomingStart >= previousEnd) {
            incoming.setIndex(incoming.getIndex() - previous.getIndex());

            //if inserting inside the characters
        }else if (incomingStart >= previousStart && incomingStart < previousEnd){

            //Just insert at the end
            incoming.setIndex(previousEnd);
        }

        return  incoming;
    }


    //DELETE
    private Operation transformDeleteAgainstInsert(Operation incoming, Operation previous){

        int incomingStart = incoming.getIndex();
        int incomingEnd = incomingStart + incoming.getLength();

        int previousStart = previous.getIndex();
        int previousEnd = previousStart + previous.getLength();


        if (previous.getIndex() < incoming.getIndex()) {
            incoming.setIndex(previous.getLength() + incoming.getIndex());
        }

        return incoming;
    }

    private Operation  transformDeleteAgainstDelete(Operation incoming, Operation previous){

        int previousStartIndex = previous.getIndex();
        int previousEndIndex = previous.getIndex() + previous.getLength();

        int incomingStartIndex = incoming.getIndex();
        int incomingEndIndex = incoming.getIndex() + incoming.getLength();

        //Case 1 Non-overlapping
        if(previousEndIndex <= incomingStartIndex){
            incoming.setIndex(incomingStartIndex - previous.getLength());

        //Case 2
        }else if (incomingStartIndex >= previousStartIndex && incomingEndIndex <= previousEndIndex){
            incoming.setLength(0);

        }else if(incomingStartIndex >= previousStartIndex
                && incomingStartIndex < previousEndIndex
                && incomingEndIndex > previousEndIndex){

            incoming.setIndex(previousStartIndex);
            incoming.setLength(incomingEndIndex - previousEndIndex);

        }else if(incomingStartIndex < previousStartIndex
                && incomingEndIndex > previousStartIndex
                && incomingEndIndex <= previousEndIndex){

            incoming.setLength(
                    previousStartIndex - incomingStartIndex
            );
        }

        else if(incomingStartIndex < previousStartIndex && incomingEndIndex > previousEndIndex){

            Operation merged = splitMerge(incoming, previous);
            return merged;
        }


        return incoming;
    }


    private Operation splitMerge(Operation incoming, Operation previous)
    {

        int pStart = previous.getIndex();
        int pEnd = previous.getIndex() + previous.getLength();

        int iStart = incoming.getIndex();
        int iEnd = incoming.getIndex() + incoming.getLength();

        // Left interval
        int leftStart = iStart;
        int leftEnd = pStart;

        // Right interval (before shift)
        int rightStart = pEnd;
        int rightEnd = iEnd;


        int shift = previous.getLength();

        switch (previous.getOperationType()){
            case INSERT -> {
                rightStart += shift;
                rightEnd += shift;
            }

            case DELETE -> {
                rightStart -= shift;
                rightEnd -= shift;
            }

        }

        // Merge
        incoming.setIndex(leftStart);
        incoming.setLength(rightEnd - leftStart);

        return incoming;
    }

}

