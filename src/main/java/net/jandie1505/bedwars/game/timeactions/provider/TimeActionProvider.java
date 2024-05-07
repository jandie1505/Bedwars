package net.jandie1505.bedwars.game.timeactions.provider;

import net.jandie1505.bedwars.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.game.timeactions.base.TimeActionData;

public interface TimeActionProvider {
    TimeAction createTimeAction(TimeActionData data);
}
