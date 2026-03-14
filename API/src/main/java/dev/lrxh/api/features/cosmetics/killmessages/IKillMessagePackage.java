package dev.lrxh.api.features.cosmetics.killmessages;

import dev.lrxh.api.features.cosmetics.ICosmeticPackage;

import java.util.List;

public interface IKillMessagePackage extends ICosmeticPackage {
    List<String> getMessages();
    String getRandomMessage();
    String permission();
}
