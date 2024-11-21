package project.project.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import project.project.components.InventoryComponent;
import project.project.components.TextureComponent;
import project.project.entities.InventoryItem;
import project.project.entities.Player;

public class InventoryUI {
    private Table inventoryTable;
    private Table hotbarTable;
    private Group hotbarGroup; // For hotbar inside inventory UI
    private Player player;
    private Stage stage;

    private BitmapFont font;
    private Texture brownSquareTexture;

    private InventoryItem pickedUpItem = null;
    private Image draggingItemImage = null; // Image for the item currently being dragged

    private boolean isVisible = false;

    public InventoryUI(Stage stage, Player player) {
        this.stage = stage;
        this.player = player;

        inventoryTable = new Table();
        hotbarTable = new Table();
        hotbarGroup = new Group();

        // Initialize font
        font = new BitmapFont();

        // Create brown square texture
        brownSquareTexture = createBrownSquareTexture(58, 58);

        // Initialize dragging item image
        draggingItemImage = new Image();
        draggingItemImage.setVisible(false);
        stage.addActor(draggingItemImage);

        // Populate inventory and hotbar slots
        populateInventoryUI();
        populateHotbarUI();

        // Add hotbar to stage
        stage.addActor(hotbarGroup);
    }

    private Texture createBrownSquareTexture(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.6f, 0.3f, 0.0f, 0.5f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void populateInventoryUI() {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);
        inventoryTable.clear();

        float slotSize = 60;
        float padding = 6;

        TextureRegionDrawable brownSquareDrawable = new TextureRegionDrawable(new TextureRegion(brownSquareTexture));

        for (int row = 0; row < 5; row++) { // 5 rows of inventory
            for (int col = 0; col < 6; col++) { // 6 columns of inventory
                InventoryItem item = inventory.getItem(row, col);
                Stack slotStack = new Stack();

                // Add slot frame
                Image slotFrame = new Image(brownSquareDrawable);
                slotStack.add(slotFrame);

                if (item != null) {
                    // Add item image
                    TextureComponent itemTextureComponent = item.getTexture();
                    Image itemImage = new Image(new TextureRegionDrawable(new TextureRegion(itemTextureComponent.texture)));
                    itemImage.setSize(48, 48);
                    slotStack.add(itemImage);

                    // Add item count label
                    if (item.getQuantity() > 1) {
                        LabelStyle labelStyle = new LabelStyle();
                        labelStyle.font = font;

                        Label countLabel = new Label(String.valueOf(item.getQuantity()), labelStyle);
                        countLabel.setFontScale(1.0f);
                        countLabel.setAlignment(Align.bottomRight);
                        slotStack.add(countLabel);
                    }
                }

                final int finalRow = row;
                final int finalCol = col;

                // Click listener for inventory logic
                slotStack.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        handleInventoryClick(finalRow, finalCol, event);
                    }
                });

