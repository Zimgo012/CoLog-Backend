package com.zimgo.colog.messages.services.subservices;

import com.zimgo.colog.operation.Operation;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class LWWSequencerService {

    /**
     * Long - DocumentId
     * BlockingQueue - Thread - safe queue
     */

    private final Map<Long, BlockingQueue<Runnable>> queues = new ConcurrentHashMap<>(); //queues for the document

    // Worker


    public void submit(Long documentId, Runnable task) {


            //get queue of that doc and check if it has queue

            // This line of code is not thread. If multiple WebSocket threads hit this at the same time, it can accidentally create 2 queues
            /*
            BlockingQueue<Runnable> queue = queues.get(documentId);

            if(queue == null) {
                queue = new LinkedBlockingQueue<>();
                queues.put(documentId, queue);
            }
             */

            BlockingQueue<Runnable> queue = queues.computeIfAbsent(documentId, id -> {

                BlockingQueue<Runnable> q = new LinkedBlockingQueue<>();

                startWorker(documentId, q);

                return q;
            });


            queue.offer(task);


    };
    private void startWorker(Long documentId, BlockingQueue<Runnable> queue) {

        Thread worker = new Thread(() -> {
            System.out.println("Created Thread !");
            while (true){
                try{
                   Runnable task = queue.take();
                   task.run();

                }catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        worker.setName("doc-sequencer-" + documentId);
        worker.start();

    }
}
