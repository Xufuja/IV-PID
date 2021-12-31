package org.xufuja.pocketmonsters;

public class PocketMonsterData {
    private int hp;
    private int attack;
    private int defense;
    private int specialAttack;
    private int specialDefense;
    private int speed;
    private int nature;
    private int ability;
    private int hiddenPowerType;
    private int hiddenPowerPower;
    private int idXorSid;

    public PocketMonsterData(int hp, int attack, int defense, int specialAttack, int specialDefense, int speed) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
        this.nature = -1;
        this.ability = 2;
        this.hiddenPowerType = -1;
        this.hiddenPowerPower = -1;
        this.idXorSid = 1;
    }

    public PocketMonsterData() {
        this(0, 0, 0, 0, 0, 0);
    }

    public void setNature(int nature) {
        this.nature = nature;
    }

    public void setAbility(int ability) {
        this.ability = ability;
    }

    public void setHiddenPowerType(int hiddenPowerType) {
        this.hiddenPowerType = hiddenPowerType;
    }

    public void setHiddenPowerPower(int hiddenPowerPower) {
        this.hiddenPowerPower = hiddenPowerPower;
    }

    public void setIdXorSid(int idXorSid) {
        this.idXorSid = idXorSid;
    }

    public int getNature() {
        return nature;
    }

    public int getAbility() {
        return ability;
    }

    public int getHiddenPowerType() {
        return hiddenPowerType;
    }

    public int getHiddenPowerPower() {
        return hiddenPowerPower;
    }

    public int getIdXorSid() {
        return idXorSid;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpecialAttack() {
        return specialAttack;
    }

    public int getSpecialDefense() {
        return specialDefense;
    }

    public int getSpeed() {
        return speed;
    }
}
