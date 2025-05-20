package net.jandie1505.bedwars.game.game.timeactions.provider;

import net.jandie1505.bedwars.game.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeActionData;

public interface TimeActionProvider {
    TimeAction createTimeAction(TimeActionData data);
}
