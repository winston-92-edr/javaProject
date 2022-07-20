package com.mynet.proxyserver.network;

import com.mynet.shared.network.NetworkMessageDecoder;
import com.mynet.shared.network.NetworkMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.node.NodeController;

import java.net.InetSocketAddress;

public class TCPProxyServer extends Thread {
    Logger logger = LoggerFactory.getLogger(TCPProxyServer.class);

    static final int MAX_FRAME_LENGTH = 8 * 8 * 1024;
    static final int EVENT_LOOP_THREADS = 4;
    static final int EXECUTOR_THREADS = 1;

    private String host;
    private int port;
    private NodeController nodeController;

    public TCPProxyServer(NodeController nodeController, String host, int port){
        this.host = host;
        this.port = port;
        this.nodeController = nodeController;
    }

    public void startServer(){
        start();
    }

    private void startWithEpoll(){
        EventLoopGroup bossGroup = new EpollEventLoopGroup(EVENT_LOOP_THREADS);
        EventLoopGroup workerGroup = new EpollEventLoopGroup();
        final EventExecutorGroup executor = new DefaultEventExecutorGroup(EXECUTOR_THREADS);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 8192)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            p.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0, 4));
                            p.addLast(new NetworkMessageDecoder());

                            p.addLast(new IdleStateHandler(60, 30, 0));
                            p.addLast(executor, new TCPHandler(nodeController));

                            //encoders
                            p.addLast(new LengthFieldPrepender(4));
                            p.addLast(new NetworkMessageEncoder());
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(new InetSocketAddress(this.host, this.port)).sync();

            logger.info("(Epoll) TCP TCPProxyServer started at " + host + ":" + port);

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(TCPProxyServer.class.getName(), e);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void startWithKqueue(){
        EventLoopGroup bossGroup = new KQueueEventLoopGroup(EVENT_LOOP_THREADS);
        EventLoopGroup workerGroup = new KQueueEventLoopGroup();
        final EventExecutorGroup executor = new DefaultEventExecutorGroup(EXECUTOR_THREADS);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(KQueueServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 8192)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            p.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0, 4));
                            p.addLast(new NetworkMessageDecoder());

                            p.addLast(new IdleStateHandler(60, 30, 0));
                            p.addLast(executor, new TCPHandler(nodeController));

                            //encoders
                            p.addLast(new LengthFieldPrepender(4));
                            p.addLast(new NetworkMessageEncoder());
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(new InetSocketAddress(this.host, this.port)).sync();

            logger.info("(Kqueue) TCP ServerToServerTCPServer started at " + host + ":" + port);

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(TCPProxyServer.class.getName(), e);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void startWithNIO(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(EXECUTOR_THREADS);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final EventExecutorGroup executor = new DefaultEventExecutorGroup(EVENT_LOOP_THREADS);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 8192)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            p.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0, 4));
                            p.addLast(new NetworkMessageDecoder());

                            p.addLast(new IdleStateHandler(60, 30, 0));
                            p.addLast(executor, new TCPHandler(nodeController));

                            //encoders
                            p.addLast(new LengthFieldPrepender(4));
                            p.addLast(new NetworkMessageEncoder());
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(new InetSocketAddress(this.host, this.port)).sync();

            logger.info("(NIO) TCP ServerToServerTCPServer started at " + host + ":" + port);

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(TCPProxyServer.class.getName(), e);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
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
}
