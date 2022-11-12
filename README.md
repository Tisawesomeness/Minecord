# Minecord [![Discord Bots](https://discordbots.org/api/widget/status/292279711034245130.png)](https://discordbots.org/bot/292279711034245130) [![Discord Bots](https://discordbots.org/api/widget/servers/292279711034245130.png)](https://discordbots.org/bot/292279711034245130) [![Discord Bots](https://discordbots.org/api/widget/upvotes/292279711034245130.png)](https://discordbots.org/bot/292279711034245130)
A robust Discord bot using the JDA library for various Minecraft functions.

- **Official Bot Invite: https://minecord.github.io/invite**
- Bot User: Minecord#1216
- Support Server: https://discord.gg/hrfQaD7

### Command List

#### Core Commands
- `/help [<command>|<module>|extra]` - Displays help for the bot, a command, or a module.
- `/info` - Shows the bot info.
- `/ping` - Pings the bot.
- `/invite` - Get the invite link for the bot.
- `/vote` - Get all the vote links.
- `/credits` - See who made the bot possible.

#### Player Commands
- `/profile <player>` - Shows info for a Minecraft account.
- `/history <player>` - Shows a player's name history.
- `/history <uuid|username>` - Shows UUID info for a player or entity.
- `/skin <player>` - Shows an image of a player's skin.
- `/cape <player>` - Shows a player's Minecraft and Optifine capes.
- `/avatar <player> [<scale>] [<overlay?>]` - Shows an image of the player's avatar.
- `/head <player> [<scale>] [<overlay?>]` - Shows an image of the player's head.
- `/body <player> [<scale>] [<overlay?>]` - Shows an image of the player's body.
- `/ansi <player> [<overlay?>]` - Converts a player's avatar to a colored text message.

#### Utility Commands
- `/status` - Checks the status of Mojang servers.
- `/server <address>[:<port>]` - Fetches the stats of a server.
- `/recipe <item name|id>` - Look up recipes.
- `/item <item name|id>` - Looks up an item.
- `/ingredient <item name|id>` - Looks up the recipes an ingredient is used in.
- `/codes` - Lists the available chat codes.
- `/color` - Look up a color. Shows color code and background color for Minecraft colors.
- `/seed <text>` - Converts some text to a seed number.
- `/shadow <seed>` - Gets the shadow of a seed.
- `/sha1 <text>` - Computes the sha1 hash of some text.

#### General Commands
- `/user <user|id>` - Shows user info.
- `/guild` - Shows guild info.
- `/role <role|id>` - Shows role info.
- `/roles <user|id>` - List a user's roles.
- `/id <id>` - Gets the creation time of a Discord ID.
- `/purge <number>` - Cleans the bot messages. Requires Manage Messages permissions.
- `/perms [<channel>]` - Test the bot's permissions in a channel. Leave blank to test the current channel.

#### Admin Commands
- `@Minecord help <command> admin` - Displays admin help for a command.
- `@Minecord infoadmin` - Displays bot info, including used memory and boot time.
- `@Minecord guildadmin <guild id>` - Show info and ban status for another guild.
- `@Minecord roleadmin <role id>` - Show role info for any role.
- `@Minecord useradmin <user id> [mutual]` - Show info, ban status, and elevation for a user outside of the current guild. Include `mutual` to show mutual guilds.
- `@Minecord settings <guild id> admin [<setting> <value>]` - Change the bot's settings for another guild.
- `@Minecord permsadmin <channel id>` - Test the bot's permissions in any channel.
- `@Minecord say <channel> <message>` - Say a message.
- `@Minecord msg <mention> <message>` - Open the DMs.
- `@Minecord name <guild id> <name>` - Changes the bot's nickname per-guild, enter nothing to reset.
- `@Minecord usage` - Shows how often commands are used.
- `@Minecord debug [<option>|all]` - Prints out debug info.
- `@Minecord promote <user>` - Elevate a user.
- `@Minecord demote <user>` - De-elevate a user.
- `@Minecord ban [user|guild] <id>` - Bans/unbans a user/guild from the bot. Omit user/guild to check for a ban.
- `@Minecord reload` - Reloads the bot. In dev mode, this hot reloads all code. Otherwise, this reloads the config, item/recipe files, and restarts the database and vote server.
- `@Minecord shutdown` - Shuts down the bot.
- `@Minecord deploy` - Deploys slash commands globally.
- `@Minecord eval` - Evaluates javascript code with variables `jda`, `config`, `event`, `guild`, `channel`, and `user`.
- `@Minecord test` - Test command. This may change depending on what features are being developed.

### Config

- *Client Token:* Your unique bot token. **Do not upload it to GitHub, or people will be able to steal your bot!**
- *Shard Count:* The amount of shards to use. Set to 1 if you don't need sharding.
- *Owner:* The user ID of the bot owner. The bot will work when owner is 0, but it is *highly encouraged* to set this value.
- *Test Servers:* A list of server IDs that will have guild slash commands updated on startup for testing.

