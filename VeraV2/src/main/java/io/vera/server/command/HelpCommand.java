package io.vera.server.command;

import io.vera.command.Command;
import io.vera.command.CommandDispatcher;
import io.vera.command.CommandListener;
import io.vera.command.CommandSource;
import io.vera.command.annotation.MaxCount;
import io.vera.command.annotation.PermissionRequired;
import io.vera.server.VeraServer;
import io.vera.ui.chat.ChatColor;
import io.vera.ui.chat.ChatComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HelpCommand implements CommandListener {
  private static final int PAGE_SIZE = 5;
  
  @Command(name = "help", aliases = {"?"}, help = "/help [command] [page]", desc = "Displays a help message, or looks for one if a command is provided")
  @PermissionRequired({"minecraft.help"})
  public void help(CommandSource source, String[] args, @MaxCount(2) String... params) {
    if (params.length == 1) {
      String command = params[0];
      try {
        int page = Integer.parseInt(command);
        help(page, source);
      } catch (NumberFormatException e) {
        if (command.equalsIgnoreCase("aliases")) {
          aliases(1, source);
        } else if (command.equalsIgnoreCase("trident")) {
          plugin("Vera", 1, source);
        } else if (command.equalsIgnoreCase("minecraft")) {
          plugin("Minecraft", 1, source);
        } else {
          search(command, 1, source);
        } 
      } 
    } else if (params.length == 2) {
      String command = params[0];
      try {
        int page = Integer.parseInt(params[1]);
        if (command.equalsIgnoreCase("aliases")) {
          aliases(page, source);
        } else if (command.equalsIgnoreCase("trident")) {
          plugin("Vera", page, source);
        } else if (command.equalsIgnoreCase("minecraft")) {
          plugin("Minecraft", page, source);
        } else {
          search(command, page, source);
        } 
      } catch (NumberFormatException x) {
        source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).setText("No help for " + params[0] + ' ' + params[1]));
      } 
    } else {
      help(1, source);
    } 
  }
  
  private void help(int page, CommandSource source) {
    int max = page * 5;
    int ceil = (int)Math.ceil(VeraServer.getInstance().getCommandHandler().getCmdCount() / 5.0D);
    if (max <= 0 || page > ceil) {
      source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
          .setText("Help page must be between 0 and " + ceil));
      return;
    } 
    int it = 0;
    List<String> help = new ArrayList<>();
    Set<CommandDispatcher> dispatcherSet = Collections.newSetFromMap(new IdentityHashMap<>());
    dispatcherSet.addAll(VeraServer.getInstance().getCommandHandler().getDispatchers().values());
    for (CommandDispatcher dispatcher : dispatcherSet) {
      if (it == max)
        break; 
      if (it >= max - 5) {
        String s = ChatColor.GOLD.toString() + '/' + dispatcher.getCommand().name() + ": " + ChatColor.WHITE + dispatcher.getCommand().desc();
        if (s.length() > 64)
          s = s.substring(0, 64) + "..."; 
        help.add(s);
      } 
      it++;
    } 
    source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("-------- ")
        .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Help: Index (" + page + '/' + ceil + ')'))
        .addExtra(ChatComponent.create().setColor(ChatColor.YELLOW).setText(" ----------------------")));
    source.sendMessage(ChatComponent.create().setColor(ChatColor.GRAY).setText("Use /help [n] to get page n of help"));
    source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Aliases: ")
        .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Lists command aliases")));
    source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Vera: ")
        .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("All commands for Vera")));
    source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Minecraft: ")
        .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("All commands for Minecraft")));
    for (String s : help)
      source.sendMessage(ChatComponent.fromFormat(s)); 
  }
  
  private void search(String search, int page, CommandSource source) {
    int max = page * 5;
    if (max <= 0) {
      source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
          .setText("Help page must be greater than 0"));
      return;
    } 
    int it = 0;
    List<String> help = new ArrayList<>();
    Map<String, CommandDispatcher> dispatchers = VeraServer.getInstance().getCommandHandler().getDispatchers();
    CommandDispatcher dispatcher = dispatchers.get(search);
    if (dispatcher != null) {
      source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("-------- ")
          .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Help: /" + search))
          .addExtra(ChatComponent.create().setColor(ChatColor.YELLOW).setText(" ----------------------")));
      if (!dispatcher.getCommand().name().equalsIgnoreCase(search))
        source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("Alias for ")
            .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText('/' + dispatcher.getCommand().name()))); 
      source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Description: ")
          .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText(dispatcher.getCommand().desc())));
      source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Usage: ")
          .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText(dispatcher.getCommand().help())));
      if ((dispatcher.getCommand().aliases()).length > 0) {
        StringBuilder builder = new StringBuilder();
        for (String alias : dispatcher.getCommand().aliases())
          builder.append(alias).append(", "); 
        String text = builder.toString();
        source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Aliases: ")
            .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText(text.substring(0, text.length() - 2))));
      } 
    } else {
      for (Map.Entry<String, CommandDispatcher> entry : dispatchers.entrySet()) {
        if (isSimilar(search, entry.getKey())) {
          if (it >= max) {
            it++;
            continue;
          } 
          if (it >= max - 5)
            if (dispatcher.getCommand().name().equalsIgnoreCase(search)) {
              String s = ChatColor.GOLD.toString() + '/' + (String)entry.getKey() + ": " + ChatColor.WHITE + ((CommandDispatcher)entry.getValue()).getCommand().desc();
              if (s.length() > 64)
                s = s.substring(0, 64) + "..."; 
              help.add(s);
            } else {
              String s = ChatColor.GOLD.toString() + '/' + (String)entry.getKey() + ": " + ChatColor.YELLOW + "Alias for " + ChatColor.WHITE + '/' + ((CommandDispatcher)entry.getValue()).getCommand().name();
              if (s.length() > 66)
                s = s.substring(0, 66) + "..."; 
              help.add(s);
            }  
          it++;
        } 
      } 
      int ceil = Math.max(1, (int)Math.ceil(it / 5.0D));
      if (page > ceil) {
        source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
            .setText("Help page must be between 0 and " + ceil));
        return;
      } 
      source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("-------- ")
          .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Help: Search (" + page + '/' + ceil + ')'))
          
          .addExtra(ChatComponent.create().setColor(ChatColor.YELLOW).setText(" ----------------------")));
      source.sendMessage(ChatComponent.create().setColor(ChatColor.GRAY).setText("Search for: " + search));
      for (String s : help)
        source.sendMessage(ChatComponent.fromFormat(s)); 
    } 
  }
  
  private boolean isSimilar(String one, String two) {
    int times = 0;
    for (int i = 0; i <= one.length(); i++) {
      if (two.contains(one.substring(0, i)))
        times++; 
    } 
    return (times / (one.length() + 1.0D) > 0.8D);
  }
  
  private void aliases(int page, CommandSource source) {
    int max = page * 5;
    int ceil = (int)Math.ceil((VeraServer.getInstance().getCommandHandler().getDispatchers().size() - 
        VeraServer.getInstance().getCommandHandler().getCmdCount()) / 5.0D);
    if (max <= 0 || page > ceil) {
      source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
          .setText("Help page must be between 0 and " + ceil));
      return;
    } 
    int it = 0;
    List<String> help = new ArrayList<>();
    for (Map.Entry<String, CommandDispatcher> entry : (Iterable<Map.Entry<String, CommandDispatcher>>)VeraServer.getInstance().getCommandHandler().getDispatchers().entrySet()) {
      CommandDispatcher dispatcher = entry.getValue();
      if (!dispatcher.getCommand().name().equalsIgnoreCase(entry.getKey())) {
        if (it == max)
          break; 
        if (it >= max - 5) {
          String s = ChatColor.GOLD.toString() + '/' + (String)entry.getKey() + ": " + ChatColor.YELLOW + "Alias for " + ChatColor.WHITE + '/' + dispatcher.getCommand().name();
          if (s.length() > 66)
            s = s.substring(0, 66) + "..."; 
          help.add(s);
        } 
        it++;
      } 
    } 
    source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("-------- ")
        .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Help: Aliases (" + page + '/' + ceil + ')'))
        .addExtra(ChatComponent.create().setColor(ChatColor.YELLOW).setText(" ----------------------")));
    for (String s : help)
      source.sendMessage(ChatComponent.fromFormat(s)); 
  }
  
  private void plugin(String plugin, int page, CommandSource source) {
    int max = page * 5;
    if (max <= 0) {
      source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
          .setText("Help page must be greater than 0"));
      return;
    } 
    int it = 0;
    List<String> help = new ArrayList<>();
    for (Map.Entry<String, CommandDispatcher> entry : (Iterable<Map.Entry<String, CommandDispatcher>>)VeraServer.getInstance().getCommandHandler().getDispatchers().entrySet()) {
      CommandDispatcher dispatcher = entry.getValue();
      if (dispatcher.getPlugin().equalsIgnoreCase(plugin) && !dispatcher.getCommand().name().equalsIgnoreCase(entry.getKey())) {
        if (it >= max) {
          it++;
          continue;
        } 
        if (it >= max - 5) {
          String s = ChatColor.GOLD.toString() + '/' + (String)entry.getKey() + ": " + ChatColor.WHITE + dispatcher.getCommand().desc();
          if (s.length() > 64)
            s = s.substring(0, 64) + "..."; 
          help.add(s);
        } 
        it++;
      } 
    } 
    int ceil = (int)Math.max(1.0D, Math.ceil(it / 5.0D));
    if (page > ceil) {
      source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
          .setText("Help page must be between 0 and " + ceil));
      return;
    } 
    source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("-------- ")
        .addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Help: " + plugin + " (" + page + '/' + ceil + ')'))
        
        .addExtra(ChatComponent.create().setColor(ChatColor.YELLOW).setText(" ----------------------")));
    source.sendMessage(ChatComponent.create().setColor(ChatColor.GRAY).setText("Below is a list of all " + plugin + " commands:"));
    for (String s : help)
      source.sendMessage(ChatComponent.fromFormat(s)); 
  }
}
