package me.realized.duels.utilities.compat;

import me.realized.duels.Core;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Attributes {

    // TODO: 11/8/16 Use a NBT Library in the future. This class can probably be optimized a lot, but lack of time

    private static final Class<?> itemStack = Reflections.getNMSClass("ItemStack");
    private static final Class<?> craftItemStack = Reflections.getCBClass("inventory.CraftItemStack");

    private static final Class<?> nbtTagCompound = Reflections.getNMSClass("NBTTagCompound");
    private static final Class<?> nbtTagList = Reflections.getNMSClass("NBTTagList");
    private static final Class<?> nbtBase = Reflections.getNMSClass("NBTBase");

    private static final Method asNMSCopy = Reflections.getMethod(craftItemStack, "asNMSCopy", ItemStack.class);
    private static final Method asBukkitCopy = Reflections.getMethod(craftItemStack, "asBukkitCopy", itemStack);

    private static final Method getTag = Reflections.getMethod(itemStack, "getTag");
    private static final Method setTag = Reflections.getMethod(itemStack, "setTag", nbtTagCompound);

    private static final Method getList = Reflections.getMethod(nbtTagCompound, "getList", String.class, int.class);
    private static final Method set = Reflections.getMethod(nbtTagCompound, "set", String.class, nbtBase);
    private static final Method getString = Reflections.getMethod(nbtTagCompound, "getString", String.class);
    private static final Method setString = Reflections.getMethod(nbtTagCompound, "setString", String.class, String.class);
    private static final Method getInt = Reflections.getMethod(nbtTagCompound, "getInt", String.class);
    private static final Method setInt = Reflections.getMethod(nbtTagCompound, "setInt", String.class, int.class);
    private static final Method getDouble = Reflections.getMethod(nbtTagCompound, "getDouble", String.class);
    private static final Method setDouble = Reflections.getMethod(nbtTagCompound, "setDouble", String.class, double.class);
    private static final Method setLong = Reflections.getMethod(nbtTagCompound, "setLong", String.class, long.class);

    private static final Method add = Reflections.getMethod(nbtTagList, "add", nbtBase);
    private static final Method size = Reflections.getMethod(nbtTagList, "size");
    private static final Method get = Reflections.getMethod(nbtTagList, "get", int.class);

    private ItemStack item;
    private Object list;

    public Attributes(ItemStack item) {
        this.item = item;

        try {
            Object stack = asNMSCopy.invoke(null, item);
            Object tagCompound = getTag.invoke(stack);

            if (tagCompound == null) {
                tagCompound = nbtTagCompound.newInstance();
            }

            Object nbtList = getList.invoke(tagCompound, "AttributeModifiers", 10);

            if (nbtList == null) {
                return;
            }

            this.list = nbtList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ItemStack addModifier(String name, String attrName, int operation, double amount) {
        // Example Usage: ItemStack new = new Attributes(old).addModifier("GENERIC_MAX_HEALTH", "generic.maxHealth", 0, 20)
        // setItemInHand(new)

        try {
            Object compound = generateModifier(name, attrName, operation, amount);
            Object stack = asNMSCopy.invoke(null, item);
            Object tagCompound = getTag.invoke(stack);

            if (tagCompound == null) {
                tagCompound = nbtTagCompound.newInstance();
            }

            add.invoke(list, compound);
            set.invoke(tagCompound, "AttributeModifiers", list);
            setTag.invoke(stack, tagCompound);
            this.item = (ItemStack) asBukkitCopy.invoke(null, stack);
        } catch (Exception e) {
            Core.getInstance().warn("Failed to add attribute " + attrName + ": " + e.getMessage());
        }

        return item;
    }

    private Object generateModifier(String name, String attrName, int operation, double amount) {
        try {
            Object compound = nbtTagCompound.newInstance();
            setString.invoke(compound, "Name", name);
            setString.invoke(compound, "AttributeName", attrName);
            setInt.invoke(compound, "Operation", operation);
            setDouble.invoke(compound, "Amount", amount);

            UUID random = UUID.randomUUID();
            setLong.invoke(compound, "UUIDMost", random.getMostSignificantBits());
            setLong.invoke(compound, "UUIDLeast", random.getLeastSignificantBits());
            return compound;
        } catch (Exception e) {
            return null;
        }
    }

    public List<AttributeModifier> getModifiers() {
        List<AttributeModifier> result = new ArrayList<>();

        if (list == null) {
            return result;
        }

        try {
            for (int i = 0; i < (int) size.invoke(list); i++) {
                Object compound = get.invoke(list, i);
                String name = (String) getString.invoke(compound, "Name");
                String attrName = (String) getString.invoke(compound, "AttributeName");
                int operation = (int) getInt.invoke(compound, "Operation");
                double amount = (double) getDouble.invoke(compound, "Amount");
                result.add(new AttributeModifier(name, attrName, operation, amount));
            }
        } catch (Exception ignored) {}
        return result;
    }

    public ItemStack result() {
        return item;
    }

    public class AttributeModifier {

        private final String name;
        private final String attrName;
        private final int operation;
        private final double amount;

        public AttributeModifier(String name, String attrName, int operation, double amount) {
            this.name = name;
            this.attrName = attrName;
            this.operation = operation;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public String getAttrName() {
            return attrName;
        }

        public int getOperation() {
            return operation;
        }

        public double getAmount() {
            return amount;
        }
    }
}