- *Log Channel:* The bot will send any logging messages to this channel. Set to 0 to disable.
- *Join Log Channel:* The bot will send server join/leave messages to this channel. Set to 0 to disable.
- *Is Self Hosted:* Leave as `true` if you are self-hosting the bot.
- *Author:* The name of the person hosting the bot.
- *Author Tag:* The Discord tag of the person hosting the bot.
- *Invite:* The invite link to use in `&invite`.
- *Help Server:* The help server link to use in `&invite`.
- *Website:* The website to display in `&info`.
- *Github:* A link to the source code currently running on the bot.
- *Prefix:* The prefix of the bot. Use something else instead of `&` if you want to host your own bot alongside the main one.
- *Game:* This is the game that the bot is playing, shown under the username. `{prefix}` and `{guilds}` are available variables.
- *Dev Mode:* Turning this on will let you reload code using `&reload` on the fly. When hosting the bot, it's best to keep this off in order to decrease memory usage.
- *Debug Mode:* If true, exceptions during command execution will be sent to the user.
- *Delete Commands:* If true, the commands sent by players (like `&help`) will be deleted to clean up chat. Requires permission to manage messages.
- *Use Menus:* If true, the bot will use a reaction menu for `&recipe` and `&ingredient` if possible.
- *Show Memory:* Whether to show the memory in `&info`.
- *Elevated Skip Cooldown:* Whether elevated users skip command cooldowns.
- *Use Electroid API:* Whether to use the Electroid API to speed up player lookups. May cause slowdowns if the API is consistently down.
- *Use Gapple API:* Whether to use the Gapple API to look up account types. May cause slowdowns if the API is consistently down.
- *Record Cache Stats:* Whether to record cache performance statistics. This will cause a slight performance hit.
- *Item Image Host:* The website hosting item images.
- *Recipe Image Host:* The website hosting recipe images.

- *Send Server Count:* Whether the bot should send the guild count to bot list websites.
- *Pw Token:* The token to use on bots.discord.pw.
- *Org Token:* The token to use on discordbots.org (top.gg).
- *Receive Votes:* When true, the bot will receive votes from discordbots.org (top.gg). **This will set up an HTTP server.**
- *Webhook URL:* The URL used to receive votes. Keep this random and private, if it leaks, users will be able to fake votes. Set the discordbots.org webhook to http://`your ip`:`port`/`url`.
- *Webhook Port:* The port used to receive votes.
- *Webhook Auth:* All incoming vote requests must have this code in the "Authorization" header.

- *Database Type:* Either mysql or sqlite. Defaults to sqlite, otherwise a mysql server is required.
- *Database Host:* The hostname for the mysql server or the file path for the sqlite database.
- *Database Port:* The port used to connect to a mysql database.
- *Database Name:* The name of the database to use.
- *Database User:* The username used to connect to the database.
- *Database Pass:* The password used to connect to the database.

### Self Hosting
- Download the [latest release](https://github.com/Tisawesomeness/Minecord/releases/latest).
- Download all the `.json` files from this repo and place them in the same folder as `minecord.jar`. You can either clone/download the entire repository or download the files individually.
- Create a Discord bot and put the token in `config.json`.
- Customize `config.json` as much as you like.
- Run the bot with `java -jar minecord.jar`.
- Join the support server [here](https://discord.gg/hrfQaD7) for any extra help or enquiries.

#### Default Config
```json
{
  "clientToken": "your token here",
  "shardCount": 1,
  "owner": "0",
  "testServers": [
  ],
  "settings": {
    "logChannel": "0",
    "joinLogChannel": "0",
    "isSelfHosted": true,
    "author": "Tis_awesomeness",
    "authorTag": "@Tis_awesomeness#8617",
    "invite": "https://minecord.github.io/invite",
    "helpServer": "https://minecord.github.io/support",
    "website": "https://minecord.github.io",
    "github": "https://github.com/Tisawesomeness/Minecord",
    "prefix": "&",
    "game": "/help | {guilds} guilds",
    "devMode": false,
    "debugMode": false,
    "deleteCommands": false,
    "useMenus": true,
    "showMemory": false,
    "elevatedSkipCooldown": true,
    "useElectroidAPI": true,
    "useGappleAPI": true,
    "recordCacheStats": false,
    "itemImageHost": "https://minecord.github.io/item/",
    "recipeImageHost": "https://minecord.github.io/recipe/"
  },
  "botLists": {
    "sendServerCount": false,
    "pwToken": "your token here",
    "orgToken": "your token here",
    "receiveVotes": false,
    "webhookURL": "sample",
    "webhookPort": 8000,
    "webhookAuth": "auth here"
  },
  "database": {
    "type": "sqlite",
    "host": "./minecord.db",
    "port": "3306",
    "name": "minecord",
    "user": "minecord",
    "pass": "password here"
  }
}
```

### Command-Line Arguments
- `-c <path/to/config.json>` - Defines a custom path to the config.json. Defaults to the current directory.
- `-t <token>` - Overrides the token provided in the config.
