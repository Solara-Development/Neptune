package dev.lrxh.neptune.feature.party;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class PartyService {
    private static PartyService instance;
    private final HashMap<UUID, Party> parties = new HashMap<>();

    public static PartyService get() {
        if (instance == null) instance = new PartyService();

        return instance;
    }

    public void addParty(Party party) {
        this.parties.put(party.getLeader(), party);
    }

    public void removeParty(Party party) {
        this.parties.remove(party.getLeader());
    }
}
