package net.dev.eazynick.commands;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.utils.*;
import net.dev.eazynick.utils.bookutils.*;
import net.md_5.bungee.api.chat.*;

public class BookGUICommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		GUIFileUtils guiFileUtils = eazyNick.getGUIFileUtils();
		NMSBookUtils nmsBookUtils = eazyNick.getNMSBookUtils();
		NMSBookBuilder nmsBookBuilder = eazyNick.getNMSBookBuilder();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.gui")) {
				if(new NickManager(p).isNicked())
					Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
				
				if(!(fileUtils.getConfig().getStringList("DisabledNickWorlds").contains(p.getWorld().getName()))) {
					if(args.length == 0) {
						if(guiFileUtils.getConfig().getBoolean("BookGUI.Page1.Enabled")) {
							ArrayList<TextComponent> textComponents = new ArrayList<>();
							
							for(String s : guiFileUtils.getConfigString(p, "BookGUI.Page1.Text").split("\n"))
								textComponents.add(new TextComponent(s + "\n"));
							
							for(String s : guiFileUtils.getConfigString(p, "BookGUI.Accept.Text").split("\n")) {
								TextComponent option = new TextComponent(s + "\n");
								option.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui accept"));
								option.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.Accept.Hover"))));
								
								textComponents.add(option);
							}
							
							nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page1.Title"), new BookPage(textComponents)));
							return true;
						} else
							args = new String[] { "accept" };
					} else if(args.length == 1) {
						if(args[0].equalsIgnoreCase("accept")) {
							ArrayList<TextComponent> textComponentsOfFirstPage = new ArrayList<>();
							ArrayList<TextComponent> textComponentsOfSecondPage = new ArrayList<>();
							
							for(String s : guiFileUtils.getConfigString(p, "BookGUI.Page2.Text").split("\n"))
								textComponentsOfFirstPage.add(new TextComponent(s + "\n"));
							
							for (int i = 1; i <= 18; i++) {
								String permission = guiFileUtils.getConfigString(p, "RankGUI.Rank" + i + ".Permission");
								
								if(guiFileUtils.getConfig().getBoolean("RankGUI.Rank" + i + ".Enabled") && (permission.equalsIgnoreCase("NONE") || p.hasPermission(permission))) {
									String rank = guiFileUtils.getConfigString(p, "RankGUI.Rank" + i + ".Rank");
									TextComponent textComponent = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Rank.Text").replace("%rank%", rank));
									textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + guiFileUtils.getConfigString(p, "RankGUI.Rank" + i + ".RankName")));
									textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.Rank.Hover").replace("%rank%", rank))));
									
									if(textComponentsOfFirstPage.size() < 7)
										textComponentsOfFirstPage.add(textComponent);
									else
										textComponentsOfSecondPage.add(textComponent);
								}
							}
							
							nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page2.Title"), new BookPage(textComponentsOfFirstPage), new BookPage(textComponentsOfSecondPage)));
						}
					} else if(args.length == 2) {
						ArrayList<TextComponent> textComponents = new ArrayList<>();
						
						for(String s : guiFileUtils.getConfigString(p, "BookGUI.Page3.Text").split("\n"))
							textComponents.add(new TextComponent(s + "\n"));
						
						TextComponent option1 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.NormalSkin.Text"));
						option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " DEFAULT"));
						option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.NormalSkin.Hover"))));
						
						textComponents.add(option1);
						
						TextComponent option2 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.SteveAlexSkin.Text"));
						option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " NORMAL"));
						option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.SteveAlexSkin.Hover"))));
						
						textComponents.add(option2);
						
						TextComponent option3 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.RandomSkin.Text"));
						option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " RANDOM"));
						option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.RandomSkin.Hover"))));
						
						textComponents.add(option3);
						
						TextComponent option4 = new TextComponent(fileUtils.getConfig().getBoolean("AllowBookGUISkinFromName") ? guiFileUtils.getConfigString(p, "BookGUI.SkinFromName.Text") : "");
						option4.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " SKINFROMNAME"));
						option4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.SkinFromName.Hover"))));
						
						textComponents.add(option4);
						
						TextComponent option5 = new TextComponent(utils.getLastSkinNames().containsKey(p.getUniqueId()) ? (guiFileUtils.getConfigString(p, "BookGUI.ReuseSkin.Text").replace("%skin%", utils.getLastSkinNames().get(p.getUniqueId()))) : "");
						
						if(utils.getLastSkinNames().containsKey(p.getUniqueId())) {
							option5.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + utils.getLastSkinNames().get(p.getUniqueId())));
							option5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.ReuseSkin.Hover"))));
						
							textComponents.add(option5);
						}
							
						nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page3.Title"), new BookPage(textComponents)));
					} else if(args.length == 3) {
						ArrayList<TextComponent> textComponents = new ArrayList<>();
						
						for(String s : guiFileUtils.getConfigString(p, "BookGUI.Page4.Text").split("\n"))
							textComponents.add(new TextComponent(s + "\n"));
						
						TextComponent option1 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.EnterName.Text"));
						option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2] + " ENTERNAME"));
						option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.EnterName.Hover"))));
						
						if(fileUtils.getConfig().getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname")))
							textComponents.add(option1);
						
						TextComponent option2 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.RandomName.Text"));
						option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2] + " RANDOM"));
						option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.RandomName.Hover"))));
						
						textComponents.add(option2);
						
						TextComponent option3 = new TextComponent(utils.getLastSkinNames().containsKey(p.getUniqueId()) ? (guiFileUtils.getConfigString(p, "BookGUI.ReuseName.Text").replace("%name%", utils.getLastNickNames().get(p.getUniqueId()))) : "");
						
						if(utils.getLastNickNames().containsKey(p.getUniqueId())) {
							option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guinick " + args[1] + " " + args[2] + " " + utils.getLastNickNames().get(p.getUniqueId())));
							option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.ReuseName.Hover").replace("%name%", utils.getLastNickNames().get(p.getUniqueId())))));
						
							textComponents.add(option3);
						}
	
						nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page4.Title"), new BookPage(textComponents)));
					} else if(args[3].equalsIgnoreCase("RANDOM")) {
						String name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
						boolean nickNameIsInUse = false;
						
						for (String nickName : utils.getPlayerNicknames().values()) {
							if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
								nickNameIsInUse = true;
						}
						
						while (nickNameIsInUse) {
							nickNameIsInUse = false;
							name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
							
							for (String nickName : utils.getPlayerNicknames().values()) {
								if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
									nickNameIsInUse = true;
							}
						}
					
						ArrayList<TextComponent> textComponents = new ArrayList<>();
						
						for(String s : guiFileUtils.getConfigString(p, "BookGUI.Page5.Text").replace("%name%", name).split("\n"))
							textComponents.add(new TextComponent(s + "\n"));
					
						TextComponent option1 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.OptionUseName.Text"));
						option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guinick " + args[1] + " " + args[2]  + " " + name));
						option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.OptionUseName.Hover"))));
						
						textComponents.add(option1);
						
						TextComponent option2 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.OptionTryAgain.Text"));
						option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2]  + " RANDOM"));
						option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.OptionTryAgain.Hover"))));
						
						textComponents.add(option2);
						
						TextComponent option3 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.OptionEnterName.Text"));
						option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2]  + " ENTERNAME"));
						option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(guiFileUtils.getConfigString(p, "BookGUI.OptionEnterName.Hover"))));
						
						if(fileUtils.getConfig().getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname")))
							textComponents.add(option3);
						
						for(String s : guiFileUtils.getConfigString(p, "BookGUI.Page5.Text2").split("\n"))
							textComponents.add(new TextComponent(s + "\n"));
						
						nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page5.Title"), new BookPage(textComponents)));
					} else if(args[3].equalsIgnoreCase("ENTERNAME")) {
						if(fileUtils.getConfig().getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname"))) {
							if(eazyNick.getVersion().equals("1_7_R4") || eazyNick.getVersion().equals("1_8_R1") || !(fileUtils.getConfig().getBoolean("UseSignGUIForCustomName") || fileUtils.getConfig().getBoolean("UseAnvilGUIForCustomName"))) {
								utils.getPlayersTypingNameInChat().put(p.getUniqueId(), args[1] + " " + args[2]);
								
								p.closeInventory();
								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(p, "Messages.TypeNameInChat"));
							} else
								utils.openCustomGUI(p, args[1], args[2]);
						}
					}
				} else
					p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(p, "Messages.DisabledWorld"));
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
