package com.mynet.shared.network;

import com.mynet.shared.config.ServerConfiguration;
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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ServerToServerTCPServer extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ServerToServerTCPServer.class);

    private String host;
    private int port;
    private ServerToServer.ServerType serverType;

    private ServerToServerMessageProcessor serverToServerMessageProcessor;
    static final int EVENT_LOOP_THREADS = 4;
    static final int EXECUTOR_THREADS = 1;

    public ServerToServerTCPServer(ServerToServer.ServerType serverType, ServerToServerMessageProcessor serverToServerMessageProcessor, String host, int port) {
        this.serverType = serverType;
        this.host = host;
        this.port = port;
        this.serverToServerMessageProcessor = serverToServerMessageProcessor;
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

    private void startWithEpoll() {
        // Configure the server.
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
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024 * 8, 0, 4));
                            ch.pipeline().addLast(new LocalNetworkMessageDecoder());
                            p.addLast(executor, new ServerToServerTCPChannelHandler(serverToServerMessageProcessor, serverType));

                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new LocalNetworkMessageEncoder());

                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(new InetSocketAddress(this.host, this.port)).sync();
            logger.debug("(Epoll) ServerToServerTCPServer Started at " + this.host + ":" + this.port);

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void startWithKqueue() {
        // Configure the server.
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
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024 * 8, 0, 4));
                            ch.pipeline().addLast(new LocalNetworkMessageDecoder());
                            p.addLast(executor, new ServerToServerTCPChannelHandler(serverToServerMessageProcessor, serverType));

                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new LocalNetworkMessageEncoder());

                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(new InetSocketAddress(this.host, this.port)).sync();
            logger.debug("(Kqueue) ServerToServerTCPServer Started at " + this.host + ":" + this.port);

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void startWithNIO() {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(EVENT_LOOP_THREADS);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final EventExecutorGroup executor = new DefaultEventExecutorGroup(EXECUTOR_THREADS);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 8192)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024 * 8, 0, 4));
                            ch.pipeline().addLast(new LocalNetworkMessageDecoder());
                            p.addLast(executor, new ServerToServerTCPChannelHandler(serverToServerMessageProcessor, ServerToServer.ServerType.GAME));

                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new LocalNetworkMessageEncoder());

                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(new InetSocketAddress(this.host, this.port)).sync();
            logger.info("(NIO) ServerToServerTCPServer Started at " + this.host + ":" + this.port);

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
