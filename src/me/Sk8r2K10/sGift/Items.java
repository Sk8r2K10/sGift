package me.Sk8r2K10.sGift;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Leaves;

public class Items {

    private static String[] potionames = new String[]{"Water Bottle",
        "Potion of Regeneration", "Potion of Swiftness",
        "Potion of Fire Resistance", "Potion of Poison",
        "Potion of Healing", "Clear Potion", "Clear Potion",
        "Potion of Weakness", "Potion of Strength", "Potion of Slowness",
        "Diffuse Potion", "Potion of Harming", "Artless Potion",
        "Thin Potion", "Thin Potion", "Awkward Potion",
        "Potion of Regeneration", "Potion of Swiftness",
        "Potion of Fire Resistance", "Potion of Poison",
        "Potion of Healing", "Bungling Potion", "Bungling Potion",
        "Potion of Weakness", "Potion of Strength", "Potion of Slowness",
        "Smooth Potion", "Potion of Harming", "Suave Potion",
        "Debonair Potion", "Debonair Potion", "Thick Potion",
        "Potion of Regeneration II", "Potion of Swiftness II",
        "Potion of Fire Resistance", "Potion of Poison II",
        "Potion of Healing II", "Charming Potion", "Charming Potion",
        "Potion of Weakness", "Potion of Strength II",
        "Potion of Slowness", "Refined Potion", "Potion of Harming II",
        "Cordial Potion", "Sparkling Potion", "Sparkling Potion",
        "Potent Potion", "Potion of Regeneration II",
        "Potion of Swiftness II", "Potion of Fire Resistance",
        "Potion of Poison II", "Potion of Healing II", "Rank Potion",
        "Rank Potion", "Potion of Weakness", "Potion of Strength II",
        "Potion of Slowness", "Acrid Potion", "Potion of Harming II",
        "Gross Potion", "Stinky Potion", "Stinky Potion"};

    public static ItemStack parse(String item, int amount) {
        String name;
        String dataname = null;
        Material mat;
        int id = 0;
        int dataid = 0;

        // Is there a data value included?
        if (item.contains(":")) {
            String[] split = item.split(":");
            name = split[0];
            dataname = split[1];

        } else {
            name = item;
        }

        // Is name actually an id?
        try {
            id = Integer.parseInt(name);
        } catch (NumberFormatException e) {
            mat = Material.matchMaterial(name);
            if (mat == null) {
                return null;
            } else {
                id = mat.getId();
            }
        }

        if (id == 0) {
            return null;
        }
        if (dataname != null) {
            dataid = matchData(id, dataname);
            ItemStack is = new ItemStack(id, amount, (short) dataid,
                    (byte) dataid);
            if (is.getType() == Material.POTION) {
                is.setDurability((short) dataid);
            }
            return is;
        } else {
            return new ItemStack(id, amount);
        }
    }

