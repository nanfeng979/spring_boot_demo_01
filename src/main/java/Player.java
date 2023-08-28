import io.netty.channel.Channel;

public class Player {
    private Channel channel; // 客户端连接的通道
    private String playerName; // 玩家昵称
    private boolean ready; // 玩家准备状态
    private int score; // 玩家分数或游戏进度

    public Player(Channel channel) {
        this.channel = channel;
        this.ready = false;
        this.score = 0;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void sendMessage(String message) {
        channel.writeAndFlush(message);
    }
}
