package dev.lrxh.neptune.game.arena.allocator;

public final class Allocation {
    public final long id;
    public final int chunkX;
    public final int chunkZ;
    public final int widthChunks;
    public final int depthChunks;

    Allocation(long id, int chunkX, int chunkZ, int widthChunks, int depthChunks) {
        this.id = id;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.widthChunks = widthChunks;
        this.depthChunks = depthChunks;
    }
}