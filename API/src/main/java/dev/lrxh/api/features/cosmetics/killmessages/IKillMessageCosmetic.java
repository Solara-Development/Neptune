package dev.lrxh.api.features.cosmetics.killmessages;

import dev.lrxh.api.features.cosmetics.ICosmetic;

import java.util.Map;

public interface IKillMessageCosmetic extends ICosmetic {
    Map<String, ? extends IKillMessagePackage> getPackages();
    IKillMessagePackage getDefault();
    IKillMessagePackage getOrDefault(String packageName);
    void addPackage(IKillMessagePackage killMessagePackage);
}
