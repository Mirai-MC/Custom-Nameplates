/*
 * Compatibility replacement for cloud-bukkit's ItemStackParser on Minecraft
 * 26.3. Paper made the legacy CraftItemStack conversion methods private, which
 * causes cloud-minecraft 2.0.0 to fail during command manager construction.
 */
package org.incendo.cloud.bukkit.parser;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.bukkit.data.ProtoItemStack;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public final class ItemStackParser<C> implements ArgumentParser.FutureArgumentParser<C, ProtoItemStack> {

    public static <C> ParserDescriptor<C, ProtoItemStack> itemStackParser() {
        return ParserDescriptor.of(new ItemStackParser<>(), ProtoItemStack.class);
    }

    public static <C> CommandComponent.Builder<C, ProtoItemStack> itemStackComponent() {
        return CommandComponent.<C, ProtoItemStack>builder().parser(itemStackParser());
    }

    @Override
    public CompletableFuture<ArgumentParseResult<ProtoItemStack>> parseFuture(
            CommandContext<C> context,
            CommandInput input
    ) {
        String materialName = input.readString();
        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            material = Material.matchMaterial(materialName.toUpperCase(Locale.ROOT));
        }
        if (material == null || !material.isItem()) {
            return ArgumentParseResult.failureFuture(
                    new IllegalArgumentException("Unknown item material: " + materialName)
            );
        }

        Material parsedMaterial = material;
        ProtoItemStack result = new ProtoItemStack() {
            @Override
            public Material material() {
                return parsedMaterial;
            }

            @Override
            public boolean hasExtraData() {
                return false;
            }

            @Override
            public ItemStack createItemStack(int amount) {
                return new ItemStack(parsedMaterial, amount);
            }
        };
        return ArgumentParseResult.successFuture(result);
    }

    @Override
    public SuggestionProvider<C> suggestionProvider() {
        return SuggestionProvider.noSuggestions();
    }
}
