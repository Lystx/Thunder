package de.lystx.discordbot.elements;

public interface Acceptor<F, S> {

    void submit(F f, S s);
}
