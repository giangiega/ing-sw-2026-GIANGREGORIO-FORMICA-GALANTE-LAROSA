package it.polimi.ingsw;

public abstract class Card {
    private EraEnum era;

    /*Ric: ho aperto i constructor in Card e nelle sottoclassi per evitare errori temporanei.
    * Il constructor di Card resta così perchè ha solo un attributo, ma quelli delle classi evento
    * potrebbero essere incompleti perchè hanno altri attributi oltre era.*/
    protected Card(EraEnum era) {
        this.era = era;
    }

    public EraEnum getEra() {
        return era;
    }
