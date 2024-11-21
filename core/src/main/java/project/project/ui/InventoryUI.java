package project.project.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import project.project.Constants;
import project.project.components.*;
import project.project.entities.InventoryItem;
import project.project.entities.ItemType;
import project.project.entities.Player;
import project.project.systems.CraftingSystem;
import project.project.systems.ObjectPlacementSystem;

import java.util.HashMap;
import java.util.Map;

public class InventoryUI {
    private Table inventoryTable;
    private Table hotbarTable;
    private Table craftingTable; // Added crafting table
    private Group hotbarGroup; // For hotbar inside inventory UI
    private Player player;
    private Stage stage;
    private ObjectPlacementSystem placementSystem;

    private BitmapFont font;
    private Texture brownSquareTexture;
    private Texture graySquareTexture;

    private Image backgroundImage; // Track the background image

    private InventoryItem pickedUpItem = null;
    private Image draggingItemImage = null; // Image for the item currently being dragged

    private boolean isVisible = false;
    private Table craftingNavigationTable; // For crafting navigation
    private Texture backgroundTexture;
    private Texture slotTexture;
    private Texture buttonTexture;
    private Texture navigationTexture;

    public InventoryUI(Stage stage, Player player, ObjectPlacementSystem placementSystem) {
        this.stage = stage;
        this.player = player;
        this.placementSystem = placementSystem;

        inventoryTable = new Table();
        hotbarTable = new Table();
        craftingTable = new Table(); // Initialize crafting table
        hotbarGroup = new Group();

        // Initialize font
        font = new BitmapFont();

        // Create textures
        brownSquareTexture = createSquareTexture(58, 58, 0.6f, 0.3f, 0.0f, 0.5f); // Brown texture for inventory slots
        graySquareTexture = createSquareTexture(150, 50, 0.3f, 0.3f, 0.3f, 0.5f); // Gray texture for crafting buttons

        font = new BitmapFont();

        // Textures for the UI
        backgroundTexture = createTexture(600, 400, new Color(0.5f, 0.25f, 0.25f, 1)); // Soft red-brown
        slotTexture = createTexture(64, 64, new Color(0.3f, 0.3f, 0.3f, 1)); // Dark gray for slots
        buttonTexture = createTexture(150, 50, new Color(0.6f, 0.4f, 0.2f, 1)); // Soft brown buttons
        navigationTexture = createTexture(50, 50, new Color(0.4f, 0.3f, 0.2f, 1)); // Vertical navigation buttons

        inventoryTable = new Table();
        craftingTable = new Table();
        craftingNavigationTable = new Table(); // Initialize navigation
        hotbarGroup = new Group();

        // Add background
        createBackground();

        populateInventoryUI();
        initializeCraftingNavigationUI(); // Add this line

        // Initialize dragging item image
        draggingItemImage = new Image();
        draggingItemImage.setVisible(false);
        stage.addActor(draggingItemImage);

        // Populate inventory, hotbar, and crafting slots
        populateInventoryUI();
        populateHotbarUI();

        // Initialize positions
        initializeUIPositions(); // Set initial positions of UI elements

        // Add hotbar and inventory to the stage
        stage.addActor(hotbarGroup);
        stage.addActor(inventoryTable);
        stage.addActor(craftingTable); // Add crafting table to the stage
    }

    private Texture createPlaceholderTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void createBackground() {
        if (backgroundImage == null) {
            backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
            backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            backgroundImage.setPosition(0, 0);
            stage.addActor(backgroundImage);
        }
    }

