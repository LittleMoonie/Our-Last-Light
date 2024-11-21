package project.project.entities;

import project.project.components.TextureComponent;

public class InventoryItem {
    private String name;
    private int quantity;
    private String texturePath;
    private ItemType itemType; // New field for item type
    private transient TextureComponent texture;

    public InventoryItem(String name, int quantity, String texturePath, ItemType itemType) {
        this.name = name;
        this.quantity = quantity;
        this.texturePath = texturePath;
        this.itemType = itemType;
        this.texture = new TextureComponent(texturePath, 32, 32); // Initialize the texture
    }

    public String getType() {
        return name;
    }

    public boolean isSameType(InventoryItem other) {
        if (other == null) {
            return false;
        }
        return this.name.equals(other.getType());
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public TextureComponent getTexture() {
        if (texture == null && texturePath != null) {
            texture = new TextureComponent(texturePath, 32, 32);
        }
        return texture;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    public String getTexturePath() {
        return texturePath;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public boolean canPlaceInHotbar() {
        return itemType == ItemType.WEAPON || itemType == ItemType.CONSUMABLE;
    }
}
