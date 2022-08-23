package tech.prct.crosschat;

import com.google.gson.*;
import net.md_5.bungee.api.ProxyServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import tech.prct.crosschat.events.GroupMsgEvent;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.logging.Logger;

public class Server extends WebSocketServer {
    Logger logger;
    ProxyServer proxyServer;
    CrossChat main;
    public Server(int port, CrossChat main){
        super(new InetSocketAddress("localhost", port));
        this.logger = main.getLogger();
        this.proxyServer = main.getProxy();
        this.main = main;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("connection created");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("connection closed");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        messageHandle(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {
        logger.info("open websocket server successfully");
    }

    public void messageHandle(String msg){
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(msg).getAsJsonObject();

        if(!jsonObject.has("post_type")){
            return;
        }

        String type = jsonObject.get("post_type").getAsString();
        if(type.isEmpty()){
            return;
        }
        String sub_type = jsonObject.get(type + "_type").getAsString();
        if (Objects.equals(type, "message") && Objects.equals(sub_type, "group")) {
            proxyServer.getPluginManager().callEvent(new GroupMsgEvent(jsonObject, main));
        }

    }

    public void sendGroupMsg(String msg){

        for (Integer groupID: main.groupMap.keySet()){
            JsonObject params = new JsonObject();
            params.addProperty("group_id", groupID);
            params.addProperty("message", msg);
            broadcast(new S2CWSPacket("send_group_msg", params).getString());
        }

    }
}

class S2CWSPacket{
    private final String action;
    private final JsonObject params;

    public S2CWSPacket(String action, JsonObject json){
        this.action = action;
        this.params = json;
    }

    public String getString(){
        JsonObject data = new JsonObject();
        data.addProperty("action", action);
        data.add("params", params);
        return data.toString();
    }
}