    private static int matchData(int id, String dataname) {
        try {
            // First let's try the dataname as if it was a number
            return Integer.parseInt(dataname);
        } catch (NumberFormatException e) {
        }

        // So the value isn't a number, but it may be an alias!
        switch (id) {
            // wood


            case 05:
                if (dataname.equalsIgnoreCase("dark") || dataname.equalsIgnoreCase("redwood")) {
                    return 1;
                } else if (dataname.equalsIgnoreCase("light") || dataname.equalsIgnoreCase("birch")) {
                    return 2;
                } else if (dataname.equalsIgnoreCase("jungle")) {
                    return 3;
                }
                
                return -1;
            case 06:
                if (dataname.equalsIgnoreCase("redwood")) {
                    return 1;
                } else if (dataname.equalsIgnoreCase("birch")) {
                    return 2;
                } else  if (dataname.equalsIgnoreCase("jungle")) {
                    return 3;
                }

                return -1;
            case 17:
                if (dataname.equalsIgnoreCase("redwood")) {
                    return 1;
                } else if (dataname.equalsIgnoreCase("birch")) {
                    return 2;
                } else if (dataname.equalsIgnoreCase("jungle")) {
                    return 3;
                }
                
                return -1;
            case 18:
                if (dataname.equalsIgnoreCase("redwood")) {
                    return 1;
                } else if (dataname.equalsIgnoreCase("birch")) {
                    return 2;
                } else if (dataname.equalsIgnoreCase("jungle")) {
                    return 3;
                }

                return -1;
            // slab
            case 44:
                if (dataname.equalsIgnoreCase("stone")) {
                    return 0;
                } else if (dataname.equalsIgnoreCase("sandstone")) {
                    return 1;
                } else if (dataname.equalsIgnoreCase("wood")) {
                    return 2;
                } else if (dataname.equalsIgnoreCase("cobblestone")
                        || dataname.equalsIgnoreCase("cobble")) {
                    return 3;
                } else if (dataname.equalsIgnoreCase("brick")) {
                    return 4;
                } else if (dataname.equalsIgnoreCase("stonebrick")) {
                    return 5;
                } else {
                    return -1;
                }
            // d-slab
            case 43:
                if (dataname.equalsIgnoreCase("stone")) {
                    return 0;
                } else if (dataname.equalsIgnoreCase("sandstone")) {
                    return 1;
                } else if (dataname.equalsIgnoreCase("wood")) {
                    return 2;
                } else if (dataname.equalsIgnoreCase("cobblestone")
                        || dataname.equalsIgnoreCase("cobble")) {
                    return 3;
                } else if (dataname.equalsIgnoreCase("brick")) {
                    return 4;
                } else if (dataname.equalsIgnoreCase("stonebrick")) {
                    return 5;
                } else {
                    return -1;
                }

            case 35:
                DyeColor col = DyeColor.valueOf(dataname.toUpperCase());
                if (col != null) {
                    return col.getData();
                }

                return -1;
            case 351: // Dye
                DyeColor dyecol = DyeColor.valueOf(dataname.toUpperCase());
                if (dyecol != null) {
                    return 15 - dyecol.getData();
                }

                return -1;
            default:
                return 0;
        }
    }

    public static String name(ItemStack is) {
        String name = is.getData().getItemType().name().toLowerCase().replace("_", " ");
        String prefix = "";
        byte data = is.getData().getData();
        int dataint = (int) data;
        if (dataint != 0) {
            switch (is.getData().getItemTypeId()) {
                            case 05:
                if (dataint == 1) {
                    prefix = "dark";
                } else if (dataint == 2) {
                    prefix = "light";
                } else if (dataint == 3) {
                    prefix = "jungle";
                }
                case 6:
                    if (dataint == 1) {
                        prefix = "redwood";
                    } else if (dataint == 2) {
                        prefix = "birch";
                    }
                    break;
                case 17:
                    if (dataint == 1) {
                        prefix = "redwood";
                    } else if (dataint == 2) {
                        prefix = "birch";
                    }
                    break;
                case 18:
                    prefix = new Leaves(18, data).toString().toLowerCase().replace("_", " ");
                    // slab
                    break;
                case 44:
                    if (dataint == 0) {
                        prefix = "stone";
                    } else if (dataint == 1) {
                        prefix = "sandstone";
                    } else if (dataint == 2) {
                        prefix = "wood";
                    } else if (dataint == 3) {
                        prefix = "cobblestone";
                    } else if (dataint == 4) {
                        prefix = "brick";
                    } else if (dataint == 5) {
                        prefix = "stonebrick";
                    } else {
                        prefix = "stone";
                    }
                    // d-slab
                    break;
                case 43:
                    if (dataint == 0) {
                        prefix = "stone";
                    } else if (dataint == 1) {
                        prefix = "sandstone";
                    } else if (dataint == 2) {
                        prefix = "wood";
                    } else if (dataint == 3) {
                        prefix = "cobblestone";
                    } else if (dataint == 4) {
                        prefix = "brick";
                    } else if (dataint == 5) {
                        prefix = "stonebrick";
                    } else {
                        prefix = "stone";
                    }
                    break;
                case 35:
                    prefix = DyeColor.getByData(data).toString().toLowerCase().replace("_", " ");
                    break;
                case 351: // Dye
                    // prefix =
                    // DyeColor.getByData(data).toString().toLowerCase().replace("_",
                    // " ");
                    if (dataint == 0) {
                        name = "ink sack";
                    } else if (dataint == 1) {
                        name = "red dye";
                    } else if (dataint == 2) {
                        name = "green dye";
                    } else if (dataint == 3) {
                        name = "cocoa beans";
                    } else if (dataint == 4) {
                        name = "lapis lazuli";
                    } else if (dataint == 5) {
                        name = "purple dye";
                    } else if (dataint == 6) {
                        name = "cyan dye";
                    } else if (dataint == 7) {
                        name = "light gray dye";
                    } else if (dataint == 8) {
                        name = "gray dye";
                    } else if (dataint == 9) {
                        name = "pink dye";
                    } else if (dataint == 10) {
                        name = "lime dye";
                    } else if (dataint == 11) {
                        name = "yellow dye";
                    } else if (dataint == 12) {
                        name = "light blue dye";
                    } else if (dataint == 13) {
                        name = "magenta dye";
                    } else if (dataint == 14) {
                        name = "orange dye";
                    } else if (dataint == 15) {
                        name = "bone meal";
                    }
                    break;
                default:
                    prefix = "";
            }
        }
        if (prefix.equals("")) {
            return name;
        } else {
            return name + " (" + prefix + ")";
        }
    }

