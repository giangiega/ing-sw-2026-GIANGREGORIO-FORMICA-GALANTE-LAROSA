package it.polimi.ingsw;

public class BuildingBonusForCharacterType extends BuildingEffect{
    private int PP;
    private CharacterEnum character;

    public int getPP(){
        return this.PP;
    }
    public CharacterEnum getCharacter(){
        return this.character;
    }

    @Override
    public void applyEndGame(Player p, Board b){

    }
}

