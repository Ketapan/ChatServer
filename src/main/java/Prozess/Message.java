package Prozess;

import java.awt.*;
import java.io.Serializable;

public class Message implements Serializable{
    String messageString = null;
    String messageTo = null;
    String type = null;
    Image img = null;

    public Message(String messageString, String messageTo, String type, Image img){
        this.messageString = messageString;
        this.messageTo = messageTo;
        this.type = type;
        this.img = img;
    }

    public Message(){

    }

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MessageObject.Message{" +
                "messageString='" + messageString + '\'' +
                ", messageTo='" + messageTo + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
