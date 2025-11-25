# Neptune Plugin Memory Leak Fix - Analysis and Resolution

## Issue Summary
The Neptune plugin experienced a **critical memory leak** when duplicating arenas (Virtual Worlds). This leak occurred because virtual worlds were being created but never properly cleaned up when exceptions occurred during the duplication process.

## Root Cause Analysis

### Primary Issue: Incomplete Exception Handling in `Arena.createDuplicate()`

**Location**: `Plugin/src/main/java/dev/lrxh/neptune/game/arena/Arena.java` (lines 101-204)

**Problem**: 
When `createDuplicate()` creates a virtual world (line 160), if any exception occurs during the subsequent operations (such as pasting the snapshot at line 174), the virtual world remains loaded in memory but is never unloaded. This causes a memory leak because:

1. The virtual world is created via `BlockChanger.createVirtualWorld(creator)` (line 160)
2. If an exception occurs during `virtualWorld.paste(snapshot)` (line 174) or any other operation
3. The exception handler (lines 195-196) completes the future exceptionally but **never unloads the virtual world**
4. The virtual world stays in memory indefinitely, consuming server resources

### Secondary Issue: Missing Exception Handlers in Callers

**Locations**:
- `ArenaSelectMenu.java` (lines 54, 85)
- `KitSelectButton.java` (line 44)
- `RoundSelectButton.java` (line 48)
- `MatchService.java` (line 118)

**Problem**:
These locations call `createDuplicate()` or `getRandomArena()` (which internally calls `createDuplicate()`) but don't handle exceptions. When the CompletableFuture completes exceptionally, the exception is silently ignored, and players receive no feedback about the failure.

## Fixes Applied

### Fix #1: Virtual World Cleanup on Exception (CRITICAL)

**File**: `Arena.java`
**Lines Modified**: 195-201

**Change**:
```java
} catch (Exception ex) {
    // CRITICAL FIX: Unload the virtual world to prevent memory leak
    try {
        virtualWorld.unload();
    } catch (Exception unloadEx) {
        // Log but don't throw - we want to propagate the original exception
        unloadEx.printStackTrace();
    }
    future.completeExceptionally(ex);
}
```

**Impact**: This ensures that if any exception occurs after the virtual world is created, the virtual world is properly unloaded before the exception is propagated. This prevents the memory leak.

### Fix #2: Exception Handling in ArenaSelectMenu

**File**: `ArenaSelectMenu.java`
**Lines Modified**: 52-69, 81-95

**Changes**:
- Added `.exceptionally()` handlers to both random arena selection and specific arena selection
- Provides user feedback when arena duplication fails
- Logs exceptions for debugging

### Fix #3: Exception Handling in KitSelectButton

**File**: `KitSelectButton.java`
**Lines Modified**: 44-57

**Changes**:
- Added `.exceptionally()` handler to random arena selection
- Provides user feedback when arena duplication fails

### Fix #4: Exception Handling in RoundSelectButton

**File**: `RoundSelectButton.java`
**Lines Modified**: 48-62

**Changes**:
- Added `.exceptionally()` handler to random arena selection
- Provides user feedback when arena duplication fails

### Fix #5: Exception Handling in MatchService

**File**: `MatchService.java`
**Lines Modified**: 118-137

**Changes**:
- Added `.exceptionally()` handler to arena duplication in match creation
- Notifies both players when match creation fails
- Logs exceptions for debugging

## Testing Recommendations

To verify the memory leak is fixed, perform the following tests:

### 1. Normal Operation Test
- Create multiple matches with duplicated arenas
- Verify matches start and end normally
- Check that virtual worlds are properly cleaned up after matches end

### 2. Exception Scenario Test
To simulate exceptions during arena duplication:
- Temporarily modify the `Arena.createDuplicate()` method to throw an exception after creating the virtual world
- Attempt to start a match
- Verify that:
  - The virtual world is unloaded
  - Players receive an error message
  - No memory leak occurs
  - Server logs show the exception

### 3. Memory Monitoring
- Monitor server memory usage before and after multiple matches
- Use tools like VisualVM or JProfiler to track virtual world instances
- Verify that virtual worlds are garbage collected after matches end

### 4. Load Test
- Create many matches simultaneously
- Monitor for memory growth
- Verify all virtual worlds are properly cleaned up

## Additional Observations

### VirtualArena.restore() Implementation
The `VirtualArena.restore()` method is currently empty with a TODO comment. This is intentional because:
- Virtual arenas are temporary and don't need restoration
- They are unloaded and discarded after use
- The parent `Arena.restore()` method would incorrectly try to restore to the original world

### Match Cleanup Flow
The proper cleanup flow for matches is:
1. Match ends → `MatchEndRunnable` is triggered
2. `match.resetArena()` is called (removes entities, but VirtualArena.restore() does nothing)
3. `match.getArena().remove()` is called → `virtualWorld.unload()` is executed
4. Virtual world is properly unloaded and can be garbage collected

## Conclusion

The memory leak was caused by incomplete exception handling in the arena duplication process. The fixes ensure that:

1. **Virtual worlds are always cleaned up**, even when exceptions occur
2. **Users receive feedback** when arena duplication fails
3. **Exceptions are logged** for debugging purposes
4. **Server resources are properly managed** and not leaked

These changes should completely resolve the memory leak issue when duplicating arenas.
