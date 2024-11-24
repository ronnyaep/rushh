package rush;

import java.io.File;
import java.io.InputStream;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Language {
   private Plugin plugin = null;
   private File langFile = null;
   private FileConfiguration lang = null;
   private File configFile = null;
   private FileConfiguration config = null;

   public Language(Plugin plugin) {
      this.plugin = plugin;
      this.langFile = new File(this.plugin.getDataFolder(), "lang.yml");
      this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
      this.reloadLang();
      this.saveDefaultLang();
      this.reloadConfig();
      this.saveDefaultConfig();
   }

   public void reloadLang() {
      this.lang = YamlConfiguration.loadConfiguration(this.langFile);
      InputStream defaultConfigStream = this.plugin.getResource("lang.yml");
      if (defaultConfigStream != null) {
         YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
         this.lang.setDefaults(defaultConfig);
      }

   }

   public void saveDefaultLang() {
      if (!this.langFile.exists()) {
         this.plugin.saveResource("lang.yml", false);
      }

   }

   public void reloadConfig() {
      this.config = YamlConfiguration.loadConfiguration(this.configFile);
      InputStream defaultConfigStream = this.plugin.getResource("config.yml");
      if (defaultConfigStream != null) {
         YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
         this.config.setDefaults(defaultConfig);
      }

   }

   public void saveDefaultConfig() {
      if (!this.configFile.exists()) {
         this.plugin.saveResource("config.yml", false);
      }

   }

   public String getCaption(String name) {
      return this.getCaption(name, false);
   }

   public String getCaption(String name, boolean color) {
      String caption = this.lang.getString(name);
      if (caption == null) {
         this.plugin.getLogger().warning("Missing caption: " + name);
         caption = "&c[missing caption]";
      }

      caption = ChatColor.translateAlternateColorCodes('&', caption);
      return caption;
   }
}
