package dev.lrxh.neptune.game.kit.command;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public class KitProvider extends DrinkProvider<Kit> {

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean allowNullArgument() {
        return false;
    }

    @Nullable
    @Override
    public Kit defaultNullValue() {
        return null;
    }

    @Nullable
    @Override
    public Kit provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations)
            throws CommandExitMessage {
        String name = arg.get();
        Kit kit = KitService.get().getKitByName(name);
        if (kit != null)
            return kit;
        throw new CommandExitMessage("[-] No kit with that name exists");
    }

    @Override
    public String argumentDescription() {
        return "kit";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return KitService.get().getKitNames();
    }
}