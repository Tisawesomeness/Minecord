package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.DbGuild;
import com.tisawesomeness.minecord.setting.impl.PrefixSetting;
import com.tisawesomeness.minecord.util.type.Validation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;

public class PrefixCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"prefix",
			"Change the prefix.",
			"[prefix]",
			new String[]{
				"resetprefix",
				"changeprefix"},
			5000,
			true,
			false,
			true
		);
	}

	public String getHelp() {
		return "`{&}prefix` - Show the current prefix.\n" +
			"`{&}prefix <prefix>` - Change the prefix. The user must have **Manage Server** permissions.\n" +
			"The prefix can be any text between 1-16 characters.\n" +
			"\n" +
			"Examples:\n" +
			"- `{&}prefix mc!`\n" +
			"- {@}` prefix &`\n";
	}
	
	public Result run(CommandContext txt) {
		String[] args = txt.args;
		MessageReceivedEvent e = txt.e;

		// Guild-only command
		if (!e.isFromGuild()) {
			return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
		}
		
		// Check if user is elevated or has the manage messages permission
		if (!txt.isElevated && !e.getMember().hasPermission(e.getTextChannel(), Permission.MANAGE_SERVER)) {
			return new Result(Outcome.WARNING, ":warning: You must have manage server permissions!");
		}

		PrefixSetting prefixSetting = txt.bot.getSettings().prefix;

		if (args.length == 0) {
			
			// Print current prefix
			return new Result(Outcome.SUCCESS,
				"The current prefix is `" + prefixSetting.getEffective(txt) + "`"
			);
			
		} else {

			// Easter egg for those naughty bois
			if (args[0].equals("'") && args[1].equals("OR") && args[2].equals("1=1")) {
				return new Result(Outcome.SUCCESS, "Nice try.");
			}
			// Set new prefix
			DbGuild guild = txt.bot.getDatabase().getCache().getGuild(e.getGuild().getIdLong());
			try {
				Validation<String> attempt = txt.bot.getSettings().prefix.tryToSet(guild, args[0]);
				if (attempt.isValid()) {
					return new Result(Outcome.SUCCESS, attempt.getValue());
				}
				String errorMsg = String.join(", ", attempt.getErrors());
				return new Result(Outcome.WARNING, ":warning: " + errorMsg);
			} catch (SQLException ex) {
				ex.printStackTrace(); // Not printing exception to the user just to be safe
				return new Result(Outcome.ERROR, ":x: There was an internal error.");
			}
			
		}
	}

}
