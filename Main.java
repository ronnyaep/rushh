package rush;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Main extends JavaPlugin {
   private SkyFactory skyFactory;
   Language handler = new Language(this);
   public Location lobbyLocation;
   private Logger log = Logger.getLogger("Minecraft");
   boolean ok = false;
   boolean inGame = false;
   boolean GameFinish = false;
   String prefix;
   String motd;
   ScoreboardManager manager;
   Scoreboard board;
   Team blue;
   Team red;
   Objective players;
   Objective kills;
   Objective health;
   Score bluePlayers;
   Score redPlayers;
   World world;

   public Main() {
      this.prefix = "[" + ChatColor.RED + "Rush" + ChatColor.RESET + "] ";
   }

   public void onEnable() {
      this.manager = this.getServer().getScoreboardManager();
      this.board = this.manager.getNewScoreboard();
      this.blue = this.board.registerNewTeam("blue");
      this.red = this.board.registerNewTeam("red");
      this.players = this.board.registerNewObjective("Players", "dummy");
      this.kills = this.board.registerNewObjective("Kills", "playerKillCount");
      this.health = this.board.registerNewObjective("health", "health");
      this.blue.setPrefix("" + ChatColor.BLUE);
      this.red.setPrefix("" + ChatColor.RED);
      this.players.setDisplaySlot(DisplaySlot.SIDEBAR);
      this.players.setDisplayName(ChatColor.GREEN + this.handler.getCaption("PlayersScoreboard"));
      this.health.setDisplaySlot(DisplaySlot.BELOW_NAME);
      this.health.setDisplayName(ChatColor.RED + "❤");
      this.kills.setDisplaySlot(DisplaySlot.PLAYER_LIST);
      this.bluePlayers = this.players.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + this.handler.getCaption("bluePlayers") + " : "));
      this.redPlayers = this.players.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + this.handler.getCaption("redPlayers") + " : "));
      this.bluePlayers.setScore(0);
      this.redPlayers.setScore(0);
      this.blue.setAllowFriendlyFire(false);
      this.red.setAllowFriendlyFire(false);
      this.getServer().getPluginManager().registerEvents(new Events(this), this);
      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
      this.motd = "&a" + this.handler.getCaption("MotdPending");
      BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
      scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
         public void run() {
            if (Bukkit.getOnlinePlayers().length >= 2 && !Main.this.inGame && !Main.this.ok) {
               Main.this.ok = true;
               Bukkit.broadcastMessage(Main.this.prefix + ChatColor.AQUA + Main.this.handler.getCaption("EnoughPlayers"));
               Bukkit.broadcastMessage(Main.this.prefix + ChatColor.AQUA + Main.this.handler.getCaption("GameMinutes"));
               Main.this.timer(Main.this.getConfig().getInt("sec"));
            }

         }
      }, 0L, 20L);
      this.skyFactory = new SkyFactory(this);
      this.skyFactory.setDimension(this.getServer().getWorld("world"), Environment.THE_END);
   }

   public void timer(int sec) {
      BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
      scheduler.scheduleSyncRepeatingTask(this, new Runnable(sec) {
         int seco;

         {
            this.seco = var2;
         }

         public void run() {
            Player p;
            int var2;
            int var3;
            Player[] var4;
            if (this.seco >= 0) {
               var3 = (var4 = Bukkit.getOnlinePlayers()).length;

               for(var2 = 0; var2 < var3; ++var2) {
                  p = var4[var2];
                  p.setLevel(this.seco);
               }
            }

            if (this.seco == 60) {
               Bukkit.broadcastMessage(Main.this.prefix + ChatColor.AQUA + this.seco + " " + Main.this.handler.getCaption("SecondBeforeGame"));
            }

            if (this.seco == 30) {
               Bukkit.broadcastMessage(Main.this.prefix + ChatColor.AQUA + this.seco + " " + Main.this.handler.getCaption("SecondBeforeGame"));
            }

            if (this.seco < 5 && !Main.this.inGame) {
               if (this.seco == 0) {
                  var3 = (var4 = Bukkit.getOnlinePlayers()).length;

                  for(var2 = 0; var2 < var3; ++var2) {
                     p = var4[var2];
                     p.playSound(p.getLocation(), Sound.LEVEL_UP, 100.0F, 1.0F);
                  }

                  Bukkit.broadcastMessage(Main.this.prefix + ChatColor.AQUA + Main.this.handler.getCaption("StartsGame"));
                  Main.this.startGame();
               } else {
                  if (this.seco > -1 && this.seco != 1) {
                     var3 = (var4 = Bukkit.getOnlinePlayers()).length;

                     for(var2 = 0; var2 < var3; ++var2) {
                        p = var4[var2];
                        p.playSound(p.getLocation(), Sound.CLICK, 100.0F, 1.0F);
                     }

                     Bukkit.broadcastMessage(Main.this.prefix + ChatColor.AQUA + this.seco + " " + Main.this.handler.getCaption("SecondBeforeGame"));
                  }

                  if (this.seco == 1) {
                     var3 = (var4 = Bukkit.getOnlinePlayers()).length;

                     for(var2 = 0; var2 < var3; ++var2) {
                        p = var4[var2];
                        p.playSound(p.getLocation(), Sound.CLICK, 100.0F, 1.0F);
                     }

                     Bukkit.broadcastMessage(Main.this.prefix + ChatColor.AQUA + this.seco + " " + Main.this.handler.getCaption("SecondBeforeGame"));
                  }
               }
            }

            --this.seco;
         }
      }, 0L, 20L);
   }

   public void startGame() {
      Bukkit.getWorld("world").setTime(20000L);
      this.inGame = true;
      this.motd = "&c" + this.handler.getCaption("MotdInGame");
      Player[] var4;
      int var3 = (var4 = Bukkit.getOnlinePlayers()).length;

      for(int var2 = 0; var2 < var3; ++var2) {
         Player p = var4[var2];
         p.setGameMode(GameMode.ADVENTURE);
         p.getInventory().clear();
         p.updateInventory();
         if (!this.blue.hasPlayer(p) && !this.red.hasPlayer(p)) {
            if (this.blue.getSize() < this.red.getSize()) {
               this.bluePlayers.setScore(this.bluePlayers.getScore() + 1);
               this.blue.addPlayer(p);
               p.teleport(new Location(p.getWorld(), (double)this.getConfig().getInt("blue.x"), (double)this.getConfig().getInt("blue.y"), (double)this.getConfig().getInt("blue.z")));
            } else {
               this.redPlayers.setScore(this.redPlayers.getScore() + 1);
               this.red.addPlayer(p);
               p.teleport(new Location(p.getWorld(), (double)this.getConfig().getInt("red.x"), (double)this.getConfig().getInt("red.y"), (double)this.getConfig().getInt("red.z")));
            }
         }

         if (this.blue.hasPlayer(p) && !this.red.hasPlayer(p)) {
            p.teleport(new Location(p.getWorld(), (double)this.getConfig().getInt("blue.x"), (double)this.getConfig().getInt("blue.y"), (double)this.getConfig().getInt("blue.z")));
         }

         if (this.red.hasPlayer(p) && !this.blue.hasPlayer(p)) {
            p.teleport(new Location(p.getWorld(), (double)this.getConfig().getInt("red.x"), (double)this.getConfig().getInt("red.y"), (double)this.getConfig().getInt("red.z")));
         }
      }

      BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
      scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
         public void run() {
            final Player p;
            int var2;
            int var3;
            Player[] var4;
            if (Main.this.blue.getSize() == 0 && Main.this.inGame && !Main.this.GameFinish) {
               Main.this.GameFinish = true;
               var3 = (var4 = Bukkit.getOnlinePlayers()).length;

               for(var2 = 0; var2 < var3; ++var2) {
                  p = var4[var2];
                  p.setAllowFlight(true);
                  p.setFlying(true);
                  p.sendMessage(Main.this.prefix + ChatColor.RED + Main.this.handler.getCaption("RedWin"));
                  Main.this.motd = "&c" + Main.this.handler.getCaption("Win");
                  if (Main.this.getConfig().getString("BungeeCord.returnserver").equalsIgnoreCase("none")) {
                     (new BukkitRunnable() {
                        public void run() {
                           p.kickPlayer(Main.this.handler.getCaption("Win"));
                        }
                     }).runTaskLater(Main.this, 100L);
                  } else {
                     (new BukkitRunnable() {
                        public void run() {
                           Main.this.backToLobby(p);
                        }
                     }).runTaskLater(Main.this, 200L);
                  }
               }

               (new BukkitRunnable() {
                  public void run() {
                     Bukkit.shutdown();
                  }
               }).runTaskLater(Main.this, 300L);
            } else if (Main.this.red.getSize() == 0 && Main.this.inGame && !Main.this.GameFinish) {
               Main.this.GameFinish = true;
               var3 = (var4 = Bukkit.getOnlinePlayers()).length;

               for(var2 = 0; var2 < var3; ++var2) {
                  p = var4[var2];
                  p.setAllowFlight(true);
                  p.setFlying(true);
                  Main.this.motd = "&9" + Main.this.handler.getCaption("Win");
                  p.sendMessage(Main.this.prefix + ChatColor.BLUE + Main.this.handler.getCaption("BlueWin"));
                  if (Main.this.getConfig().getString("BungeeCord.returnserver").equalsIgnoreCase("none")) {
                     (new BukkitRunnable() {
                        public void run() {
                           p.kickPlayer(Main.this.handler.getCaption("Win"));
                        }
                     }).runTaskLater(Main.this, 100L);
                  } else {
                     (new BukkitRunnable() {
                        public void run() {
                           Main.this.backToLobby(p);
                        }
                     }).runTaskLater(Main.this, 200L);
                  }

                  (new BukkitRunnable() {
                     public void run() {
                        Bukkit.shutdown();
                     }
                  }).runTaskLater(Main.this, 200L);
               }
            }

         }
      }, 0L, 20L);
   }

   private boolean delete(File file) {
      if (file.isDirectory()) {
         File[] var5;
         int var4 = (var5 = file.listFiles()).length;

         for(int var3 = 0; var3 < var4; ++var3) {
            File subfile = var5[var3];
            if (!this.delete(subfile)) {
               return false;
            }
         }
      }

      return file.delete();
   }

   public void deleteWorlds(String worldName) {
      File target = new File(this.getServer().getWorldContainer(), worldName);
      if (!target.exists()) {
         this.log.info("Could not load world \"" + worldName + "\" with a random seed: no such world " + "exists in the server directory!");
      } else {
         if (target.isDirectory() && !this.delete(target)) {
            this.log.info("Failed to delete world \"" + worldName + "\", perhaps the folder is locked?");
         }

      }
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (command.getName().equalsIgnoreCase("rush")) {
         if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You have to be a player.");
            return true;
         }

         Player player = (Player)sender;
         if (args.length != 0) {
            String sub = args[0];
            if (sub.equalsIgnoreCase("help")) {
               player.sendMessage(ChatColor.GREEN + "Rush Help :");
               player.sendMessage("/rush setlobby" + ChatColor.YELLOW + " - Set the lobby");
               player.sendMessage("/rush setspawn [color]" + ChatColor.YELLOW + " - Set the spawn of team [color]");
               player.sendMessage("/rush setreturnserver [server]" + ChatColor.YELLOW + " - Set BungeeCord returnserver");
            } else if (sub.equalsIgnoreCase("setreturnserver")) {
               if (args[1] != null) {
                  this.getConfig().set("BungeeCord.returnserver", args[1]);
                  this.saveConfig();
                  player.sendMessage(this.prefix + ChatColor.RED + "You defined successfully the server of return");
               } else {
                  player.sendMessage(this.prefix + ChatColor.RED + "You have to inform an argument");
               }
            } else if (sub.equalsIgnoreCase("setlobby")) {
               this.lobbyLocation = player.getLocation();
               player.sendMessage(ChatColor.GREEN + "You have defined the lobby successfully.");
               this.getConfig().set("lobby.x", player.getLocation().getX());
               this.getConfig().set("lobby.y", player.getLocation().getY());
               this.getConfig().set("lobby.z", player.getLocation().getZ());
               this.saveConfig();
            } else if (sub.equalsIgnoreCase("setspawn")) {
               if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue")) {
                  player.sendMessage(ChatColor.RED + "The color " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " does not exist.");
               } else {
                  player.sendMessage(ChatColor.GREEN + "You have successfully defined the spawn of the " + args[1] + "team");
                  this.getConfig().set(args[1] + ".x", player.getLocation().getX());
                  this.getConfig().set(args[1] + ".y", player.getLocation().getY());
                  this.getConfig().set(args[1] + ".z", player.getLocation().getZ());
                  this.saveConfig();
               }
            } else {
               sender.sendMessage(ChatColor.RED + "Bad arguments or nonexistent command. Use " + ChatColor.DARK_RED + "/rush help" + ChatColor.RED + ".");
            }

            return true;
         }

         player.sendMessage(ChatColor.YELLOW + "Plugin Rush v" + this.getDescription().getVersion() + " by Dragoh.");
      }

      return false;
   }

   public void onLoad() {
      try {
         File worldContainer = this.getServer().getWorldContainer();
         File worldFolder = new File(worldContainer, "world");
         this.deleteWorlds("world");
         FileUtils.copyFolder(new File(worldContainer, "rush"), worldFolder);
         this.getLogger().info("Worl deleted successful !");
      } catch (Throwable var3) {
         throw var3;
      }
   }

   public void CheckTeam(Player p) {
      if (this.blue.hasPlayer(p)) {
         this.bluePlayers.setScore(this.bluePlayers.getScore() - 1);
         this.blue.removePlayer(p);
      }

      if (this.red.hasPlayer(p)) {
         this.redPlayers.setScore(this.redPlayers.getScore() - 1);
         this.red.removePlayer(p);
      }

   }

   public void backToLobby(Player p) {
      ByteArrayOutputStream b = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(b);

      try {
         out.writeUTF("Connect");
         out.writeUTF(this.getConfig().getString("BungeeCord.returnserver"));
      } catch (IOException var5) {
         var5.printStackTrace();
      }

      p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
   }

   public ItemStack createItem(ItemStack item, String name, String[] lore) {
      ItemMeta im = item.getItemMeta();
      im.setDisplayName(name);
      im.setLore(Arrays.asList(lore));
      item.setItemMeta(im);
      return item;
   }
}