    private Texture createSquareTexture(int width, int height, float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void populateInventoryUI() {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);

        inventoryTable.clear();
        inventoryTable.setPosition(
                (Gdx.graphics.getWidth() - inventoryTable.getWidth()) / 2f,
                (Gdx.graphics.getHeight() - inventoryTable.getHeight()) / 2f
        );

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        inventoryTable.add(new Label("INVENTORY", labelStyle)).colspan(6).pad(10).row(); // 6 columns for inventory

        // Adjust for 6 columns and 5 rows
        for (int row = 0; row < 5; row++) { // 5 rows
            for (int col = 0; col < 6; col++) { // 6 columns
                InventoryItem item = inventory.getItem(row, col);

                Stack slotStack = new Stack();
                Image slotImage = new Image(new TextureRegionDrawable(new TextureRegion(slotTexture)));
                slotStack.add(slotImage);

                if (item != null) {
                    // Add item image
                    Image itemImage = new Image(new TextureRegionDrawable(new TextureRegion(item.getTexture().texture)));
                    itemImage.setSize(48, 48);
                    slotStack.add(itemImage);

                    // Add item count label
                    if (item.getQuantity() > 1) {
                        labelStyle.font = font;

                        Label countLabel = new Label(String.valueOf(item.getQuantity()), labelStyle);
                        countLabel.setFontScale(1.0f);
                        countLabel.setAlignment(Align.bottomRight);
                        slotStack.add(countLabel);
                    }
                }

                final int finalRow = row;
                final int finalCol = col;

                slotStack.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        handleInventoryClick(finalRow, finalCol, event);
                    }
                });

