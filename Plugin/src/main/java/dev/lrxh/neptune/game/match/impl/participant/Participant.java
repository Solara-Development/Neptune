package dev.lrxh.neptune.game.match.impl.participant;

import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SoundsLocale;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.Time;
import lombok.Data;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@Data
public class Participant implements IParticipant {
    public boolean dead = false;
    private UUID playerUUID;
    private String name;
    private Participant opponent;
    private DeathCause deathCause;
    private ParticipantColor color;
    private Participant lastAttacker;
    private int hits;
    private int longestCombo;
    private int combo;
    private boolean loser;
    private boolean disconnected = false;
    private boolean left = false;
    private int points = 0;
    private boolean frozen = false;
    private boolean bedBroken;
    private Time time;

    // ELO CHANGES
    private int eloChange = 0;

    // PARKOUR GAME RULE
    private Location currentCheckPoint;
    private int checkPoint = 0;

    public Participant(Player player) {
        this.playerUUID = player.getUniqueId();
        this.name = player.getName();
    }

    public void reset() {
        this.bedBroken = false;
        this.currentCheckPoint = null;
        this.checkPoint = 0;
        this.hits = 0;
        this.combo = 0;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
        playSound(SoundsLocale.getSound(SoundsLocale.PLAYER_DEATH));
    }

    public boolean setCurrentCheckPoint(Location location) {
        if (currentCheckPoint != null) {
            if (location.getBlockX() == currentCheckPoint.getBlockX() &&
                    location.getBlockY() == currentCheckPoint.getBlockY() &&
                    location.getBlockZ() == currentCheckPoint.getBlockZ()) {
                return false;
            }
        }

        this.currentCheckPoint = location.add(0.5, 0, 0.5);
        this.checkPoint++;

        return true;
    }

    public void toggleFreeze() {
        frozen = !frozen;
    }

    public String getNameColored() {
        return color.getColor() + name;
    }

    public void addWin() {
        points++;
    }

    public String getNameUnColored() {
        return name;
    }

    public void playSound(Sound sound) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }

    public void teleport(Location location) {
        Player player = getPlayer();
        if (player == null)
            return;
        player.teleport(location);
    }

    public void playKillEffect() {
        Participant lastAttacker = getLastAttacker();
        if (lastAttacker == null)
            return;
        UUID attckerUUID = lastAttacker.getPlayerUUID();
        if (attckerUUID == null)
            return;
        Profile profile = API.getProfile(attckerUUID);
        Player player = getPlayer();
        Player killer = Bukkit.getPlayer(attckerUUID);
        if (profile == null || player == null || killer == null)
            return;
        profile.getSettingData().getKillEffect().execute(player, killer);
    }

    public Location getSpawn(Match match) {
        if (currentCheckPoint != null)
            return currentCheckPoint;
        return match.getSpawn(this);
    }

    public Location getSpawn(IMatch match) {
        return getSpawn((Match) match);
    }

    public Profile getProfile() {
        return API.getProfile(playerUUID);
    }

    public void sendTitle(TextComponent header, TextComponent footer, int duration) {
        Player player = getPlayer();
        if (player == null)
            return;
        PlayerUtil.sendTitle(player, header, footer, duration);
    }

    public void sendTitle(MessagesLocale header, MessagesLocale footer, int duration) {
        Player player = getPlayer();
        if (player == null)
            return;
        PlayerUtil.sendTitle(player, CC.color(header.getString()), CC.color(footer.getString()), duration);
    }

    public void sendMessage(TextComponent message) {
        PlayerUtil.sendMessage(playerUUID, message);
    }

    public void sendMessage(MessagesLocale message, Replacement... replacements) {
        Player player = getPlayer();
        if (player == null)
            return;
        message.send(player, replacements);
    }

    public void resetCombo() {
        combo = 0;
    }

    public void handleHit(Participant opponent) {
        Participant lastAttacker = opponent.getLastAttacker();
        if (lastAttacker != null && !lastAttacker.equals(this)) {
            resetCombo();
        }

        hits++;
        combo++;
        if (combo > longestCombo) {
            longestCombo = combo;
        }

        if (MessagesLocale.MATCH_COMBO_MESSAGE_ENABLE.getBoolean()) {
            switch (combo) {
                case 5:
                    MessagesLocale.MATCH_COMBO_MESSAGE_5.send(playerUUID);
                    break;
                case 10:
                    MessagesLocale.MATCH_COMBO_MESSAGE_10.send(playerUUID);
                    break;
                case 20:
                    MessagesLocale.MATCH_COMBO_MESSAGE_20.send(playerUUID);
                    break;
            }
        }
        Match match = API.getProfile(playerUUID).getMatch();
        if (match.getKit().is(KitRule.BOXING)) {
            if (match instanceof TeamFightMatch teamFightMatch
                    ? hits >= teamFightMatch.getTeamA().getParticipants().size() * 100
                    : hits >= 100) {
                opponent.setDeathCause(getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
                match.onDeath(opponent);
            }
        }
    }

    public String getHitsDifference(IParticipant otherParticipant) {
        String hitDiff = String.valueOf(hits - otherParticipant.getHits());
        if (hits - otherParticipant.getHits() > 0) {
            return MessagesLocale.MATCH_BOXING_HIT_DIFFERENCE_HIGHER.getString().replace("<hit-difference>",
                    hitDiff);
        } else if (hits - otherParticipant.getHits() < 0) {
            return MessagesLocale.MATCH_BOXING_HIT_DIFFERENCE_LOWER.getString().replace("<hit-difference>",
                    hitDiff);
        } else {
            return MessagesLocale.MATCH_BOXING_HIT_DIFFERENCE_EQUAL.getString().replace("<hit-difference>",
                    hitDiff);
        }
    }

    public String getHitsDifferenceUncolored(IParticipant otherParticipant) {
        if (hits - otherParticipant.getHits() > 0) {
            return "+" + (hits - otherParticipant.getHits());
        } else if (hits - otherParticipant.getHits() < 0) {
            return String.valueOf(hits - otherParticipant.getHits());
        } else {
            return "-" + (hits - otherParticipant.getHits());
        }
    }

    public String getDeathMessage() {
        Profile profile = API.getProfile(playerUUID);
        if (profile == null) {
            return "";
        }

        Match match = profile.getMatch();
        if (match == null) {
            return "";
        }

        Participant lastAttacker = getLastAttacker();
        if (lastAttacker == null) {
            return "";
        }

        Profile attackerProfile = API.getProfile(lastAttacker.getPlayerUUID());
        if (attackerProfile == null || attackerProfile.getSettingData().getKillMessagePackage() == null) {
            return "";
        }

        return attackerProfile.getSettingData().getKillMessagePackage().getRandomMessage()
                .replace("<player>", getNameColored())
                .replace("<killer>", getLastAttackerName());
    }

    public String getLastAttackerName() {
        return Optional.ofNullable(getLastAttacker())
                .map(Participant::getNameColored)
                .orElse("");
    }

    public Player getPlayer() {
        Player p = Bukkit.getPlayer(playerUUID);
        if (p != null)
            return p;
        return Bukkit.getPlayer(getName());
    }
}
