package dev.lrxh.api.profile;


import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IProfileService {
    CompletableFuture<IProfile> getProfile(UUID uuid);
}
