# Minecord [![Codacy Badge](https://api.codacy.com/project/badge/Grade/3de0f658514246f598b40fb1bdf55af9)](https://www.codacy.com/app/Tis_awesomeness/Minecord?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Tisawesomeness/Minecord&amp;utm_campaign=Badge_Grade) [![Discord Bots](https://discordbots.org/api/widget/status/292279711034245130.png)](https://discordbots.org/bot/292279711034245130) [![Discord Bots](https://discordbots.org/api/widget/servers/292279711034245130.png)](https://discordbots.org/bot/292279711034245130) [![Discord Bots](https://discordbots.org/api/widget/upvotes/292279711034245130.png)](https://discordbots.org/bot/292279711034245130)
A robust Discord bot using the JDA library for various Minecraft functions.
- Invite: https://bot.discord.io/minecord
- Bot User: Minecord#1216
- Support Server: https://discord.io/minecord

### Command List
#### General Commands
- `&help` - Displays this help menu.
- `&info` - Shows the bot info.
- `&invite` - Give the invite link for the bot.
- `&status` - Checks the status of Mojang servers.
- `&sales` - Looks up the sale statistics.
- `&purge [number]` - Cleans the bot messages.

#### Utility Commands:
- `&codes` - Lists the available chat codes.
- `&server <address>[:port]` - Fetches the stats of a server.
- `&item <item name|id>` - Looks up an item.
- `&recipe <item name|id>` - Looks up a recipe.

#### Player Commands:
- `&uuid <username> [date]` - Gets the UUID of a player.
- `&history <username|uuid> [date]` - Gets the name history of a player.
- `&avatar <username|uuid> [date] [overlay?]` - Gets the avatar of a player.
- `&head <username|uuid> [date] [overlay?]` - Gets the head render of a player.
- `&body username|uuid> [date] [overlay?]` - Gets the body render of a player.
- `&skin <username|uuid> [date]` - Gets the skin of a player.
- `&cape <username|uuid> [date]` - Gets the cape of a player.

#### Admin Commands:
- `&say <channel> <message` - Say a message.
- `&msg <mention> <message>` - Open the DMs.
- `&name <guild id> <name>` - Changes the bot's nickname per-guild, enter nothing to reset.
- `&usage` - Shows how often commands are used.
- `&reload` - Reloads the bot.
- `&shutdown` - Shuts down the bot.
- `&eval` - Evaluates javascript code with variables `jda`, `config`, `event`, `guild`, `channel`, and `user`.
- `&test` - Test command.

### Config
- *Client Token:* Your unique bot token. **Do not upload it to GitHub, or people will be able to steal your bot!**
- *Shard Count:* The amount of shards to use. Set to 1 if you don't need sharding.
- *Owner:* The user ID of the bot owner.
- *Dev Mode:* Turning this on will let you reload code using `&reload` on the fly. When hosting the bot, it's best to keep this off in order to decrease memory usage.
- *Debug Mode:* Prints additional info to console.
- *Log JDA:* Whether or not to log messages from the JDA library.
- *Log Channel:* The bot will send any logging messages to this channel. Set to 0 to disable.
- *Send Server Count:* Whether or not the bot should send the guild count to bot list websites.
- *Pw Token:* The token to use on bots.discord.pw.
- *Net Token:* The token to use on bots.discordlist.net.
- *Org Token:* The token to use on discordbots.org.
- *Game:* This is the game that the bot is playing, shown under the username. `{prefix}`, `{guilds}`, `{channels}`, and `{users}` are available variables.
- *Name:* The name of the bot.
- *Prefix:* The prefix of the bot. Use `>&` instead of `&` if you want to host your own bot alongside the main one.
- *Respond To Mentions:* This option decides if the bot will respond to being mentioned at the beginning of a message, so you can use `@Minecord#1216 help` to execute `&help`.
- *Notification Time:* The default time to show notifications in miliseconds (4000ms = 4s). This value is multiplied based on the length of the notification sent.
- *Delete Commands:* If true, the commands sent by players (like `&help`) will be deleted to clean up chat. Requires permission to manage messages.
- *Send Typing:* If true, the bot will send typing packets.
- *Invite:* The invite link to use in `&invite`.
- *Show Memory:* Whether or not to show the memory in `&info`.
- *Elevated Skip Cooldown:* Whether or not elevated users skip command cooldowns.
- *Elevated Users:* A list of user IDs. Elevated users can do `&help admin` to view hidden commands, `&msg`, `&reload`, `&shutdown`, `&dump`, `&test`, and `&purge` in all servers.

#### Default Config
```json
{
	"clientToken": "your token here",
	"shardCount": 1,
	"owner": "0",
	"devMode": false,
	"debugMode": false,
	"logJDA": true,
	"logChannel": "0",
	"sendServerCount": false,
	"pwToken": "your token here",
	"netToken": "your token here",
	"orgToken": "your token here",
	"game": "{prefix}help | {guilds} guilds",
	"name": "Minecord",
	"prefix": "&",
	"respondToMentions": true,
	"notificationTime": 4000,
	"deleteCommands": true,
	"sendTyping": true,
	"invite": "https://bot.discord.io/minecord",
	"showMemory": true,
	"elevatedSkipCooldown": true,
	"elevatedUsers": [
		"211261249386708992",
		"220591718158827520"
	]
}
```

### Command-Line Arguments
- `-c <path/to/config.json>` - Defines a custom path to the config.json. Defaults to the current directory.
- `-t <token>` - Overrides the token provided in the config.

### Conventions
Feel free to contribute **to the dev branch** with whatever you like, but make sure to follow these conventions.
1. Your code blocks should look like the one below. This is Java, not C++.
```java
public static void main(String[] args) throws Exception {
	cl = Thread.currentThread().getContextClassLoader();
	load(args);
}
```
2. PLEASE comment all of your code so that people can find their way around it. You don't have to be excessive, just enough so that it is understandable.
3. Do not touch the version value in Bot class or the default elevated users list.
4. Be very careful when editing the Main, Loader, and Bot classes. They can break very easily.
5. **If you upload your bot token, then someone can steal your bot!** Contact me as soon as possible so I can remove the file quickly.
