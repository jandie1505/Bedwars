package net.jandie1505.bedwars.game.game.team.storage;

import java.util.Objects;

public class ResourceStorage {
    private int ironAmount;
    private int goldAmount;
    private int diamondAmount;
    private int emeraldAmount;

    public ResourceStorage() {
        this.ironAmount = 0;
        this.goldAmount = 0;
        this.diamondAmount = 0;
        this.emeraldAmount = 0;
    }

    // ----- GET/SET -----

    public int ironAmount() {
        return ironAmount;
    }

    public void setIronAmount(int ironAmount) {
        this.ironAmount = ironAmount;
    }

    public int goldAmount() {
        return goldAmount;
    }

    public void setGoldAmount(int goldAmount) {
        this.goldAmount = goldAmount;
    }

    public int diamondAmount() {
        return diamondAmount;
    }

    public void setDiamondAmount(int diamondAmount) {
        this.diamondAmount = diamondAmount;
    }

    public int emeraldAmount() {
        return emeraldAmount;
    }

    public void setEmeraldAmount(int emeraldAmount) {
        this.emeraldAmount = emeraldAmount;
    }

    // ----- COMPARE -----

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResourceStorage rs)) return false;
        return rs.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.ironAmount, this.goldAmount, this.diamondAmount, this.emeraldAmount);
    }

}