                inventoryTable.add(slotStack).size(64, 64).pad(5); // Add to the 6-column table
            }
            inventoryTable.row();
        }
    }

    private void populateCraftingUI(String category) {
        CraftingComponent crafting = player.getComponent(CraftingComponent.class);

        craftingTable.clear();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        craftingTable.add(new Label("Crafting Menu - " + category, labelStyle)).colspan(2).pad(10).row();

        // Filter recipes based on the selected category
        Map<String, Map<String, Integer>> filteredRecipes = getRecipesForCategory(category);

        for (Map.Entry<String, Map<String, Integer>> recipe : filteredRecipes.entrySet()) {
            String itemName = recipe.getKey();
            Map<String, Integer> requirements = recipe.getValue();

            // Display crafting requirements
            StringBuilder requirementText = new StringBuilder("Requires: ");
            for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
                requirementText.append(entry.getKey()).append(" x").append(entry.getValue()).append(" ");
            }

            Label requirementLabel = new Label(requirementText.toString(), labelStyle);

            // Create button
            TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = font;
            buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));

            TextButton craftButton = new TextButton("Craft " + itemName, buttonStyle);
            craftButton.setDisabled(!new CraftingSystem().canCraft(crafting, player.getComponent(InventoryComponent.class), itemName));

            craftButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    boolean crafted = new CraftingSystem().craftItem(crafting, player.getComponent(InventoryComponent.class), itemName, 1);
                    if (crafted) {
                        System.out.println("Crafted: " + itemName);
                        populateInventoryUI(); // Refresh inventory
                    } else {
                        System.out.println("Insufficient resources for " + itemName);
                    }
                }
            });

            craftingTable.add(craftButton).size(150, 50).pad(5);
            craftingTable.row();
            craftingTable.add(requirementLabel).colspan(2).pad(5);
            craftingTable.row();
        }
    }

    private Map<String, Map<String, Integer>> getRecipesForCategory(String category) {
        CraftingComponent crafting = player.getComponent(CraftingComponent.class);
        Map<String, Map<String, Integer>> allRecipes = crafting.getAllRecipes();

        // Filter recipes by category
        Map<String, Map<String, Integer>> filteredRecipes = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> recipe : allRecipes.entrySet()) {
            if (getItemCategory(recipe.getKey()).equals(category)) {
                filteredRecipes.put(recipe.getKey(), recipe.getValue());
            }
        }
        return filteredRecipes;
    }

    private String getItemCategory(String itemName) {
        // Logic to determine category of the item (e.g., hardcoded mapping or item metadata)
        switch (itemName) {
            case "campfire":
            case "chest":
                return "Building";
            case "sword":
            case "axe":
                return "Weapons";
            case "steak":
            case "healing potion":
                return "Food/Water";
            default:
                return "Tools";
        }
    }

    private void populateCraftingNavigation() {
        craftingNavigationTable.clear();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        craftingNavigationTable.add(new Label("Categories", labelStyle)).pad(10).row();

        String[] categories = {"Food/Water", "Tools", "Building", "Weapons"};

        for (String category : categories) {
            TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = font;
            buttonStyle.up = new TextureRegionDrawable(new TextureRegion(navigationTexture));

            TextButton categoryButton = new TextButton(category, buttonStyle);
            craftingNavigationTable.add(categoryButton).size(200, 50).pad(10).row();

            categoryButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Selected category: " + category);
                    // Logic to filter crafting items by category
                }
            });
        }

        craftingNavigationTable.setSize(250, Gdx.graphics.getHeight() - 200);
        craftingNavigationTable.setPosition(50, (Gdx.graphics.getHeight() / 2f) - (craftingNavigationTable.getHeight() / 2f));
    }

    private void handleInventoryClick(int row, int col, InputEvent event) {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);
        InventoryItem clickedItem = inventory.getItem(row, col);

        if (pickedUpItem == null) {
            if (clickedItem != null) {
                if (event.getButton() == Input.Buttons.RIGHT) {
                    int halfQuantity = clickedItem.getQuantity() / 2;
                    pickedUpItem = new InventoryItem(clickedItem.getName(), halfQuantity, clickedItem.getTexturePath(), clickedItem.getItemType());
                    clickedItem.setQuantity(clickedItem.getQuantity() - halfQuantity);
                    if (clickedItem.getQuantity() == 0) {
                        inventory.clearSlot(row, col);
                    }
                } else {
                    pickedUpItem = clickedItem;
                    inventory.clearSlot(row, col);
                }
                updateDraggingItemImage();
            }
        } else {
            // Placement logic (updated below)
            if (clickedItem != null && clickedItem.getItemType() == ItemType.BUILDING) {
                placeBuilding(clickedItem, row, col);
            } else {
                mergeOrSwapItems(row, col, clickedItem);
            }
        }

        // Don't enable crafting unless explicitly toggled
        updateUI();
    }

    private void mergeOrSwapItems(int row, int col, InventoryItem clickedItem) {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);

        if (clickedItem == null) {
            // If the target slot is empty, place the picked-up item there
            inventory.setItem(row, col, pickedUpItem);
            pickedUpItem = null; // Clear the picked-up item
            updateDraggingItemImage(); // Update the UI
        } else if (clickedItem.isSameType(pickedUpItem)) {
            // If the items are the same type, try to merge them
            int spaceLeft = 64 - clickedItem.getQuantity(); // Assuming a max stack size of 64
            int mergeQuantity = Math.min(spaceLeft, pickedUpItem.getQuantity());

            clickedItem.setQuantity(clickedItem.getQuantity() + mergeQuantity);
            pickedUpItem.setQuantity(pickedUpItem.getQuantity() - mergeQuantity);

            if (pickedUpItem.getQuantity() <= 0) {
                pickedUpItem = null; // Clear the picked-up item if fully merged
            }
            updateDraggingItemImage(); // Update the UI
        } else {
            // If the items are different, swap them
            InventoryItem temp = clickedItem;
            inventory.setItem(row, col, pickedUpItem);
            pickedUpItem = temp;
            updateDraggingItemImage(); // Update the UI
        }

        // Notify the UI of any changes to the inventory
        player.notifyInventoryUpdate();
    }

    private void placeBuilding(InventoryItem buildingItem, int row, int col) {
        Vector2 screenPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 worldPosition = stage.screenToStageCoordinates(screenPosition);

        Vector2 snappedPosition = placementSystem.snapToTile(worldPosition);

        project.project.entities.Character buildingEntity = new project.project.entities.Character(buildingItem.getName());
        buildingEntity.addComponent(new PlacementComponent(true, 1, 1));
        buildingEntity.addComponent(new HitboxComponent(Constants.TILE_WIDTH, Constants.TILE_HEIGHT));

        boolean placed = placementSystem.placeObject(player, buildingEntity, snappedPosition, false);

        if (placed) {
            player.getComponent(InventoryComponent.class).removeItemFromInventory(buildingItem.getName(), 1);
            System.out.println("Placed " + buildingItem.getName() + " at " + snappedPosition);
        } else {
            System.out.println("Failed to place " + buildingItem.getName());
        }
    }


    private void populateHotbarUI() {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);
        hotbarTable.clear();

        float slotSize = 64; // Fixed size for hotbar slots
        float padding = 4;   // Padding between slots

        TextureRegionDrawable brownSquareDrawable = new TextureRegionDrawable(new TextureRegion(brownSquareTexture));

        for (int col = 0; col < 6; col++) { // Assuming hotbar has 6 slots
            InventoryItem item = inventory.getItem(0, col); // Hotbar uses row 0
            Stack slotStack = new Stack();

            // Add slot background
            Image slotFrame = new Image(brownSquareDrawable);
            slotStack.add(slotFrame);

            if (item != null) {
                // Add item image
                Image itemImage = new Image(new TextureRegionDrawable(new TextureRegion(item.getTexture().texture)));
                itemImage.setSize(48, 48);
                slotStack.add(itemImage);

                // Add item count label
                if (item.getQuantity() > 1) {
                    Label.LabelStyle labelStyle = new Label.LabelStyle();
                    labelStyle.font = font;

                    Label countLabel = new Label(String.valueOf(item.getQuantity()), labelStyle);
                    countLabel.setFontScale(1.0f);
                    countLabel.setAlignment(Align.bottomRight);
                    slotStack.add(countLabel);
                }
            }

            hotbarTable.add(slotStack).size(slotSize, slotSize).pad(padding); // Add slot to table
        }

        hotbarTable.setSize((slotSize + padding) * 6, slotSize); // Assuming 6 columns
        hotbarTable.setPosition(
                (Gdx.graphics.getWidth() - hotbarTable.getWidth()) / 2f, // Center horizontally
                20 // Slightly above the bottom edge
        );

        hotbarGroup.clear(); // Remove previous elements
        hotbarGroup.addActor(hotbarTable); // Add updated hotbar to group
    }

    private void moveToFirstAvailable(InventoryItem item, int originalRow, int originalCol) {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);

        // Check all rows and columns for the first available slot
        for (int row = 0; row < inventory.getRows(); row++) {
            for (int col = 0; col < inventory.getCols(); col++) {
                InventoryItem existingItem = inventory.getItem(row, col);

                if (existingItem == null) {
                    inventory.setItem(row, col, item);
                    inventory.clearSlot(originalRow, originalCol);
                    return;
                } else if (existingItem.isSameType(item) && existingItem.getQuantity() < 64) {
                    int spaceLeft = 64 - existingItem.getQuantity();
                    int toTransfer = Math.min(spaceLeft, item.getQuantity());
                    existingItem.setQuantity(existingItem.getQuantity() + toTransfer);
                    item.setQuantity(item.getQuantity() - toTransfer);

                    if (item.getQuantity() == 0) {
                        inventory.clearSlot(originalRow, originalCol);
                        return;
                    }
                }
            }
        }
    }

    private void updateDraggingItemImage() {
        if (pickedUpItem != null) {
            TextureComponent itemTexture = pickedUpItem.getTexture();
            draggingItemImage.setDrawable(new TextureRegionDrawable(new TextureRegion(itemTexture.texture)));
            draggingItemImage.setSize(48, 48); // Adjust size of the dragged item
            draggingItemImage.setVisible(true);

            // Dynamically follow the mouse cursor
            draggingItemImage.setPosition(Gdx.input.getX() - (draggingItemImage.getWidth() / 2f),
                    Gdx.graphics.getHeight() - Gdx.input.getY() - (draggingItemImage.getHeight() / 2f));
        } else {
            draggingItemImage.setVisible(false);
        }
    }


    public void setVisible(boolean visible) {
        isVisible = visible;

        if (visible) {
            initializeUIPositions(); // Ensure elements are in the correct positions

            if (!stage.getActors().contains(backgroundImage, true)) {
                stage.addActor(backgroundImage); // Add background first
            }
            stage.addActor(inventoryTable);       // Add inventory table
            stage.addActor(craftingNavigationTable);
            stage.addActor(craftingTable);
            stage.addActor(hotbarGroup);          // Add hotbar group

            inventoryTable.setVisible(true);
            craftingNavigationTable.setVisible(true);
            craftingTable.setVisible(true);
            hotbarGroup.setVisible(true);
        } else {
            // Hide the UI components
            inventoryTable.setVisible(false);
            craftingNavigationTable.setVisible(false);
            craftingTable.setVisible(false);
            hotbarGroup.setVisible(false);

            // Remove background
            if (backgroundImage != null) {
                backgroundImage.remove();
            }
        }
    }

    public void updateUI() {
        if (!isVisible) return;

        // Only update the contents of the UI
        populateInventoryUI();
        populateHotbarUI();

        if (craftingNavigationTable.isVisible()) {
            populateCraftingUI("Building"); // Default to "Building" category
        }

        // Reapply correct positions
        initializeUIPositions();
    }

    private void initializeCraftingNavigationUI() {
        craftingNavigationTable.clear();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        craftingNavigationTable.add(new Label("Categories", labelStyle)).pad(10).row();

        String[] categories = {"Food/Water", "Tools", "Building", "Weapons"};

        for (String category : categories) {
            TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = font;
            buttonStyle.up = new TextureRegionDrawable(new TextureRegion(navigationTexture));

            TextButton categoryButton = new TextButton(category, buttonStyle);
            craftingNavigationTable.add(categoryButton).size(200, 50).pad(10).row();

            categoryButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    populateCraftingUI(category); // Populate recipes for the selected category
                    craftingTable.setVisible(true); // Ensure crafting table becomes visible
                }
            });
        }

        craftingNavigationTable.setSize(250, Gdx.graphics.getHeight() - 200);
        craftingNavigationTable.setPosition(50, (Gdx.graphics.getHeight() / 2f) - (craftingNavigationTable.getHeight() / 2f));
        craftingNavigationTable.setVisible(false); // Start hidden
        stage.addActor(craftingNavigationTable);
    }

    private void initializeUIPositions() {
        // Set inventory table position
        inventoryTable.setPosition(
                (Gdx.graphics.getWidth() - inventoryTable.getWidth()) / 2f,
                (Gdx.graphics.getHeight() - inventoryTable.getHeight()) / 2f
        );

        // Set crafting table position
        craftingTable.setPosition(
                (Gdx.graphics.getWidth() / 2f) + 200,
                (Gdx.graphics.getHeight() / 2f) - (craftingTable.getHeight() / 2f)
        );

        // Set crafting navigation table position
        craftingNavigationTable.setPosition(
                50,
                (Gdx.graphics.getHeight() / 2f) - (craftingNavigationTable.getHeight() / 2f)
        );

        // Set hotbar group position
        hotbarGroup.setPosition(
                (Gdx.graphics.getWidth() - hotbarGroup.getWidth()) / 2f,
                20
        );

        // Set background position and size
        if (backgroundImage != null) {
            backgroundImage.setPosition(0, 0);
            backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    public void toggleCraftingNavigation(boolean visible) {
        craftingNavigationTable.setVisible(visible);
        craftingTable.setVisible(visible);
    }

    public void resize(int width, int height) {
        // Update inventory table position
        inventoryTable.setPosition(
                (Gdx.graphics.getWidth() - inventoryTable.getWidth()) / 2f,
                (Gdx.graphics.getHeight() - inventoryTable.getHeight()) / 2f
        );

        // Update crafting navigation
        craftingNavigationTable.setPosition(50, (height / 2f) - (craftingNavigationTable.getHeight() / 2f));

        // Update crafting table
        craftingTable.setPosition((width / 2f) + 200, height / 2f);

        // Update hotbar
        hotbarTable.setPosition((width - hotbarTable.getWidth()) / 2f, 20);
        initializeUIPositions();
    }
}
