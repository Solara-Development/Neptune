package dev.lrxh.neptune.profile.data;

import dev.lrxh.api.profile.IProfileState;

public enum ProfileState implements IProfileState {
    IN_LOBBY,
    IN_QUEUE,
    IN_GAME,
    IN_KIT_EDITOR,
    IN_SPECTATOR,
    IN_PARTY,
    IN_CUSTOM,
    IN_EVENT
}
