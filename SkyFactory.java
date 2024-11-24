package rush;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SkyFactory implements Listener {
   private Main plugin;
   private static Constructor<?> packetPlayOutRespawn;
   private static Method getHandle;
   private static Field playerConnection;
   private static Method sendPacket;
   private static Field normal;
   private Map<String, Environment> worldEnvironments = new HashMap();

   static {
      try {
         packetPlayOutRespawn = getMCClass("PacketPlayOutRespawn").getConstructor(Integer.TYPE, getMCClass("EnumDifficulty"), getMCClass("WorldType"), getMCClass("EnumGamemode"));
         getHandle = getCraftClass("entity.CraftPlayer").getMethod("getHandle");
         playerConnection = getMCClass("EntityPlayer").getDeclaredField("playerConnection");
         sendPacket = getMCClass("PlayerConnection").getMethod("sendPacket", getMCClass("Packet"));
         normal = getMCClass("WorldType").getDeclaredField("NORMAL");
      } catch (Exception var1) {
         var1.printStackTrace();
      }

   }

   public SkyFactory(Main plugin) {
      this.plugin = plugin;
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
   }

   private static Class<?> getMCClass(String name) throws ClassNotFoundException {
      String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
      String className = "net.minecraft.server." + version + name;
      return Class.forName(className);
   }

   private static Class<?> getCraftClass(String name) throws ClassNotFoundException {
      String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
      String className = "org.bukkit.craftbukkit." + version + name;
      return Class.forName(className);
   }

   public void setDimension(World w, Environment env) {
      this.worldEnvironments.put(w.getName(), env);
   }

   @EventHandler
   private void onJoin(PlayerJoinEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ClassNotFoundException {
      Player p = event.getPlayer();
      if (this.worldEnvironments.containsKey(p.getWorld().getName())) {
         Object nms_entity = getHandle.invoke(p);
         Object nms_connection = playerConnection.get(nms_entity);
         sendPacket.invoke(nms_connection, this.getPacket(p));
      }

   }

   @EventHandler
   private void onRespawn(final PlayerRespawnEvent event) {
      (new BukkitRunnable() {
         public void run() {
            try {
               Player p = event.getPlayer();
               if (SkyFactory.this.worldEnvironments.containsKey(p.getWorld().getName())) {
                  Object nms_entity = SkyFactory.getHandle.invoke(p);
                  Object nms_connection = SkyFactory.playerConnection.get(nms_entity);
                  SkyFactory.sendPacket.invoke(nms_connection, SkyFactory.this.getPacket(p));
               }
            } catch (Exception var4) {
               var4.printStackTrace();
            }

         }
      }).runTaskLater(this.plugin, 1L);
   }

   private Object getPacket(Player p) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
      World w = p.getWorld();
      return packetPlayOutRespawn.newInstance(this.getID((Environment)this.worldEnvironments.get(w.getName())), this.getDifficulty(w), this.getLevel(w), this.getGameMode(p));
   }

   private int getID(Environment env) {
      if (env == Environment.NETHER) {
         return -1;
      } else if (env == Environment.NORMAL) {
         return 0;
      } else {
         return env == Environment.THE_END ? 1 : -1;
      }
   }

   private Object getDifficulty(World w) throws ClassNotFoundException {
      Object[] var5;
      int var4 = (var5 = getMCClass("EnumDifficulty").getEnumConstants()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Object dif = var5[var3];
         if (dif.toString().equalsIgnoreCase(w.getDifficulty().toString())) {
            return dif;
         }
      }

      return null;
   }

   private Object getGameMode(Player p) throws ClassNotFoundException {
      Object[] var5;
      int var4 = (var5 = getMCClass("EnumGamemode").getEnumConstants()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Object dif = var5[var3];
         if (dif.toString().equalsIgnoreCase(p.getGameMode().toString())) {
            return dif;
         }
      }

      return null;
   }

   private Object getLevel(World w) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
      return normal.get((Object)null);
   }
}
