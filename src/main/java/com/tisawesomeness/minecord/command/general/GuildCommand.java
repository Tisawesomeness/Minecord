package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.setting.ServerSetting;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.BoostTier;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.TimeUtil;

public class GuildCommand extends Command {

    public CommandInfo getInfo() {
		return new CommandInfo(
			"guild",
			"Shows guild info.",
			null,
			new String[]{"guildinfo"},
			0,
			false,
			false,
			false
		);
    }

    public String getAdminHelp() {
        return "`{&}guild` - Shows current guild info.\n" +
            "`{&}guild <guild id> admin` - Shows the info of another guild.\n" +
            "\n" +
            "Examples:\n" +
            "- `{&}guild 347765748577468416 admin`\n";
    }
    
    public Result run(CommandContext txt) {
        String[] args = txt.args;

        // If the author used the admin keyword and is an elevated user
        boolean elevated = false;
        Guild g;
		if (args.length > 1 && args[1].equals("admin") && txt.isElevated) {
            elevated = true;
            if (!args[0].matches(DiscordUtils.idRegex)) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
            g = txt.bot.getShardManager().getGuildById(args[0]);
            if (g == null) {
                long gid = Long.valueOf(args[0]);
                if (txt.bot.getDatabase().isBanned(gid)) {
                    return new Result(Outcome.SUCCESS,
                            "__**GUILD BANNED FROM MINECORD**__\n" + getSettingsStr(gid, txt));
                }
                return new Result(Outcome.SUCCESS, getSettingsStr(gid, txt));
            }
        } else {
            if (!txt.e.isFromGuild()) {
                return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
            }
            g = txt.e.getGuild();
        }
        User owner = g.retrieveOwner().complete().getUser();

        // Generate guild info
        int textChannels = g.getTextChannels().size();
        int voiceChannels = g.getVoiceChannels().size();
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle(MarkdownSanitizer.escape(g.getName()))
            .setImage(g.getIconUrl())
            .addField("ID", g.getId(), true)
            .addField("Users", String.valueOf(g.getMemberCount()), true)
            .addField("Roles", String.valueOf(g.getRoles().size()), true)
            .addField("Categories", String.valueOf(g.getCategories().size()), true)
            .addField("Channels", String.format("%d (%d text, %d voice)", textChannels + voiceChannels, textChannels, voiceChannels), true)
            .addField("Region", g.getRegion().getName(), true)
            .addField("Verification Level", g.getVerificationLevel().toString(), true)
            .addField("Owner", MarkdownSanitizer.escape(owner.getAsTag()), true)
            .addField("Owner ID", owner.getId(), true)
            .addField("Created", DateUtils.getDateAgo(TimeUtil.getTimeCreated(g)), false);
         if (g.getBoostTier() == BoostTier.UNKNOWN) {
            eb.addField("Boosts", g.getBoostCount() + " (Unknown Tier)", true);
        } else {
            eb.addField("Boosts", String.format("%d (Tier %s)", g.getBoostCount(), g.getBoostTier().getKey()), true);
        }
        if (g.getVanityCode() != null) {
            eb.addField("Vanity Code", g.getVanityCode(), true);
        }
        if (g.getDescription() != null) {
            eb.addField("Description", MarkdownSanitizer.escape(g.getDescription()), false);
        }
        if (elevated) {
            eb.addField("Settings", getSettingsStr(g.getIdLong(), txt), false);
            if (txt.bot.getDatabase().isBanned(g.getIdLong())) {
                eb.setDescription("__**GUILD BANNED FROM MINECORD**__");
            }
        }
        return new Result(Outcome.SUCCESS, txt.brand(eb).build());
    }

    private static String getSettingsStr(long gid, CommandContext txt) {
        SettingRegistry settings = txt.bot.getSettings();
        return String.format("prefix: `%s`%ndeleteCommands: `%s`%nuseMenus: `%s`",
                displaySetting(gid, settings.prefix),
                displaySetting(gid, settings.deleteCommands),
                displaySetting(gid, settings.useMenus));
    }
    private static String displaySetting(long gid, ServerSetting<?> setting) {
        return setting.getGuild(gid).map(Object::toString).orElse("`unset`");
    }
    
}