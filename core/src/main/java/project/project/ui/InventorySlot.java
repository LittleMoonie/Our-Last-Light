package project.project.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class InventorySlot extends ImageButton {
    private String itemName;
    private int quantity;

    public InventorySlot(Drawable slotBackground) {
        super(slotBackground);
        this.itemName = null;
        this.quantity = 0;
    }

    public void setItem(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = Math.min(quantity, 64); // Enforce stack limit
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int amount) {
        this.quantity = Math.min(this.quantity + amount, 64); // Add and enforce stack limit
    }

    public void removeQuantity(int amount) {
        this.quantity -= amount;
        if (this.quantity <= 0) {
            clear();
        }
    }

    public void clear() {
        this.itemName = null;
        this.quantity = 0;
    }

    public boolean isEmpty() {
        return itemName == null || quantity <= 0;
    }

    public boolean canStackWith(String itemName) {
        return this.itemName != null && this.itemName.equals(itemName) && this.quantity < 64;
    }
}