    public static String namePotion(int data) {
        String fin = "";
        String maxAmpStr = Integer.toBinaryString(data);
        char[] arr = maxAmpStr.toCharArray();
        boolean[] binaryarray = new boolean[arr.length];
        for (int i = 0; i < maxAmpStr.length(); i++) {
            if (arr[i] == '1') {
                binaryarray[i] = true;
            } else if (arr[i] == '0') {
                binaryarray[i] = false;
            }
        }

        if (binaryarray.length > 14) {
            fin += "Splash ";
        } else {
            fin += "";
        }

        int namer = 0;
        for (int x = binaryarray.length - 1; x < binaryarray.length - 5 && x < 0; x--) {
            System.out.println(x);
            if (binaryarray[x] == true) {
                namer += 2 ^ x;
            }
        }
        System.out.println(namer);

        fin += potionames[namer];

        return fin;
    }

    public static String nameEnch(Enchantment ench) {

        String fin = "";

        switch (ench.getId()) {
            case 0:
                fin = "Protection";
                break;
            case 1:
                fin = "Fire Protection";
                break;
            case 2:
                fin = "Feather Falling";
                break;
            case 3:
                fin = "Blast Protection";
                break;
            case 4:
                fin = "Projectile Protection";
                break;
            case 5:
                fin = "Respiration";
                break;
            case 6:
                fin = "Aqua Affinity";
                break;
            case 16:
                fin = "Sharpness";
                break;
            case 17:
                fin = "Smite";
                break;
            case 18:
                fin = "Bane of Arthropods";
                break;
            case 19:
                fin = "Knockback";
                break;
            case 20:
                fin = "Fire Aspect";
                break;
            case 21:
                fin = "Looting";
                break;
            case 48:
                fin = "Power";
                break;
            case 49:
                fin = "Punch";
                break;
            case 50:
                fin = "Flame";
                break;
            case 51:
                fin = "Infinity";
                break;
            case 32:
                fin = "Efficiency";
                break;
            case 33:
                fin = "Silk Touch";
                break;
            case 34:
                fin = "Unbreaking";
                break;
            case 35:
                fin = "Fortune";
                break;
            default:
                fin = "UNKNOWN";
                break;
        }

        return fin;

    }
}