                inventoryTable.add(slotStack).size(slotSize, slotSize).pad(padding);
            }
            inventoryTable.row();
        }

        inventoryTable.setSize(
            (slotSize + padding) * 6,
            (slotSize + padding) * 5
        );
        inventoryTable.setPosition(
            (Gdx.graphics.getWidth() - inventoryTable.getWidth()) / 2f,
            (Gdx.graphics.getHeight() - inventoryTable.getHeight()) / 2f
        );

        stage.addActor(inventoryTable);
    }

    private void handleInventoryClick(int row, int col, InputEvent event) {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);
        InventoryItem clickedItem = inventory.getItem(row, col);

        // Shift-click logic: Move item to first available space in hotbar or inventory
        if (event.getButton() == Input.Buttons.LEFT && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            if (clickedItem != null) {
                moveToFirstAvailable(clickedItem, row, col);
                updateUI();
            }
            return;
        }

        if (pickedUpItem == null) {
            if (clickedItem != null) {
                if (event.getButton() == Input.Buttons.RIGHT) {
                    // Pick half
                    int halfQuantity = clickedItem.getQuantity() / 2;
                    pickedUpItem = new InventoryItem(
                        clickedItem.getName(),
                        halfQuantity,
                        clickedItem.getTexturePath(),
                        clickedItem.getItemType()
                    );
                    clickedItem.setQuantity(clickedItem.getQuantity() - halfQuantity);

                    if (clickedItem.getQuantity() == 0) {
                        inventory.clearSlot(row, col);
                    }
                } else {
                    // Pick full stack
                    pickedUpItem = clickedItem;
                    inventory.clearSlot(row, col);
                }

                updateDraggingItemImage();
            }
        } else {
            // Enforce hotbar restrictions
            if (row == 0 && !pickedUpItem.canPlaceInHotbar()) {
                // Prevent placing invalid items in the hotbar
                return;
            }

            if (event.getButton() == Input.Buttons.RIGHT) {
                if (clickedItem == null) {
                    inventory.setItem(row, col, new InventoryItem(
                        pickedUpItem.getName(),
                        1,
                        pickedUpItem.getTexturePath(),
                        pickedUpItem.getItemType()
                    ));
                } else if (clickedItem.isSameType(pickedUpItem) && clickedItem.getQuantity() < 64) {
                    clickedItem.setQuantity(clickedItem.getQuantity() + 1);
                }
                pickedUpItem.setQuantity(pickedUpItem.getQuantity() - 1);

                if (pickedUpItem.getQuantity() == 0) {
                    pickedUpItem = null;
                    draggingItemImage.setVisible(false);
                }
            } else {
                // Left-click: Merge, place, or swap
                if (clickedItem == null) {
                    inventory.setItem(row, col, pickedUpItem);
                    pickedUpItem = null;
                    draggingItemImage.setVisible(false);
                } else if (clickedItem.isSameType(pickedUpItem) && clickedItem.getQuantity() < 64) {
                    int spaceLeft = 64 - clickedItem.getQuantity();
                    int toTransfer = Math.min(spaceLeft, pickedUpItem.getQuantity());
                    clickedItem.setQuantity(clickedItem.getQuantity() + toTransfer);
                    pickedUpItem.setQuantity(pickedUpItem.getQuantity() - toTransfer);

                    if (pickedUpItem.getQuantity() == 0) {
                        pickedUpItem = null;
                        draggingItemImage.setVisible(false);
                    }
                } else {
                    // Swap items
                    InventoryItem temp = clickedItem;
                    inventory.setItem(row, col, pickedUpItem);
                    pickedUpItem = temp;

                    updateDraggingItemImage();
                }
            }
        }

        updateUI();
    }

    private void updateDraggingItemImage() {
        if (pickedUpItem != null) {
            draggingItemImage.setDrawable(new TextureRegionDrawable(new TextureRegion(pickedUpItem.getTexture().texture)));
            draggingItemImage.setSize(48, 48);
            draggingItemImage.setVisible(true);
        } else {
            draggingItemImage.setDrawable(null);
        }
    }

    private void moveToFirstAvailable(InventoryItem item, int originalRow, int originalCol) {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);

        // Search hotbar
        if (item.canPlaceInHotbar()) {
            for (int col = 0; col < 6; col++) {
                InventoryItem hotbarItem = inventory.getItem(0, col);
                if (hotbarItem == null) {
                    inventory.setItem(0, col, item);
                    inventory.clearSlot(originalRow, originalCol);
                    return;
                } else if (hotbarItem.isSameType(item) && hotbarItem.getQuantity() < 64) {
                    int spaceLeft = 64 - hotbarItem.getQuantity();
                    int toTransfer = Math.min(spaceLeft, item.getQuantity());
                    hotbarItem.setQuantity(hotbarItem.getQuantity() + toTransfer);
                    item.setQuantity(item.getQuantity() - toTransfer);

                    if (item.getQuantity() == 0) {
                        inventory.clearSlot(originalRow, originalCol);
                    }
                    return;
                }
            }
        }

        // Search main inventory
        for (int row = 1; row < 5; row++) {
            for (int col = 0; col < 6; col++) {
                InventoryItem inventoryItem = inventory.getItem(row, col);
                if (inventoryItem == null) {
                    inventory.setItem(row, col, item);
                    inventory.clearSlot(originalRow, originalCol);
                    return;
                } else if (inventoryItem.isSameType(item) && inventoryItem.getQuantity() < 64) {
                    int spaceLeft = 64 - inventoryItem.getQuantity();
                    int toTransfer = Math.min(spaceLeft, item.getQuantity());
                    inventoryItem.setQuantity(inventoryItem.getQuantity() + toTransfer);
                    item.setQuantity(item.getQuantity() - toTransfer);

                    if (item.getQuantity() == 0) {
                        inventory.clearSlot(originalRow, originalCol);
                    }
                    return;
                }
            }
        }
    }

    private void populateHotbarUI() {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);
        hotbarTable.clear();

        float slotSize = 64; // Fixed size for hotbar slots
        float itemSize = 48; // Adjusted size for the item icons to fit neatly inside the slots

        TextureRegionDrawable brownSquareDrawable = new TextureRegionDrawable(new TextureRegion(brownSquareTexture));

        for (int col = 0; col < 6; col++) { // Set hotbar to 6 slots
            InventoryItem item = inventory.getItem(0, col); // Hotbar should use row 0 of the inventory (in inventory UI)
            Stack slotStack = new Stack(); // Use Stack to overlay the item with the frame or labels

            // Add the brown square as the slot border
            Image slotFrame = new Image(brownSquareDrawable);
            slotStack.add(slotFrame);

            if (item != null) {
                TextureComponent itemTextureComponent = item.getTexture();
                Image itemImage = new Image(new TextureRegionDrawable(new TextureRegion(itemTextureComponent.texture)));
                itemImage.setSize(itemSize, itemSize); // Resize item image to fit inside the slot properly
                itemImage.setPosition((slotSize - itemSize) / 2f, (slotSize - itemSize) / 2f); // Center item inside slot
                slotStack.add(itemImage); // Add item image to the stack

                if (item.getQuantity() > 1) {
                    LabelStyle labelStyle = new LabelStyle();
                    labelStyle.font = font;

                    Label countLabel = new Label(String.valueOf(item.getQuantity()), labelStyle);
                    countLabel.setFontScale(1.0f); // Set appropriate font size for better readability
                    countLabel.setAlignment(Align.bottomRight); // Align label to the bottom-right
                    slotStack.add(countLabel); // Add count label on top of the item image
                }
            }

            hotbarTable.add(slotStack).size(slotSize, slotSize).pad(4); // Reduced padding between slots for better alignment
        }

        hotbarTable.setSize((slotSize + 10) * 6, slotSize); // Account for slot size and padding (6 slots)
        hotbarTable.setPosition(
            0,
            0 // Positioned within hotbarGroup, which is positioned itself
        );

        hotbarGroup.addActor(hotbarTable); // Add the table to the hotbar group
    }

    public void setVisible(boolean visible) {
        isVisible = visible;

        // Update visibility of inventory table and related components
        inventoryTable.setVisible(visible);
        hotbarGroup.setVisible(visible); // Ensure hotbar is visible only if the inventory UI is visible
    }

    public void updateUI() {
        if (!isVisible) return;

        populateInventoryUI();
        populateHotbarUI();

        // Update dragging item image position to follow the mouse cursor
        if (pickedUpItem != null) {
            draggingItemImage.setDrawable(new TextureRegionDrawable(new TextureRegion(pickedUpItem.getTexture().texture)));
            draggingItemImage.setSize(48, 48);
            draggingItemImage.setPosition(
                Gdx.input.getX() - draggingItemImage.getWidth() / 2f,
                Gdx.graphics.getHeight() - Gdx.input.getY() - draggingItemImage.getHeight() / 2f
            );
            draggingItemImage.setVisible(true);
        } else {
            draggingItemImage.setVisible(false);
        }
    }

    public void resize(int width, int height) {
        // Update inventory table position based on new screen dimensions
        float slotSize = 60;
        float padding = 6;

        inventoryTable.setSize(
            (slotSize + padding) * 6, // Total width of inventory (6 columns)
            (slotSize + padding) * 5  // Total height of inventory (5 rows)
        );
        inventoryTable.setPosition(
            (width - inventoryTable.getWidth()) / 2f, // Center horizontally
            (height - inventoryTable.getHeight()) / 2f // Center vertically
        );

        // Update hotbar group position based on new screen dimensions
        hotbarGroup.setSize((slotSize + 10) * 6, slotSize);
        hotbarGroup.setPosition(
            (width - hotbarGroup.getWidth()) / 2f, // Center horizontally
            20 // Slightly above the bottom of the screen
        );

        // Make sure inventoryTable and hotbarGroup are set to visible state if inventory is open
        inventoryTable.setVisible(isVisible);
        hotbarGroup.setVisible(isVisible);
    }
}

