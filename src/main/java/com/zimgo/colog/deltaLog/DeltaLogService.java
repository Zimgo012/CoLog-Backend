package com.zimgo.colog.deltaLog;

import com.zimgo.colog.document.Document;
import com.zimgo.colog.operation.Operation;
import com.zimgo.colog.operation.OperationService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class DeltaLogService {

    private String pathfilehead = "storage";
    public DeltaLogRepository deltaLogRepository;
    public OperationService operationService;

    public DeltaLogService(DeltaLogRepository deltaLogRepository, OperationService operationService) {
        this.operationService = operationService;
        this.deltaLogRepository = deltaLogRepository;
    }

    public DeltaLog createDeltaLog(DeltaLog deltaLog){
        return deltaLogRepository.save(deltaLog);
    }

    public DeltaLog readDeltaLog(Long deltaLogId){

        if (!deltaLogRepository.existsById(deltaLogId)) {
            throw new RuntimeException("DeltaLog does not exist!");
        }

        return deltaLogRepository.findById(deltaLogId).get();
    }

    //TODO: fix function to properly get the top log
    public DeltaLog findTopLogByDocumentId(Long documentId){
        return deltaLogRepository.findTopByDocumentDocumentIdOrderByDeltaLogIdDesc(documentId);
    }

    public void forwardOperation(){
        //TODO: apply forward operation , cant forward if it is the top operation - undoing
    }

    public void backwardOperation(){
        /* TODO: apply backward operation , cant backward if it is the bottom operation
                - redoing
        */
    }

    // Apply a new operation
    public void applyOperation(Operation op, DeltaLog log) throws IOException {

        String content = retrieveContent(log);

        content = applyToContent(content, op);

        overwriteContent(log.getContentPointer(), content);

//        operationService.saveOperation(op);
    }



    public DeltaLog rotateLog(DeltaLog oldLog) throws IOException {
        // 1. Read old content
        String content = retrieveContent(oldLog);

        // 2. Create new DeltaLog
        DeltaLog newLog = new DeltaLog();
        newLog.setDocument(oldLog.getDocument());
        newLog.setCreatedAt(LocalDateTime.now());

        // 3. Save first to generate ID
        deltaLogRepository.save(newLog);

        // 4. Create new file path using NEW ID
        String newPath = saveContent(
                oldLog.getDocument().getDiary().getDiaryId(),
                oldLog.getDocument().getDocumentId(),
                newLog.getDeltaLogId(),
                content
        );

        // 5. Set pointer
        newLog.setContentPointer(newPath);

        // 6. Save updated log
        deltaLogRepository.save(newLog);

        return newLog;

    }

    public boolean shouldRotateLog(DeltaLog log){
        if (log == null) {
            return false;
        }


        Duration duration = Duration.between(log.getCreatedAt(), LocalDateTime.now());
        long hoursAgo = duration.toHours();

        //log.getOperations().size() > 100 &&
        return hoursAgo > 1;
    }

    public DeltaLog createInitialLog(Document doc) throws IOException {

        DeltaLog log = new DeltaLog();
        log.setDocument(doc);
        log.setCreatedAt(LocalDateTime.now());
        log.setOperations(new ArrayList<>());

        // 1. Save first to generate ID
        DeltaLog saved = deltaLogRepository.save(log);

        // 2. Create file path using generated ID
        String path = saveContent(
                doc.getDiary().getDiaryId(),
                doc.getDocumentId(),
                saved.getDeltaLogId(),
                ""
        );

        // 3. Set pointer
        saved.setContentPointer(path);

        // 4. Persist again
        return deltaLogRepository.save(saved);
    }

    private String applyToContent(String content, Operation op) {

        if (content == null) {
            content = "";
        }

        int len = content.length();
        int index = Math.max(0, Math.min(op.getIndex(), len));

        switch (op.getOperationType()) {

            case INSERT:
                String text = op.getText() == null ? "" : op.getText();
                return content.substring(0, index)
                        + text
                        + content.substring(index);

            case DELETE:
                int end = Math.min(index + op.getLength(), len);
                return content.substring(0, index)
                        + content.substring(end);

            default:
                throw new RuntimeException("Invalid operation type");
        }
    }


    // Save content to a file
    private String saveContent(Long diaryId, Long documentId,Long deltaLogId, String content) throws IOException {

        Path directory =
                Paths.get(
                        "storage",
                        "diaryId-" + diaryId,
                        "document-" + documentId
                );

        Files.createDirectories(directory);

        Path filePath =
                directory.resolve(
                        "revision-" + deltaLogId + ".txt"
                );


        Files.writeString(
                filePath,
                content,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );


        return filePath.toString();
    }

    private void overwriteContent(String path, String content) throws IOException {
        Files.writeString(
                Paths.get(path),
                content,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }


    // Retrieve content to a file
    private String retrieveContent(DeltaLog log) throws IOException {

        String filePointer = log.getContentPointer();

        Path filePath = Paths.get(filePointer);

        if (!Files.exists(filePath)) {
            return "";
        }

        return Files.readString(filePath);
    }




}
