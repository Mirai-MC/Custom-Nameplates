/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.bukkit.compatibility.emoji;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.chat.emoji.EmojiProvider;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ItemsAdderEmojiProvider implements EmojiProvider {

    private final Method replaceFontImages;

    public ItemsAdderEmojiProvider() throws ReflectiveOperationException {
        Class<?> wrapperClass = Class.forName("dev.lone.itemsadder.api.FontImages.FontImageWrapper");
        this.replaceFontImages = wrapperClass.getMethod("replaceFontImages", Player.class, String.class);
    }

    @Override
    public String replace(CNPlayer player, String text) {
        try {
            CustomNameplates.getInstance().debug(() -> "before: " + text);
            String result = ((String) replaceFontImages.invoke(null, (Player) player.player(), text))
                    .replace("§f", "<white><font:default>")
                    .replace("§r", "</font></white>");
            CustomNameplates.getInstance().debug(() -> "after: " + result);
            return result;
        } catch (IllegalAccessException | InvocationTargetException | LinkageError ignored) {
            return text;
        }
    }
}
