import java.util.concurrent.LinkedTransferQueue;

public class CallManager {

    private LinkedTransferQueue<Call> transferQueue;//неограниченная по емкости и ориентированная на многопоточное исполнение очередь;

    public static void main(String[] args) {
        CallManager cm = new CallManager();
        cm.startCallManager();
    }

    public CallManager() {
        transferQueue = new LinkedTransferQueue<Call>();
    }

    public void startCallManager() {
        //start call producer
        CallProducer cp = new CallProducer(transferQueue);
        cp.produceCalls();

        //start call consumer
        CallConsumer cc = new CallConsumer(transferQueue);
        cc.consumeCalls();

        System.out.println("Calls processing started");

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //ask producer threads to stop
        cp.setTerminate(true);

        //wait before asking consumer threads to stop
        //to prevent blocking producers issue
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //ask consumer threads to stop
        cc.setTerminate(true);

        System.out.println("Calls processing stopping after 260 milliseconds");

        //wait for the remaining work to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //shutdown producer thread executor
        cp.shutDownExecutor();
        //clear any blocking consumer with dummy calls
        cp.clearWaitingConsumers();

        //shutdown consumer thread executor
        cc.shutDownExecutor();
    }
}
