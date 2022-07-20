package com.mynet.proxyserver.network.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.node.NodeController;

import javax.net.ssl.SSLException;
import java.io.File;
import java.net.InetSocketAddress;

public class WebSocketProxyServer extends Thread {
    Logger logger = LoggerFactory.getLogger(WebSocketProxyServer.class);
    private String host;
    private int port;
    private NodeController nodeController;
    private File certFile;
    private File keyFile;
    private SslContext sslCtx;
    private static final String WEBSOCKET_PATH = "/web";

    public WebSocketProxyServer(NodeController nodeController, String host, int port, String certFilePath, String keyFilePath){
        this.host = host;
        this.port = port;
        this.nodeController = nodeController;

        if (certFilePath != null) {
            this.certFile = new File(certFilePath);
        }
        if (keyFilePath != null) {
            this.keyFile = new File(keyFilePath);
        }
    }

    public void startServer(){
        start();
    }

    private void startWithNIO(){

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        final EventExecutorGroup executor = new DefaultEventExecutorGroup(4);

        try {

            if (certFile != null && keyFile != null) {
                sslCtx = SslContextBuilder.forServer(this.certFile, this.keyFile).sslProvider(SslProvider.JDK).build();
            }

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            final int maxContentLength = 65536;

                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(maxContentLength));
                            p.addLast(new WebSocketServerCompressionHandler());
                            p.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, "mynet", true, maxContentLength, true));

                            p.addLast(new IdleStateHandler(60, 30, 0));
                            p.addLast(executor, new WebSocketHandler(nodeController));

                            p.addLast(new WebSocketOutHandler());

                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(new InetSocketAddress(this.host, this.port)).sync();

            logger.info("(NIO) WebSocket ServerToServerTCPServer Started at " + host + ":" + port);

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(WebSocketProxyServer.class.getName(), e);
        } catch (SSLException e) {
            logger.error(WebSocketProxyServer.class.getName(), e);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally
        {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void startWithKqueue(){

        KQueueEventLoopGroup bossGroup = new KQueueEventLoopGroup(4);
        KQueueEventLoopGroup workerGroup = new KQueueEventLoopGroup();

        final EventExecutorGroup executor = new DefaultEventExecutorGroup(4);

        try {

            if (certFile != null && keyFile != null) {
                sslCtx = SslContextBuilder.forServer(this.certFile, this.keyFile).sslProvider(SslProvider.JDK).build();
            }

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(KQueueServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            final int maxContentLength = 65536;

                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(maxContentLength));
                            p.addLast(new WebSocketServerCompressionHandler());
                            p.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, "mynet", true, maxContentLength, true));

                            p.addLast(new IdleStateHandler(60, 30, 0));
                            p.addLast(executor, new WebSocketHandler(nodeController));

                            p.addLast(new WebSocketOutHandler());

                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(new InetSocketAddress(this.host, this.port)).sync();

            logger.info("(Kqueue) WebSocket ServerToServerTCPServer Started at " + host + ":" + port);

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(WebSocketProxyServer.class.getName(), e);
        } catch (SSLException e) {
            logger.error(WebSocketProxyServer.class.getName(), e);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally
        {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void startWithEpoll(){

        EpollEventLoopGroup bossGroup = new EpollEventLoopGroup(4);
        EpollEventLoopGroup workerGroup = new EpollEventLoopGroup();


        final EventExecutorGroup executor = new DefaultEventExecutorGroup(4);

        try {

            if (certFile != null && keyFile != null) {
                sslCtx = SslContextBuilder.forServer(this.certFile, this.keyFile).sslProvider(SslProvider.JDK).build();
            }

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            final int maxContentLength = 65536;

                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(maxContentLength));
                            p.addLast(new WebSocketServerCompressionHandler());
                            p.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, "mynet", true, maxContentLength, true));

                            p.addLast(new IdleStateHandler(60, 30, 0));
                            p.addLast(executor, new WebSocketHandler(nodeController));

                            p.addLast(new WebSocketOutHandler());

                        }
                    });


            // Start the server.
            ChannelFuture f = b.bind(new InetSocketAddress(this.host, this.port)).sync();

            logger.info("(Epoll) WebSocket ServerToServerTCPServer Started at " + host + ":" + port);

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(WebSocketProxyServer.class.getName(), e);
        } catch (SSLException e) {
            logger.error(WebSocketProxyServer.class.getName(), e);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally
        {
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
