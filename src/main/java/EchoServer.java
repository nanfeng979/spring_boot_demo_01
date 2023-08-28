import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EchoServer {

    private final int port;
    private final PlayerManager playerManager;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public EchoServer(int port) {
        this.port = port;
        this.playerManager = new PlayerManager();
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        scheduler.scheduleAtFixedRate(this::checkPlayerStatus, 0, 2, TimeUnit.SECONDS);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler(playerManager));
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("EchoServer started and listening on port " + port);

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    private void checkPlayerStatus() {
        int playerCount = playerManager.getAllPlayers().size();
        if (playerCount == 0) {
            System.out.println("No players.");
        } else {
            System.out.println("Current player status:");
            for (Player player : playerManager.getAllPlayers()) {
                System.out.println("Player: " + player.getPlayerName() + ", Ready: " + player.isReady());
            }
        }
    }

    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            System.err.println("Usage: " + EchoServer.class.getSimpleName() + " <port>");
//            return;
//        }

        int port = 12345;
        new EchoServer(port).start();
    }
}
