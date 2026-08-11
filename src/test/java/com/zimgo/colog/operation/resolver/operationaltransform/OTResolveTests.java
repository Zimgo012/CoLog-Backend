package com.zimgo.colog.operation.resolver.operationaltransform;

import com.zimgo.colog.document.Document;
import com.zimgo.colog.operation.Operation;
import com.zimgo.colog.operation.OperationService;
import com.zimgo.colog.operation.OperationType;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class OTResolveTests {

    private final OperationService operationService = mock(OperationService.class);
    private final OTResolver resolver = new OTResolver(null, operationService);


    //Insert Vs Insert
    // before incomming index


    @Test
    void insertAgainstInsert_previousBeforeIncoming_shouldShiftRight() {

        Operation previous = new Operation();
        previous.setOperationType(OperationType.INSERT);
        previous.setIndex(1);
        previous.setLength(1);

        Operation incoming = new Operation();
        incoming.setOperationType(OperationType.INSERT);
        incoming.setIndex(3);
        incoming.setLength(1);


        Operation result = resolver.transform(incoming, previous);


        assertEquals(4, result.getIndex());
    }


    // after incoming index
    @Test
    void insertAgainstInsert_previousAfterIncoming_shouldStaySame() {

        Operation previous = new Operation();
        previous.setOperationType(OperationType.INSERT);
        previous.setIndex(10);
        previous.setLength(3);

        Operation incoming = new Operation();
        incoming.setOperationType(OperationType.INSERT);
        incoming.setIndex(5);
        incoming.setLength(1);


        Operation result = resolver.transform(incoming, previous);


        assertEquals(5, result.getIndex());
    }

    @Test
    void insertAgainstInsert_sameIncoming_shouldShiftRight() {

        Operation previous = new Operation();
        previous.setOperationType(OperationType.INSERT);
        previous.setIndex(2);
        previous.setLength(3);

        Operation incoming = new Operation();
        incoming.setOperationType(OperationType.INSERT);
        incoming.setIndex(2);
        incoming.setLength(3);


        Operation result = resolver.transform(incoming, previous);


        assertEquals(5, result.getIndex());
    }


    //Insert vs Delete

    //before
    @Test
    void insertAgainstDelete_previousBeforeIncoming_shouldShiftLeft() {

        Operation previous = new Operation();
        previous.setOperationType(OperationType.DELETE);
        previous.setIndex(1);
        previous.setLength(1);


        Operation incoming = new Operation();
        incoming.setOperationType(OperationType.INSERT);
        incoming.setIndex(2);
        incoming.setLength(1);


        Operation result = resolver.transform(incoming, previous);


        assertEquals(1, result.getIndex());
    }

    //after
    @Test
    void insertAgainstDelete_previousAfterIncoming_shouldStaySame() {

        Operation previous = new Operation();
        previous.setOperationType(OperationType.DELETE);
        previous.setIndex(10);
        previous.setLength(3);


        Operation incoming = new Operation();
        incoming.setOperationType(OperationType.INSERT);
        incoming.setIndex(5);
        incoming.setLength(1);


        Operation result = resolver.transform(incoming, previous);


        assertEquals(5, result.getIndex());
    }

    //Delete vs Insert
    //before
    @Test
    void deleteAgainstInsert_previousBeforeIncoming_shouldShiftRight() {

        Operation previous = new Operation();
        previous.setOperationType(OperationType.INSERT);
        previous.setIndex(2);
        previous.setLength(3);


        Operation incoming = new Operation();
        incoming.setOperationType(OperationType.DELETE);
        incoming.setIndex(3);
        incoming.setLength(1);


        Operation result = resolver.transform(incoming, previous);


        assertEquals(6, result.getIndex());
    }

    //after
    @Test
    void deleteAgainstInsert_previousAfterIncoming_shouldStaySame() {

        Operation previous = new Operation();
        previous.setOperationType(OperationType.INSERT);
        previous.setIndex(10);
        previous.setLength(3);


        Operation incoming = new Operation();
        incoming.setOperationType(OperationType.DELETE);
        incoming.setIndex(5);
        incoming.setLength(2);


        Operation result = resolver.transform(incoming, previous);


        assertEquals(5, result.getIndex());
    }

    //Delete vs Delete
    //before
    @Test
    void deleteAgainstDelete_previousBeforeIncoming_shouldShiftLeft() {

        Operation previous = new Operation();
        previous.setOperationType(OperationType.DELETE);
        previous.setIndex(2);
        previous.setLength(3);


        Operation incoming = new Operation();
        incoming.setOperationType(OperationType.DELETE);
        incoming.setIndex(8);
        incoming.setLength(2);


        Operation result = resolver.transform(incoming, previous);


        assertEquals(5, result.getIndex());
    }

    //after
    @Test
    void deleteAgainstDelete_previousAfterIncoming_shouldStaySame() {

        Operation previous = new Operation();
        previous.setOperationType(OperationType.DELETE);
        previous.setIndex(10);
        previous.setLength(3);


        Operation incoming = new Operation();
        incoming.setOperationType(OperationType.DELETE);
        incoming.setIndex(5);
        incoming.setLength(2);


        Operation result = resolver.transform(incoming, previous);


        assertEquals(5, result.getIndex());
    }

    @Test
    void initOTResolver_sameRevision_shouldReturnSameOperation() {

        Document doc = new Document();
        doc.setDocumentId(1L);
        doc.setCurrentRevision(5L);


        Operation incoming = new Operation();
        incoming.setOperationType(OperationType.INSERT);
        incoming.setIndex(10);
        incoming.setLength(2);
        incoming.setBaseRevision(5L);


        Operation result =
                resolver.initOTResolver(
                        incoming,
                        doc
                );


        assertSame(
                incoming,
                result
        );


        verifyNoInteractions(operationService);
    }

    @Test
    void initOTResolver_shouldTransformThroughMissingOperations() {

        Document doc = new Document();
        doc.setDocumentId(1L);
        doc.setCurrentRevision(5L);


    /*
        Incoming operation created from revision 3

        INSERT index 10
     */
        Operation incoming = new Operation();
        incoming.setOperationType(OperationType.INSERT);
        incoming.setIndex(10);
        incoming.setLength(2);
        incoming.setBaseRevision(3L);


    /*
        Missing operation 1:

        INSERT index 5 length 3

        Incoming:
        10 + 3 = 13
     */
        Operation previous1 = new Operation();
        previous1.setOperationType(OperationType.INSERT);
        previous1.setIndex(5);
        previous1.setLength(3);


    /*
        Missing operation 2:

        INSERT index 7 length 2

        Incoming:
        13 + 2 = 15
     */
        Operation previous2 = new Operation();
        previous2.setOperationType(OperationType.INSERT);
        previous2.setIndex(7);
        previous2.setLength(2);



        when(operationService.getMissingOperations(
                1L,
                3L
        )).thenReturn(
                List.of(previous1, previous2)
        );


        Operation result =
                resolver.initOTResolver(
                        incoming,
                        doc
                );


        assertEquals(
                15,
                result.getIndex()
        );


        verify(operationService)
                .getMissingOperations(
                        1L,
                        3L
                );
    }


}

