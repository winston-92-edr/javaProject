package com.mynet.shared.node;

import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.network.LocalNetworkMessageDecoder;
import com.mynet.shared.network.LocalNetworkMessageEncoder;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.user.UserController;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeConnection extends Thread {
    final Logger logger = LoggerFactory.getLogger(NodeConnection.class);

    private Node node;
    private Channel channel;
    private UserController userController;
    private NodeController nodeController;
    private NodeData serverNode;
    static final int EVENT_LOOP_THREADS = 2;
    static final int EXECUTOR_THREADS = 1;

    public NodeConnection(Node node, UserController userController, NodeController nodeController, NodeData serverNode) {
        this.node = node;
        this.userController = userController;
        this.nodeController = nodeController;
        this.serverNode = serverNode;
    }

    public void connect() {
        start();
    }

    private void startWithEpoll(){
        EventLoopGroup workerGroup = new EpollEventLoopGroup(EVENT_LOOP_THREADS);
        final EventExecutorGroup executor = new DefaultEventExecutorGroup(EXECUTOR_THREADS);
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(EpollSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {

                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024 * 8, 0, 4));
                    ch.pipeline().addLast(new LocalNetworkMessageDecoder());
                    ch.pipeline().addLast(executor, new NodeConnectionHandler(node, nodeController, serverNode));

                    ch.pipeline().addLast(new LengthFieldPrepender(4));
                    ch.pipeline().addLast(new LocalNetworkMessageEncoder());

                }
            });

            // Start the client.
            ChannelFuture f = b.connect(node.getHost(), node.getPort()).sync(); // (5)

            this.channel = f.channel();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            logger.error(NodeConnection.class.getName(), ex);
            Thread.currentThread().interrupt();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }


    private void startWithKqueue(){
        EventLoopGroup workerGroup = new KQueueEventLoopGroup(EVENT_LOOP_THREADS);
        final EventExecutorGroup executor = new DefaultEventExecutorGroup(EXECUTOR_THREADS);
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(KQueueSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {

                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024 * 8, 0, 4));
                    ch.pipeline().addLast(new LocalNetworkMessageDecoder());
                    ch.pipeline().addLast(executor, new NodeConnectionHandler(node, nodeController, serverNode));

                    ch.pipeline().addLast(new LengthFieldPrepender(4));
                    ch.pipeline().addLast(new LocalNetworkMessageEncoder());

                }
            });

            // Start the client.
            ChannelFuture f = b.connect(node.getHost(), node.getPort()).sync(); // (5)

            this.channel = f.channel();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            logger.error(NodeConnection.class.getName(), ex);
            Thread.currentThread().interrupt();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private void startWithNIO(){
        EventLoopGroup workerGroup = new NioEventLoopGroup(EVENT_LOOP_THREADS);
        final EventExecutorGroup executor = new DefaultEventExecutorGroup(EXECUTOR_THREADS);

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {

                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024 * 8, 0, 4));
                    ch.pipeline().addLast(new LocalNetworkMessageDecoder());
                    ch.pipeline().addLast(executor, new NodeConnectionHandler(node, nodeController, serverNode));

                    ch.pipeline().addLast(new LengthFieldPrepender(4));
                    ch.pipeline().addLast(new LocalNetworkMessageEncoder());

                }
            });

            // Start the client.
            ChannelFuture f = b.connect(node.getHost(), node.getPort()).sync(); // (5)

            this.channel = f.channel();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            logger.error(NodeConnection.class.getName(), ex);
            Thread.currentThread().interrupt();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void run() {
        String poolType = ServerConfiguration.get("socketPoolType");
        poolType = poolType == null ? "nio" : poolType.trim().toLowerCase();

        switch (poolType){
            case "kqueue":
                startWithKqueue();
                break;
            case "epoll":
                startWithEpoll();
                break;
            case "nio":
                startWithNIO();
                break;

            default:
                System.out.println("Unknown socketPoolType, quitting !!!!!!!");
                System.exit(1);
                break;
        }
    }

    public void close(){
        if(isConnected()){
            try {
                this.channel.close().await();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                logger.error(NodeConnection.class.getName(), ex);
            }
            logger.info(node + " disconnected!");
        }

    }

    public Channel getChannel(){
        return channel;
    }

    void send(NetworkMessage request) {
        if (channel != null) {
            channel.writeAndFlush(request);
        }

    }

    void sendString(String string) {
        if (channel != null) {
            channel.writeAndFlush(string);
        }
    }

    boolean isConnected() {
        return channel != null && channel.isOpen();
    }

}
