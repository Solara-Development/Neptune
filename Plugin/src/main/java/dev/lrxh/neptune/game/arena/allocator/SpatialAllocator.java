package dev.lrxh.neptune.game.arena.allocator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SpatialAllocator {

    private static final SpatialAllocator INSTANCE = new SpatialAllocator();

    // occupancy map: maps (chunkX,chunkZ) key -> allocation id
    private final ConcurrentHashMap<Long, Long> occupancy = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Allocation> allocations = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // helper to encode chunk coords into a single long key
    private static long keyFor(int cx, int cz) {
        return (((long) cx) << 32) ^ (cz & 0xffffffffL);
    }

    public static SpatialAllocator get() {
        return INSTANCE;
    }

    private SpatialAllocator() {
    }

    /**
     * Allocate a rectangle on the chunk-grid sized widthChunks x depthChunks.
     * This will search outward in a spiral for the first free region.
     *
     * @param widthChunks  width in chunks (>=1)
     * @param depthChunks  depth in chunks (>=1)
     * @param gutterChunks minimum gutter (empty chunks) between allocations (>=0)
     * @param maxRadius    max spiral radius (in allocation steps). If not found within maxRadius, throws IllegalStateException.
     * @return an Allocation (non-null)
     */
    public synchronized Allocation allocate(int widthChunks, int depthChunks, int gutterChunks, int maxRadius) {
        if (widthChunks <= 0 || depthChunks <= 0) {
            throw new IllegalArgumentException("widthChunks/depthChunks must be > 0");
        }
        // search positions on a grid with stride = (widthChunks + gutter) and (depthChunks + gutter)
        int strideX = widthChunks + Math.max(0, gutterChunks);
        int strideZ = depthChunks + Math.max(0, gutterChunks);

        // spiral search around origin
        int layer;
        // check origin first
        if (regionFree(0, 0, widthChunks, depthChunks)) {
            return reserveAt(0, 0, widthChunks, depthChunks);
        }

        for (layer = 1; layer <= maxRadius; layer++) {
            // iterate the perimeter of the square ring at distance 'layer'
            for (int dx = -layer; dx <= layer; dx++) {
                // top row (cz = -layer)
                if (regionFree(dx * strideX, -layer * strideZ, widthChunks, depthChunks)) {
                    return reserveAt(dx * strideX, -layer * strideZ, widthChunks, depthChunks);
                }
                // bottom row (cz = +layer)
                if (regionFree(dx * strideX, layer * strideZ, widthChunks, depthChunks)) {
                    return reserveAt(dx * strideX, layer * strideZ, widthChunks, depthChunks);
                }
            }
            for (int dz = -layer + 1; dz <= layer - 1; dz++) {
                // left column (cx = -layer)
                if (regionFree(-layer * strideX, dz * strideZ, widthChunks, depthChunks)) {
                    return reserveAt(-layer * strideX, dz * strideZ, widthChunks, depthChunks);
                }
                // right column (cx = +layer)
                if (regionFree(layer * strideX, dz * strideZ, widthChunks, depthChunks)) {
                    return reserveAt(layer * strideX, dz * strideZ, widthChunks, depthChunks);
                }
            }
        }

        throw new IllegalStateException("SpatialAllocator: could not find free region within radius " + maxRadius);
    }

    public Allocation allocate(int widthChunks, int depthChunks) {
        return allocate(widthChunks, depthChunks, 1, 200);
    }

    private boolean regionFree(int startChunkX, int startChunkZ, int widthChunks, int depthChunks) {
        for (int x = startChunkX; x < startChunkX + widthChunks; x++) {
            for (int z = startChunkZ; z < startChunkZ + depthChunks; z++) {
                if (occupancy.containsKey(keyFor(x, z))) {
                    return false;
                }
            }
        }
        return true;
    }

    private Allocation reserveAt(int startChunkX, int startChunkZ, int widthChunks, int depthChunks) {
        long id = idCounter.getAndIncrement();
        Allocation alloc = new Allocation(id, startChunkX, startChunkZ, widthChunks, depthChunks);
        for (int x = startChunkX; x < startChunkX + widthChunks; x++) {
            for (int z = startChunkZ; z < startChunkZ + depthChunks; z++) {
                occupancy.put(keyFor(x, z), id);
            }
        }
        allocations.put(id, alloc);
        return alloc;
    }

    public synchronized void free(long allocationId) {
        Allocation alloc = allocations.remove(allocationId);
        if (alloc == null) return;
        for (int x = alloc.chunkX; x < alloc.chunkX + alloc.widthChunks; x++) {
            for (int z = alloc.chunkZ; z < alloc.chunkZ + alloc.depthChunks; z++) {
                occupancy.remove(keyFor(x, z), allocationId);
            }
        }
    }
}
