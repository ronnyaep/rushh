package rush;

import net.minecraft.server.v1_7_R4.EnumClientCommand;
import net.minecraft.server.v1_7_R4.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
   private final Main plugin;

   public Events(Main plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onPing(ServerListPingEvent event) {
      event.setMotd(this.plugin.motd);
   }

   @EventHandler
   public void onLogin(PlayerLoginEvent event) {
      if (this.plugin.inGame) {
         if (event.getPlayer().isOp() && event.getPlayer().hasPermission("rush.admin")) {
            event.allow();
         } else {
            event.disallow(Result.KICK_OTHER, this.plugin.handler.getCaption("CurrentGame"));
         }
      }

   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      Player p = event.getPlayer();
      Action var10000 = event.getAction();
      event.getAction();
      if (var10000 != Action.RIGHT_CLICK_AIR) {
         var10000 = event.getAction();
         event.getAction();
         if (var10000 != Action.RIGHT_CLICK_BLOCK) {
            return;
         }
      }

      if (!this.plugin.inGame && p.getItemInHand().getType().equals(Material.WOOL)) {
         if (p.getItemInHand().getData().getData() == 11) {
            if (!this.plugin.blue.hasPlayer(p)) {
               if (Bukkit.getOnlinePlayers().length / 2 > this.plugin.blue.getSize()) {
                  this.plugin.CheckTeam(p);
                  this.plugin.bluePlayers.setScore(this.plugin.bluePlayers.getScore() + 1);
                  this.plugin.blue.addPlayer(p);
                  p.sendMessage(this.plugin.prefix + ChatColor.BLUE + this.plugin.handler.getCaption("JoinBlueTeam"));
               } else {
                  p.sendMessage(this.plugin.prefix + ChatColor.RED + this.plugin.handler.getCaption("NotEnoughPlayers"));
               }
            } else {
               p.sendMessage(this.plugin.prefix + ChatColor.RED + this.plugin.handler.getCaption("AlreadyInThisTeam"));
            }
         }

         if (p.getItemInHand().getData().getData() == 14) {
            if (!this.plugin.red.hasPlayer(p)) {
               if (Bukkit.getOnlinePlayers().length / 2 > this.plugin.red.getSize()) {
                  this.plugin.CheckTeam(p);
                  this.plugin.redPlayers.setScore(this.plugin.redPlayers.getScore() + 1);
                  this.plugin.red.addPlayer(p);
                  p.sendMessage(this.plugin.prefix + ChatColor.RED + this.plugin.handler.getCaption("JoinRedTeam"));
               } else {
                  p.sendMessage(this.plugin.prefix + ChatColor.RED + this.plugin.handler.getCaption("NotEnoughPlayers"));
               }
            } else {
               p.sendMessage(this.plugin.prefix + ChatColor.RED + this.plugin.handler.getCaption("AlreadyInThisTeam"));
            }
         }
      }

   }

   @EventHandler
   public void onConnect(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      player.setScoreboard(this.plugin.board);
      if (!this.plugin.inGame) {
         player.setGameMode(GameMode.ADVENTURE);
         player.getInventory().clear();
         player.updateInventory();
         this.plugin.CheckTeam(player);
         player.getInventory().setItem(0, this.plugin.createItem(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()), "" + ChatColor.RED + ChatColor.BOLD + this.plugin.handler.getCaption("redPlayers"), new String[]{"" + ChatColor.RED}));
         player.getInventory().setItem(1, this.plugin.createItem(new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getData()), "" + ChatColor.BLUE + ChatColor.BOLD + this.plugin.handler.getCaption("bluePlayers"), new String[]{"" + ChatColor.BLUE}));
         event.setJoinMessage(this.plugin.handler.getCaption("JoinMessage").replace("%player%", player.getName()) + " (" + Bukkit.getOnlinePlayers().length + "/" + Bukkit.getMaxPlayers() + ")");
         player.teleport(new Location(player.getWorld(), (double)this.plugin.getConfig().getInt("lobby.x"), (double)this.plugin.getConfig().getInt("lobby.y"), (double)this.plugin.getConfig().getInt("lobby.z")));
      }

      if (this.plugin.inGame) {
         event.setJoinMessage((String)null);
         player.teleport(new Location(player.getWorld(), (double)this.plugin.getConfig().getInt("lobby.x"), (double)this.plugin.getConfig().getInt("lobby.y"), (double)this.plugin.getConfig().getInt("lobby.z")));
      }

   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      if (!this.plugin.inGame) {
         this.plugin.CheckTeam(player);
         event.setQuitMessage(this.plugin.prefix + this.plugin.handler.getCaption("QuitMessage").replace("%player%", player.getName()));
      }

      if (this.plugin.inGame) {
         if (this.plugin.blue.hasPlayer(player)) {
            this.plugin.CheckTeam(player);
            event.setQuitMessage(this.plugin.prefix + this.plugin.handler.getCaption("QuitMessageBlue").replace("%player%", player.getName()));
         }

         if (this.plugin.red.hasPlayer(player)) {
            this.plugin.CheckTeam(player);
            event.setQuitMessage(this.plugin.prefix + this.plugin.handler.getCaption("QuitMessageRed").replace("%player%", player.getName()));
         }

         if (!this.plugin.blue.hasPlayer(player) && !this.plugin.red.hasPlayer(player)) {
            event.setQuitMessage((String)null);
         }
      }

   }

   @EventHandler
   public void onDeath(PlayerDeathEvent event) {
      final Player player = event.getEntity();
      if (this.plugin.inGame) {
         if (player.getKiller() instanceof Player) {
            Player killer = player.getKiller().getPlayer();
            event.setDeathMessage(this.plugin.prefix + this.plugin.handler.getCaption("KilledBy").replace("%player%", player.getDisplayName()).replace("%killer%", killer.getDisplayName()));
         } else {
            event.setDeathMessage(this.plugin.prefix + this.plugin.handler.getCaption("Died").replace("%player%", player.getDisplayName()));
         }
      } else {
         event.setDeathMessage((String)null);
      }

      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
         public void run() {
            if (player.isDead()) {
               ((CraftPlayer)player).getHandle().playerConnection.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
            }

         }
      });
   }

   @EventHandler
   public void onRespawn(PlayerRespawnEvent event) {
      Player p = event.getPlayer();
      if (event.getPlayer().getBedSpawnLocation() != null) {
         event.getPlayer().teleport(event.getPlayer().getBedSpawnLocation());
         p.sendMessage(ChatColor.GREEN + this.plugin.handler.getCaption("PlayerRespawn"));
      } else if (this.plugin.blue.hasPlayer(p) || this.plugin.red.hasPlayer(p)) {
         this.plugin.CheckTeam(p);
         Bukkit.broadcastMessage(this.plugin.prefix + this.plugin.handler.getCaption("PlayerLost").replace("%player%", p.getDisplayName()));
         if (this.plugin.getConfig().getString("BungeeCord.returnserver").equalsIgnoreCase("none")) {
            p.kickPlayer(this.plugin.handler.getCaption("Lost"));
         } else {
            this.plugin.backToLobby(p);
         }

         p.teleport(new Location(p.getWorld(), 0.0D, 0.0D, 0.0D));
         Player[] var6;
         int var5 = (var6 = Bukkit.getOnlinePlayers()).length;

         for(int var4 = 0; var4 < var5; ++var4) {
            Player online = var6[var4];
            if (online != p && !online.hasPermission("rush.rush")) {
               online.hidePlayer(p);
            }
         }
      }

   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent event) {
      Player p = event.getPlayer();
      if (this.plugin.inGame) {
         Player player;
         int var4;
         int var5;
         Player[] var6;
         if (this.plugin.blue.hasPlayer(p)) {
            if (!event.getMessage().startsWith("@")) {
               var5 = (var6 = Bukkit.getOnlinePlayers()).length;

               for(var4 = 0; var4 < var5; ++var4) {
                  player = var6[var4];
                  if (this.plugin.blue.hasPlayer(player)) {
                     player.sendMessage("[" + ChatColor.BLUE + this.plugin.handler.getCaption("bluePlayers") + ChatColor.RESET + "] " + p.getName() + ": " + event.getMessage());
                     Bukkit.getLogger().info("[" + ChatColor.BLUE + this.plugin.handler.getCaption("bluePlayers") + ChatColor.RESET + "] " + p.getName() + ": " + event.getMessage());
                     event.setCancelled(true);
                  }
               }
            } else {
               event.setFormat("[" + ChatColor.BLUE + this.plugin.handler.getCaption("bluePlayers") + ChatColor.RESET + "] " + p.getName() + ": " + event.getMessage());
            }
         }

         if (this.plugin.red.hasPlayer(p)) {
            if (!event.getMessage().startsWith("@")) {
               var5 = (var6 = Bukkit.getOnlinePlayers()).length;

               for(var4 = 0; var4 < var5; ++var4) {
                  player = var6[var4];
                  if (this.plugin.red.hasPlayer(player)) {
                     player.sendMessage("[" + ChatColor.RED + this.plugin.handler.getCaption("redPlayers") + ChatColor.RESET + "] " + p.getName() + ": " + event.getMessage());
                     Bukkit.getLogger().info("[" + ChatColor.RED + this.plugin.handler.getCaption("redPlayers") + ChatColor.RESET + "] " + p.getName() + ": " + event.getMessage());
                     event.setCancelled(true);
                  }
               }
            } else {
               event.setFormat("[" + ChatColor.RED + this.plugin.handler.getCaption("redPlayers") + ChatColor.RESET + "] " + p.getName() + ": " + event.getMessage());
            }
         }

         if (!this.plugin.red.hasPlayer(p) && !this.plugin.blue.hasPlayer(p)) {
            event.setFormat(ChatColor.GRAY + p.getName() + ": " + event.getMessage());
         }
      } else {
         event.setFormat(ChatColor.GRAY + p.getName() + ": " + event.getMessage());
      }

   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent event) {
      Player player = event.getPlayer();
      Location to = event.getTo();
      int y = to.getBlockY();
      if (y <= 0 && !this.plugin.inGame) {
         player.teleport(new Location(player.getWorld(), (double)this.plugin.getConfig().getInt("lobby.x"), (double)this.plugin.getConfig().getInt("lobby.y"), (double)this.plugin.getConfig().getInt("lobby.z")));
      }

   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent event) {
      if ((!this.plugin.inGame || this.plugin.GameFinish) && (!this.plugin.blue.hasPlayer(event.getPlayer()) || !this.plugin.red.hasPlayer(event.getPlayer()))) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
      if (!this.plugin.inGame || this.plugin.GameFinish) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onDamageByBlock(EntityDamageByBlockEvent event) {
      if (!this.plugin.inGame || this.plugin.GameFinish) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent event) {
      if ((!this.plugin.inGame || this.plugin.GameFinish) && (!this.plugin.blue.hasPlayer(event.getPlayer()) || !this.plugin.red.hasPlayer(event.getPlayer()))) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onFoodChange(FoodLevelChangeEvent event) {
      if (!this.plugin.inGame || this.plugin.GameFinish) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onCreatureSpawn(CreatureSpawnEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onDrop(PlayerDropItemEvent event) {
      if ((!this.plugin.inGame || this.plugin.GameFinish) && (!this.plugin.blue.hasPlayer(event.getPlayer()) || !this.plugin.red.hasPlayer(event.getPlayer()))) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onEntityExplode(EntityExplodeEvent event) {
      if (!this.plugin.inGame || this.plugin.GameFinish) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerPickup(PlayerPickupItemEvent event) {
      if ((!this.plugin.inGame || this.plugin.GameFinish) && (!this.plugin.blue.hasPlayer(event.getPlayer()) || !this.plugin.red.hasPlayer(event.getPlayer()))) {
         event.setCancelled(true);
      }

   }
}
