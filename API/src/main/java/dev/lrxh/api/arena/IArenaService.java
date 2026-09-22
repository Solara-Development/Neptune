package dev.lrxh.api.arena;

import java.util.LinkedHashSet;
import java.util.List;

public interface IArenaService {
    LinkedHashSet<IArena> getAllArenas();

    IArena getArenaByName(String name);

    List<IArena> getDuplicatesApi(IArena owner);

    IArena getFreeDuplicateApi(IArena owner);
}
