package net.jandie1505.bedwars.old.game.timeactions.provider;

import net.jandie1505.bedwars.old.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.old.game.timeactions.base.TimeActionData;

public interface TimeActionProvider {
    TimeAction createTimeAction(TimeActionData data);
}
