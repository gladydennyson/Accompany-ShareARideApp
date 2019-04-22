package com.example.accompany;



public class GroupActions {


    private String messageUser;
    //private long msgId;
    private int noofpeople;

    public GroupActions(Integer noofpeople, String messageUser) {

        this.messageUser = messageUser;
        //this.msgId = msgId;
        this.noofpeople = noofpeople;

    }

    public GroupActions(){

    }

//    public long getMsgId() {
//        return msgId;
//    }
//
//    public void setMsgId(long msgId) {
//        this.msgId = msgId;
//    }


    public int getNoofpeople() {
        return noofpeople;
    }

    public void setNoofpeople(int noofpeople) {
        this.noofpeople = noofpeople;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }


}
