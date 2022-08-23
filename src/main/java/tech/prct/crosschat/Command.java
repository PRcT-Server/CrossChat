package tech.prct.crosschat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Objects;

public class Command extends net.md_5.bungee.api.plugin.Command {

    CrossChat main;

    public Command(CrossChat main){
        super("crosschat");
        this.main = main;
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 1 && Objects.equals(args[0], "reload")){
            main.loadConfig();
            main.getGroups();
            sender.sendMessage(new TextComponent("Reload config success!"));
        }
    }
}
