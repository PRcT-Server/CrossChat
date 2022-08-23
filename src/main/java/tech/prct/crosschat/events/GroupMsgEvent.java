package tech.prct.crosschat.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.plugin.Event;
import tech.prct.crosschat.CrossChat;

public class GroupMsgEvent extends Event {

    public GroupMsgEvent(JsonObject json, CrossChat main){

        Integer groupID = json.get("group_id").getAsInt();
        if(!main.groupMap.containsKey(groupID)){
            return;
        }

        JsonElement message = main.config.getBoolean("simple_msg") ? json.get("raw_message"):json.get("message");
        String msg = message.getAsString().replaceAll("\\[CQ:.*?,file=.*?]", "");
        if(msg.isEmpty()){
            return;
        }

        JsonObject sender = json.getAsJsonObject("sender");
        String user = sender.get("card") == null ?
                sender.get("nickname").getAsString() : sender.get("card").getAsString();

        String finalMessage = String.format(main.config.getString("chat"), user, main.groupMap.get(groupID), msg);
        main.sendServerMessage(finalMessage);
    }

}
