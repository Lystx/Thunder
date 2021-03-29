package io.betterbukkit.provider.command;


import java.util.List;

public interface TabCompletable {

    List<String> onTabComplete(String[] args);

}