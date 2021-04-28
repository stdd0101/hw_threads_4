import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;

public class CallConsumer {
    private LinkedTransferQueue<Call> transferQueue;

    private ExecutorService consumer;
    private volatile boolean terminate;

    public CallConsumer(LinkedTransferQueue<Call> transferQueue){
        this.transferQueue = transferQueue;
        consumer = Executors.newFixedThreadPool(4);
    }

    public void consumeCalls() {
        //consumer on thread one
        consumer.execute(takeOrder);
        //consumer on thread two
        consumer.execute(takeOrder);
        consumer.execute(takeOrder);
        consumer.execute(takeOrder);
    }

    Runnable takeOrder = new Runnable() {
        @Override
        public void run() {
            long threadId = Thread.currentThread().getId();

            //consume calls until it needs to be shutdown
            while(!terminate) {
                try {
                    Call call = transferQueue.take();
                    call.markAsAnswered();
                    Thread.sleep(240);
                    System.out.println("thread: " + threadId
                            + " received and answered call: "
                            + call.getCallId());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("consumer thread: " + threadId + " stopped");
        }
    };

    public void shutDownExecutor() {
        consumer.shutdown();
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }
}
