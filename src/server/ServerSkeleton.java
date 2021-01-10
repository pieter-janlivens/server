package server;


import server.communication.MessageManager;
import server.communication.MethodCallMessage;

import java.awt.event.TextEvent;
import java.awt.event.TextListener;

public final class ServerSkeleton{
    private final MessageManager messageManager;
    private final Server server;

    public ServerSkeleton() {
        this.messageManager = new MessageManager();
        this.server = new ServerImpl();
    }

    private void sendEmptyReply(MethodCallMessage request) {
        MethodCallMessage reply = new MethodCallMessage(messageManager.getMyAddress(), "result");
        reply.setParameter("result", "Ok");
        messageManager.send(reply, request.getOriginator());
    }


    public void handleLog(MethodCallMessage request) {

        String text = request.getParameter("text");

        server.log(new DocumentImpl(text));
        sendEmptyReply(request);

    }


    public void handleCreate(MethodCallMessage request) {

        String text = request.getParameter("text");
        Document document = server.create(text);

        MethodCallMessage reply = new MethodCallMessage(messageManager.getMyAddress(), "result");
        reply.setParameter("result", "Ok");
        reply.setParameter("text", document.getText());

        messageManager.send(reply, request.getOriginator());
    }


    public void handleToUpper(MethodCallMessage request) {

        String text = request.getParameter("text");
        Document document = new DocumentImpl(text);
        server.toUpper(document);

        MethodCallMessage reply = new MethodCallMessage(messageManager.getMyAddress(), "resultToUpper");
        reply.setParameter("result","Ok");
        reply.setParameter("text", document.getText());
        messageManager.send(reply, request.getOriginator());
    }


    public void handleToLower(MethodCallMessage request) {

        String text = request.getParameter("text");
        Document document = new DocumentImpl(text);
        server.toLower(document);

        MethodCallMessage reply = new MethodCallMessage(messageManager.getMyAddress(), "resultToLower");
        reply.setParameter("result","Ok");
        reply.setParameter("text", document.getText());
        messageManager.send(reply, request.getOriginator());
    }


    public void handleType(MethodCallMessage request) {


        String text = request.getParameter("text");
        String textToAppend = request.getParameter("textToAppend");
        DocumentImpl document = new DocumentImpl(text);

        TextListener listener = new TextListener() {
            @Override
            public void textValueChanged(TextEvent e) {
                MethodCallMessage reply = new MethodCallMessage(messageManager.getMyAddress(), "resultType");
                reply.setParameter("result","Typing");
                reply.setParameter("text", document.getText());
                messageManager.send(reply, request.getOriginator());
            }
        };
        document.setTextListener(listener);
        server.type(document,textToAppend);
        sendEmptyReply(request);



    }

    private void handleRequest(MethodCallMessage request) {
        String methodName = request.getMethodName();
        if ("log".equals(methodName)) {
            handleLog(request);
        } else if ("create".equals(methodName)) {
            handleCreate(request);
        } else if ("toUpper".equals(methodName)) {
            handleToUpper(request);
        } else if ("toLower".equals(methodName)) {
            handleToLower(request);
        } else if ("type".equals(methodName)) {
            handleType(request);
        } else {
            //todo change to proper error
            System.out.println("ServerSkeleton: received an unknown request:");
            System.out.println(request);
        }
    }

    public void run() {
        while (true) {
            System.out.println("listening");
            MethodCallMessage request = messageManager.wReceive();
            handleRequest(request);
        }
    }




}
