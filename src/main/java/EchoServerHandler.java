import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    private final PlayerManager playerManager;

    public EchoServerHandler(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    // 当客户端向服务端发送消息时触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 将接收到的消息转换为ByteBuf
        ByteBuf byteBuf = (ByteBuf) msg;

        try {
            // 读取ByteBuf中的数据并转换为字符串
            String receivedData = byteBuf.toString(io.netty.util.CharsetUtil.UTF_8);
            System.out.println("Received data from client: " + receivedData);

            // 设置玩家准备状态
            Player player = playerManager.getPlayer(ctx.channel().id());
            player.setReady(true); // Set player's ready state to true

            // 广播消息
            playerManager.broadcastMessage("The game has started!"); // Broadcast a message to all players

            // 将接收到的消息写回客户端
//            ctx.write(receivedData);
            ctx.flush();
        } finally {
            // 释放ByteBuf的引用计数
            byteBuf.release();
        }
    }

    // 当客户端连接断开时触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client connected: " + ctx.channel().remoteAddress());

        // 创建一个新的Player
        Player player = new Player(ctx.channel()); // Create a new player instance
        playerManager.addPlayer(ctx.channel().id(), player); // Add player to PlayerManager
    }

    // 当客户端断开连接时触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Client disconnected: " + ctx.channel().remoteAddress());

        // 从PlayerManager中移除该玩家
        playerManager.removePlayer(ctx.channel().id()); // Remove player from PlayerManager
    }

    // 当出现异常时触发
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
