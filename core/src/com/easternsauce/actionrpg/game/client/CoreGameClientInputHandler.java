package com.easternsauce.actionrpg.game.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.easternsauce.actionrpg.game.command.ActionPerformCommand;
import com.easternsauce.actionrpg.game.command.ChatMessageSendCommand;
import com.easternsauce.actionrpg.model.action.CreatureMoveTowardsTargetAction;
import com.easternsauce.actionrpg.model.action.InventoryWindowToggleAction;
import com.easternsauce.actionrpg.model.action.PotionMenuItemUseAction;
import com.easternsauce.actionrpg.model.action.SkillTryPerformAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.item.EquipmentSlotType;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.hud.inventorywindow.InventoryWindowController;
import com.easternsauce.actionrpg.renderer.hud.inventorywindow.PotionMenuController;
import com.easternsauce.actionrpg.renderer.hud.itempickupmenu.ItemPickupMenuController;
import com.easternsauce.actionrpg.renderer.hud.skillmenu.SkillMenuController;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class CoreGameClientInputHandler {
  private final SkillMenuController skillMenuController = SkillMenuController.of();

  private final InventoryWindowController inventoryWindowController = InventoryWindowController.of();

  private final ItemPickupMenuController pickupMenuController = ItemPickupMenuController.of();

  private final PotionMenuController potionMenuController = PotionMenuController.of();

  private Float menuClickTime = 0f; // TODO: should do it differently

  public void process(CoreGameClient game) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
      handleChatMessageActionInput(game);
    }

    if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
      handleDeleteChatMessageCharacterInput(game);
    } else {
      handleDeleteChatMessageCharacterNoInput(game);
    }

    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      handleExitWindowInput(playerConfig, game);
    }

    if (!game.getChat().getTyping()) {
      if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
        handleActionButtonInput(playerConfig, game);
      }
      if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
        handleActionButtonHoldInput(playerConfig, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
        handleDebugInformationQueryInput(game);
      }
      if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
        handleAttackButtonInput(playerConfig, game);
      }
      if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
        handleAttackButtonHoldInput(playerConfig, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
        handlePerformAbilityInput(playerConfig, 0, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
        handlePerformAbilityInput(playerConfig, 1, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
        handlePerformAbilityInput(playerConfig, 2, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
        handlePerformAbilityInput(playerConfig, 3, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
        handlePerformAbilityInput(playerConfig, 4, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
        handleUsePotionMenuItemInput(0, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
        handleUsePotionMenuItemInput(1, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
        handleUsePotionMenuItemInput(2, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
        handleUsePotionMenuItemInput(3, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
        handleUsePotionMenuItemInput(4, game);
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
        handleInventoryWindowActionInput(game);
      }
      //noinspection StatementWithEmptyBody
      if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
        // TODO: make debug command that spawns enemy in front of player
      }
    }
  }

  private void handleChatMessageActionInput(CoreGameClient game) {
    if (!game.getChat().getTyping()) {
      game.getChat().setTyping(true);
    } else {
      game.getChat().setTyping(false);
      if (!game.getChat().getCurrentMessage().isEmpty()) {
        game.getEndPoint().sendTCP(ChatMessageSendCommand.of(game.getGameState().getThisClientPlayerId().getValue(),
          game.getChat().getCurrentMessage()));

        game.getChat()
          .sendMessage(game.getGameState().getThisClientPlayerId().getValue(), game.getChat().getCurrentMessage(),
            game);

        game.getChat().setCurrentMessage("");
      }
    }
  }

  private void handleDeleteChatMessageCharacterInput(CoreGameClient game) {
    if (game.getChat().getTyping()) {
      if (game.getChat().getHoldingBackspace()) {
        if (!game.getChat().getCurrentMessage().isEmpty() &&
          game.getGameState().getTime() > game.getChat().getHoldBackspaceTime() + 0.3f) {
          game.getChat().setCurrentMessage(
            game.getChat().getCurrentMessage().substring(0, game.getChat().getCurrentMessage().length() - 1));
        }
      } else {
        game.getChat().setHoldingBackspace(true);
        game.getChat().setHoldBackspaceTime(game.getGameState().getTime());
        if (!game.getChat().getCurrentMessage().isEmpty()) {
          game.getChat().setCurrentMessage(
            game.getChat().getCurrentMessage().substring(0, game.getChat().getCurrentMessage().length() - 1));
        }
      }

    }
  }

  private void handleDeleteChatMessageCharacterNoInput(CoreGameClient game) {
    if (game.getChat().getHoldingBackspace() && game.getChat().getTyping()) {
      game.getChat().setHoldingBackspace(false);
    }
  }

  private void handleExitWindowInput(PlayerConfig playerConfig, CoreGameClient game) {
    if (game.getChat().getTyping()) {
      if (!game.getChat().getCurrentMessage().isEmpty()) {
        game.getChat().setCurrentMessage("");
        game.getChat().setTyping(false);
      }
    } else if (playerConfig.getInventoryVisible()) {
      handleInventoryWindowActionInput(game);

    }
  }

  private void handleActionButtonInput(PlayerConfig playerConfig, CoreGameClient game) {
    if (playerConfig != null) {
      if (playerConfig.getInventoryVisible()) {
        inventoryWindowController.performMoveItemClick(game.getEndPoint(), game);
      } else if (!playerConfig.getInventoryVisible() && !playerConfig.getItemPickupMenuLootPiles().isEmpty()) {
        boolean successful = pickupMenuController.performItemPickupMenuClick(game.getEndPoint(), game);
        if (successful) {
          menuClickTime = game.getGameState().getTime();
        }

      } else if (!playerConfig.getInventoryVisible() && playerConfig.getSkillMenuPickerSlotBeingChanged() != null) {
        boolean successful = skillMenuController.performSkillMenuPickerClick(game.getEndPoint(), game);
        if (successful) {
          menuClickTime = game.getGameState().getTime();
        }

      } else {
        boolean successful = skillMenuController.performSkillMenuClick(game.getEndPoint(), game);
        if (successful) {
          menuClickTime = game.getGameState().getTime();
        }

      }
    }
  }

  private void handleActionButtonHoldInput(PlayerConfig playerConfig, CoreGameClient game) {
    if (playerConfig != null && !playerConfig.getInventoryVisible()) {
      Vector2 mousePos = game.getMousePositionRetriever().mousePosRelativeToCenter(game);

      Creature player = game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());

      if (player != null) {
        if (player.getParams().getMovementParams().getMovementActionsPerSecondLimiterTimer().getTime() >
          Constants.MOVEMENT_COMMAND_COOLDOWN && game.getGameState().getTime() > menuClickTime + 0.1f) {
          game.getEndPoint().sendTCP(ActionPerformCommand.of(
            CreatureMoveTowardsTargetAction.of(game.getGameState().getThisClientPlayerId(), mousePos)));
        }
      }
    }
  }

  private void handleDebugInformationQueryInput(CoreGameClient game) {
    CreatureId creatureId = game.getActiveCreatures().keySet().stream()
      .filter(cId -> cId.getValue().startsWith("kamil")).collect(Collectors.toList()).get(0);
    Vector2 pos = game.getGameState().accessCreatures().getCreature(creatureId).getParams().getPos();
    System.out.println("Vector2.of(" + pos.getX() + "f, " + pos.getY() + "f),");
  }

  private void handleAttackButtonInput(PlayerConfig playerConfig, CoreGameClient game) {
    potionMenuController.performUseItemClick(game.getEndPoint(), game);

    if (playerConfig.getInventoryVisible()) {
      inventoryWindowController.performUseItemClick(game.getEndPoint(), game);
    }
  }

  private void handleAttackButtonHoldInput(PlayerConfig playerConfig, CoreGameClient game) {
    if (!playerConfig.getInventoryVisible()) {

      Creature player = game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());

      Vector2 dirVector = game.getMousePositionRetriever().mousePosRelativeToCenter(game);

      float weaponDamage;
      SkillType attackSkill;

      if (player.getParams().getEquipmentItems().containsKey(EquipmentSlotType.PRIMARY_WEAPON.ordinal())) {
        Item weaponItem = player.getParams().getEquipmentItems().get(EquipmentSlotType.PRIMARY_WEAPON.ordinal());

        attackSkill = weaponItem.getTemplate().getAttackSkill();
        weaponDamage = weaponItem.getDamage();
      } else {
        attackSkill = SkillType.PUNCH;
        weaponDamage = 0f; // weapon damage doesn't apply
      }
      game.getEndPoint().sendTCP(ActionPerformCommand.of(
        SkillTryPerformAction.of(game.getGameState().getThisClientPlayerId(), attackSkill, player.getParams().getPos(),
          dirVector, weaponDamage)));
    }
  }

  private void handlePerformAbilityInput(PlayerConfig playerConfig, int abilitySequenceNumber, CoreGameClient game) {
    Creature player = game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());

    Vector2 dirVector = game.getMousePositionRetriever().mousePosRelativeToCenter(game);

    if (playerConfig.getSkillMenuSlots().containsKey(abilitySequenceNumber)) {

      game.getEndPoint().sendTCP(ActionPerformCommand.of(
        SkillTryPerformAction.of(game.getGameState().getThisClientPlayerId(),
          playerConfig.getSkillMenuSlots().get(abilitySequenceNumber), player.getParams().getPos(), dirVector)));
    }
  }

  private void handleUsePotionMenuItemInput(int potionMenuItemIndex, CoreGameClient game) {
    game.getEndPoint().sendTCP(ActionPerformCommand.of(
      PotionMenuItemUseAction.of(game.getGameState().getThisClientPlayerId(), potionMenuItemIndex)));

  }

  private void handleInventoryWindowActionInput(CoreGameClient game) {
    game.getEndPoint()
      .sendTCP(ActionPerformCommand.of(InventoryWindowToggleAction.of(game.getGameState().getThisClientPlayerId())));
  }
}
