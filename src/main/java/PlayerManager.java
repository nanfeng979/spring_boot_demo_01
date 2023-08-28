import io.netty.channel.ChannelId;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class PlayerManager {
    private Map<ChannelId, Player> players = new HashMap<>();

    public void addPlayer(ChannelId channelId, Player player) {
        players.put(channelId, player);
    }

    public void removePlayer(ChannelId channelId) {
        players.remove(channelId);
    }

    public Player getPlayer(ChannelId channelId) {
        return players.get(channelId);
    }

    public Collection<Player> getAllPlayers() {
        return players.values();
    }

    public void broadcastMessage(String message) {
        for (Player player : players.values()) {
            player.sendMessage(message);
        }
    }
}
