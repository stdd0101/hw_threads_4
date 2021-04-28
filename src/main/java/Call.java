public class Call {

    private String callId;
    private String callMessage;
    private Boolean isAnswered = false;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public void markAsAnswered() {
        this.isAnswered = true;
    }

    public String getCallMessage() {
        return callMessage;
    }

    public void setCallMessage(String callMessage) {
        this.callMessage = callMessage;
    }
}
