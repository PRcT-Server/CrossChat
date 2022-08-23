package tech.prct.crosschat;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import tech.prct.crosschat.events.EventListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public final class CrossChat extends Plugin {
    public Configuration config;
    public Server server;
    public Map<Integer, String> groupMap;

    @Override
    public void onEnable() {
        getLogger().info("Start init");
        loadConfig();
        getGroups();
        getProxy().getPluginManager().registerListener(this, new EventListener(this));
        getProxy().getPluginManager().registerCommand(this, new Command(this));
        startServer();
    }

    @Override
    public void onDisable() {
        try {
            server.stop(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig(){
        File configFile = new File(getDataFolder(), "config.yml");

        if(!getDataFolder().exists()){
            getLogger().info("Created config folder" + getDataFolder().mkdir());
        }

        if(!configFile.exists()){
            try (InputStream inputStream = getResourceAsStream("config.yml")){
                Files.copy(inputStream, configFile.toPath());
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        try {
            config = ConfigurationProvider.getProvider(
                    YamlConfiguration.class
            ).load(new File(getDataFolder(), "config.yml"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void getGroups(){
        Configuration groups = config.getSection("groups");
        Map<Integer, String> groupMap = new HashMap<>();
        for(String groupName: groups.getKeys()){
            Integer groupID = groups.getInt(groupName);
            groupMap.put(groupID, groupName);
        }
        this.groupMap = groupMap;
    }
    public void startServer(){
        server = new Server((Integer) config.get("port"), this);
        Thread thread = new Thread(server);
        thread.start();
    }

    public void sendServerMessage(String message){
        for (ProxiedPlayer player: getProxy().getPlayers()){
            player.sendMessage(new TextComponent(message));
        }
    }
}
