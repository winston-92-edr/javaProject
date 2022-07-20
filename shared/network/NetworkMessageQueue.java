package com.mynet.shared.network;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.network.xml.XmlParser;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class NetworkMessageQueue {
    public static final Logger logger = LoggerFactory.getLogger(NetworkMessageQueue.class);

    private BlockingQueue<NetworkMessageWrapper> messages;
    private int QUEUE_SIZE = 10000;
    private int messageWorkerCount = 4;
    private MessageQueueWorker[] workers;


    public NetworkMessageQueue(int workerCount) {
        messageWorkerCount = workerCount;
        messages = new ArrayBlockingQueue<>(QUEUE_SIZE);
        workers = new MessageQueueWorker[messageWorkerCount];
    }


    public void init() {
        for (int i = 0; i < messageWorkerCount; i++) {
            workers[i] = new MessageQueueWorker(messages);
            workers[i].setName("Message Queue Worker " + i);
            workers[i].start();
        }
    }

    public void addMessage(NetworkMessageWrapper message) {
        messages.offer(message);
    }


    class MessageQueueWorker extends Thread {

        private BlockingQueue<NetworkMessageWrapper> queue;
        private volatile boolean isRunning;


        public MessageQueueWorker(BlockingQueue<NetworkMessageWrapper> queue_) {
            this.isRunning = true;
            this.queue = queue_;
        }

        @Override
        public void run() {

            while (isRunning) {

                try {

                    NetworkMessageWrapper message = queue.take();

                    if (message.getChannel() == null || !message.getChannel().isActive()) {
                        logger.info("Channel connection not active!! Dropping message " + message);
                    } else {
                        logger.debug(message.getMessage().toString());
                        send(message);
                    }


                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                    Thread.currentThread().interrupt();
                }

            }
        }

        private void send(NetworkMessageWrapper message) {
            Channel ch = message.getChannel();

            if (ch != null) {
                NetworkMessage networkMessageNetwork = message.getMessage();
                ch.writeAndFlush(networkMessageNetwork, ch.voidPromise());

                logger.info(">> " + networkMessageNetwork);
            }
        }
    }
}
