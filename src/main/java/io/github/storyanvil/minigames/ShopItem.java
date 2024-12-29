package io.github.storyanvil.minigames;

import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;

public class ShopItem {
    public int price;
    public ItemInput item;
    public int index;
    public Component name;

    public ShopItem(int price, ItemInput item, int index, Component name) {
        this.price = price;
        this.item = item;
        this.index = index;
        this.name = name;
    }
}
