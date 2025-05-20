package net.jandie1505.bedwars.game.game.timeactions.provider;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.timeactions.actions.DestroyBedsAction;
import net.jandie1505.bedwars.game.game.timeactions.actions.EndgameWitherTimeAction;
import net.jandie1505.bedwars.game.game.timeactions.actions.GeneratorUpgradeAction;
import net.jandie1505.bedwars.game.game.timeactions.actions.WorldborderChangeTimeAction;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeAction;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeActionData;

import java.util.HashMap;
import java.util.Map;

public class TimeActionCreator {
    private final Game game;
    private final Map<String, TimeActionProvider> providers;

    public TimeActionCreator(Game game) {
        this.game = game;
        this.providers = new HashMap<>();

        // DEFAULT TIME ACTIONS

        this.providers.put("GENERATOR_UPGRADE", data -> new GeneratorUpgradeAction(this.game, data));
        this.providers.put("DESTROY_BEDS", data -> new DestroyBedsAction(this.game, data));
        this.providers.put("ENDGAME_WITHER", data -> new EndgameWitherTimeAction(this.game, data));
        this.providers.put("WORLDBORDER_CHANGE", data -> new WorldborderChangeTimeAction(this.game, data));
    }

    public TimeAction createTimeAction(TimeActionData data) {
        TimeActionProvider provider = this.providers.get(data.type());
        if(provider == null) return null;
        return provider.createTimeAction(data);
    }

    public Game getGame() {
        return this.game;
    }

    public Map<String, TimeActionProvider> getProviders() {
        return Map.copyOf(this.providers);
    }

}
