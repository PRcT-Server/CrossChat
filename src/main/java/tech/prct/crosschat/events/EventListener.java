package tech.prct.crosschat.events;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tech.prct.crosschat.CrossChat;

import java.util.regex.Pattern;

public class EventListener implements Listener {

    CrossChat main;
    public EventListener(CrossChat main){
        this.main = main;
    }

    @EventHandler
    public void onChat(ChatEvent event){
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String name = player.getName();
        String server = player.getServer().getInfo().getName();
        String message = event.getMessage();

        for(String rule: main.config.getStringList("filter")){
            if(Pattern.matches(rule, message)){
                return;
            }
        }

        String finalMessage = String.format(main.config.getString("chat")
                .replaceAll("§.?", ""), name, server, message);
        for(ServerInfo info :main.getProxy().getServers().values()){
            if(info == player.getServer().getInfo()){
                continue;
            }
            for(ProxiedPlayer players: info.getPlayers()){
                players.sendMessage(new TextComponent(finalMessage));
            }
        }

        Thread thread = new Thread(()-> main.server.sendGroupMsg(finalMessage));
        thread.start();
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event){
        String player = event.getPlayer().getName();
        String message = String.format(main.config.getString("join"), player);
        main.sendServerMessage(message);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event){
        String player = event.getPlayer().getName();
        String message = String.format(main.config.getString("left"), player);
        main.sendServerMessage(message);
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event){
        String player = event.getPlayer().getName();
        if(event.getFrom() == null){
            return;
        }
        String serverFrom = event.getFrom().getName();
        String serverTo = event.getPlayer().getServer().getInfo().getName();
        String message = String.format(main.config.getString("change"), player, serverFrom, serverTo);
        main.sendServerMessage(message);
    }
}
