package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.ColorEnum;

public class LoggedEvent implements ServerEvent {
    private final String name;
    private final String color;

    public LoggedEvent(boolean result, String name, ColorEnum color) {
        this.name = name;
        this.color = color.toString();
    }
}
