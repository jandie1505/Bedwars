package net.jandie1505.bedwars.game.game.timeactions.actions;

import net.jandie1505.bedwars.game.game.Game;
import net.jandie1505.bedwars.game.game.timeactions.base.TimeAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.time.Duration;

public class DestroyBedsAction extends TimeAction {
    private final boolean disableBeds;

    public DestroyBedsAction(@NotNull Game game, @NotNull String id, int time, boolean disableBeds) {
        super(game, id, time);
        this.disableBeds = disableBeds;
    }

    // ----- DATA -----

    @Override
    protected void onRun() {
        this.getGame().getTeams().forEach(team -> {

            // Send message
            if (team.getAvailableBedsCount() > 0) {
                team.getOnlineMembers().forEach(member -> {

                    member.showTitle(Title.title(
                            Component.text("BEDS DESTROYED!", NamedTextColor.RED),
                            Component.text("All beds have been destroyed!", NamedTextColor.RED),
                            Title.Times.times(Duration.ofMillis(25), Duration.ofSeconds(3), Duration.ofMillis(25))
                    ));

                    member.playSound(member.getLocation().clone(), Sound.ENTITY_WITHER_DEATH, 1, 1);
                });
            } else {
                team.getOnlineMembers().forEach(member -> member.playSound(member.getLocation().clone(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1));
            }

            // Destroy beds
            team.destroyBeds();
            if (this.disableBeds) team.setBedDisabled(true);

        });
    }

    // ----- MESSAGES -----

    @Override
    public @NotNull Component getChatMessage() {
        Component message = Component.empty().appendNewline()
                .append(Component.text("BEDS DESTROYED!", NamedTextColor.RED, TextDecoration.BOLD)).appendNewline()
                .append(Component.text("All beds have been destroyed!", NamedTextColor.RED)).appendNewline();

        if (this.disableBeds) {
            message = message.append(Component.text("They cannot be replaced.", NamedTextColor.RED)).appendNewline();
        }

        return message;
    }

    @Override
    public Component getScoreboardText() {
        return Component.text("Beds gone");
    }

    // ----- OTHER -----

    public boolean isDisableBeds() {
        return this.disableBeds;
    }

    // ----- DATA -----

    public record Data(@NotNull String id, int time, boolean disableBeds) implements TimeAction.Data {

        @Override
        public @NotNull String type() {
            return "destroy_beds";
        }

        @Override
        public @NotNull TimeAction build(@NotNull Game game) {
            return new DestroyBedsAction(game, this.id(), this.time(), this.disableBeds());
        }

        @Override
        public @NotNull JSONObject toJSON() {
            JSONObject json = new JSONObject();

            json.put("id", this.id());
            json.put("type", this.type());
            json.put("time", this.time());
            json.put("disable_beds", this.disableBeds());

            return json;
        }

        public static Data fromJSON(@NotNull JSONObject json) {
            return new Data(json.getString("id"), json.getInt("time"), json.getBoolean("disable_beds"));
        }

    }

}
