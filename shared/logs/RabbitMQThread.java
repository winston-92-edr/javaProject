package com.mynet.shared.logs;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RabbitMQThread extends Thread {

    private final String queueName;
    private final String queueHost;
    private final String queueUsername;
    private final String queuePass;

    private BlockingQueue<QueueElement> queries;
    protected Connection connection;

    private volatile boolean isAborted;

    private final Logger logger = LoggerFactory.getLogger(RabbitMQThread.class);


    public RabbitMQThread(String queueName, String queueHost, String queueUsername, String queuePass) {
        this.queueName = queueName;
        this.queueHost = queueHost;
        this.queueUsername = queueUsername;
        this.queuePass = queuePass;

        this.isAborted = false;
        this.queries = new ArrayBlockingQueue<QueueElement>(1000);

        this.setPriority(Thread.MIN_PRIORITY);
        this.start();
    }

    public void addQuery(QueueElement t) {
        this.queries.offer(t);
    }

    private synchronized boolean initConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.queueHost);
        factory.setUsername(this.queueUsername);
        factory.setPassword(this.queuePass);

        try {
            connection = factory.newConnection();

            connection.addShutdownListener(new ShutdownListener() {

                public void shutdownCompleted(ShutdownSignalException e) {
                    logger.error(e.getMessage(), e);
                }
            });

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        } catch (TimeoutException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }

        return true;
    }


    @Override
    public void run() {
        while (!this.isAborted) {
            try {
                QueueElement query = this.queries.take();
                sendLogQueue(query, queueName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void sendLogQueue(QueueElement query, String queueName) {
        if (connection == null || !connection.isOpen()) {
            boolean initSuccess = initConnection();
            if (!initSuccess) {
                logger.error("Cannot init RabbitMQ: " + toString());
                return;
            }
        }

        byte[] bytes = query.getBytes();

        if (bytes == null) {
            logger.debug("Bytes are null!!");
            return;
        }

        Channel channel = null;
        try {
            channel = connection.createChannel();
            channel.queueDeclare(queueName, true, false, false, null);
            channel.basicPublish("", queueName,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    bytes);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException ex) {
                    logger.error(ex.getMessage(), ex);
                } catch (TimeoutException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }


    @Override
    public String toString() {
        return "[Name : " + queueName + ", Host: " + queueHost + ", Username: " + queueUsername + ", Pass: " + queuePass + "]";
    }
}
