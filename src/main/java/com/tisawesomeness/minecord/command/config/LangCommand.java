package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.setting.parse.SmartSetParser;
import com.tisawesomeness.minecord.util.BooleanUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LangCommand extends AbstractConfigCommand {

    public @NonNull String getId() {
        return "lang";
    }

    public void run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            ctx.triggerCooldown();
            listLanguages(ctx, "Languages", false);
            return;
        } else if ("all".equalsIgnoreCase(args[0])) {
            ctx.triggerCooldown();
            listLanguages(ctx, "All Languages", true);
            return;
        } else if ("info".equalsIgnoreCase(args[0])) {
            if (args.length == 1) {
                ctx.invalidArgs("You must specify a language.");
                return;
            }
            Optional<Lang> langOpt = Lang.from(args[1]);
            if (langOpt.isPresent()) {
                ctx.triggerCooldown();
                displayLanguageInfo(ctx, langOpt.get());
                return;
            }
            ctx.invalidArgs("That language is not valid.");
            return;
        }
        Optional<Lang> langOpt = Lang.from(args[0]);
        if (langOpt.isPresent()) {
            new SmartSetParser(ctx, ctx.getBot().getSettings().lang).parse();
            return;
        }
        ctx.invalidArgs("That language is not valid.");
    }

    private static void listLanguages(CommandContext ctx, String title, boolean includeDevelopment) {
        Stream<Lang> langStream = Arrays.stream(Lang.values());
        if (!includeDevelopment) {
            langStream = langStream.filter(l -> !l.isInDevelopment());
        }
        String langStr = langStream
                .map(LangCommand::getLangDescriptionString)
                .collect(Collectors.joining("\n"));
        String langHelp = String.format("Use `%slang <code>` or `%sset` to change languages.\n" +
                "Use `%slang info <code>` to view info for a language.",
                ctx.getPrefix(), ctx.getPrefix(), ctx.getPrefix());
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(title)
                .setDescription(langHelp + "\n\n" + langStr);
        ctx.reply(eb);
    }
    private static String getLangDescriptionString(Lang l) {
        return String.format("**`%s`** %s - %s", l.getCode(), l.getFlagEmote(), l.getLocale().getDisplayName());
    }

    private static void displayLanguageInfo(CommandContext ctx, Lang l) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Language Info for " + l.getLocale().getDisplayName() + " " + l.getFlagEmote());
        eb.getDescriptionBuilder()
                .append("Language Code: `").append(l.getCode()).append("`\n")
                .append("Supports Messages?: ").append(emote(l.isBotStringsSupported())).append("\n")
                .append("Has Command Aliases?: ").append(emote(l.isCommandAliasSupported())).append("\n")
                .append("Supports Minecraft Items?: ").append(emote(l.isItemsSupported())).append("\n")
                .append("Supports Enhanced Item Search?: ").append(emote(l.isItemSearchSupported())).append("\n")
                .append("In Development?: ").append(emote(l.isInDevelopment()));
        ctx.reply(eb);
    }
    // exists purely to save space
    private static String emote(boolean b) {
        return BooleanUtils.getEmote(b);
    }
}
