package com.mynet.questservice;

import com.mynet.shared.utils.Utils;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class RabbitConsumerThread implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(RabbitConsumerThread.class);
    private final String queueName;
    private final String queueHost;
    private final String queueUsername;
    private final String queuePass;

    private Connection connection;
    private Channel channel;

    private boolean autoAck;
    private AtomicBoolean active;

    public RabbitConsumerThread(String queueName, String queueHost, String queueUsername, String queuePass) {
        this.queueName = queueName;
        this.queueHost = queueHost;
        this.queueUsername = queueUsername;
        this.queuePass = queuePass;

        this.autoAck = false;
        this.active = new AtomicBoolean();
    }

    private synchronized boolean initConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.queueHost);
        factory.setUsername(this.queueUsername);
        factory.setPassword(this.queuePass);

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            connection.addShutdownListener(e -> System.out.println(String.format("CONSUME ERROR: %s | AT: %s", e.getMessage(), Utils.getCurrentDate())));

        } catch (IOException | TimeoutException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }

        return true;
    }

    public void setActive(boolean active) {
        this.active.lazySet(active);
    }

    public boolean isRabbitConnected(){
        return connection != null && connection.isOpen();
    }

    public void start() {
        this.active.set(true);
        Thread worker = new Thread(this);
        worker.start();

        System.out.println("SERVICE CONSUMING STARTED AT: " + Utils.getCurrentDate());
    }

    public void stop() {
        this.active.set(false);
        System.out.println("SERVICE CONSUMING STOPPED AT: " + Utils.getCurrentDate());

        try {
            if(channel == null || connection == null) return;

            if (channel.isOpen()) channel.close();
            if (connection.isOpen()) connection.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }


    @Override
    public void run() {

        try {
            logger.info("RABBIT THREAD RUNNING!!!!");
            if (connection == null || !connection.isOpen()) {
                boolean initSuccess = initConnection();
                if (!initSuccess) {
                    logger.error("Cannot init RabbitMQ: " + toString());
                    return;
                }
            }

            Thread.sleep(2000);

            //  channel.queueDeclare(queueName, false, false, false, null);
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(
                        String consumerTag,
                        Envelope envelope,
                        AMQP.BasicProperties properties,
                        byte[] body) throws IOException {

                    String message = new String(body, StandardCharsets.UTF_8);
                    logger.info(message);


                    long deliveryTag = envelope.getDeliveryTag();
                    QuestController controller = QuestController.getInstance();

                    if(controller.isReady()){
                        controller.addMessage(message);
                        channel.basicAck(deliveryTag, false);
                    }
                }
            };

            channel.basicConsume(queueName, autoAck, consumer);

        } catch (IOException | InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
            try {
                if (channel.isOpen()) channel.close();
                if (connection.isOpen()) connection.close();
                logger.error("RABBITMQ CONNECTION CLOSED!!!");
            } catch (IOException | TimeoutException e) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public String toString() {
        return "RabbitConsumerThread{" +
                "queueName='" + queueName + '\'' +
                '}';
    }
}
