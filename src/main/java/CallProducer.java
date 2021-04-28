import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;

public class CallProducer {

    private LinkedTransferQueue<Call> transferQueue;
    private ExecutorService producer;

    private int startCallId = 1000;
    private Object lock = new Object();
    private volatile boolean terminate;

    public CallProducer(LinkedTransferQueue<Call> transferQueue) {
        this.transferQueue = transferQueue;
        producer = Executors.newFixedThreadPool(3);
    }
    public void produceCalls() {
        //producer on thread one
        producer.execute(addCall);
        producer.execute(addCall);
        producer.execute(addCall);
    }

    Runnable addCall = new Runnable() {
        @Override
        public void run() {
            long threadId = Thread.currentThread().getId();

            //post calls until it needs to be shutdown
            while(!terminate) {
                try {
                    Call call = getCall();
                    System.out.println("thread: " + threadId
                            + " adding call for processing: id: "
                            + call.getCallId() + ", text: " + call.getCallMessage());

                    //add call object to queue and wait for consumer
                    transferQueue.transfer(call);
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("producer thread: " + threadId + " stopped");
        }
    };

    public void clearWaitingConsumers() {
        //to prevent blocking consumers
        //before existing post dummy call if there are waiting consumers
        while(transferQueue.hasWaitingConsumer()) {
            transferQueue.put(new Call());
        }
    }
    public void shutDownExecutor() {
        producer.shutdown();
    }
    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    public Call getCall() {
        int callId;
        synchronized(lock) {
            callId = startCallId++;
        }

        Call call = new Call();
        call.setCallId("" + callId);
        call.setCallMessage(getCallMsg());

        return call;
    }

    public String getCallMsg() {
        Random random = new Random();

        char nextChar;
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < 30; i++) {
            // lowercase characters go from 97 to 122
            nextChar = (char) (random.nextInt(26) + 97);
            sb.append(nextChar);
            if ((i + 1) % 5 == 0 && i != 19) sb.append(' ');
        }

        return sb.toString();
    }
}
