package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.internal.utils.IOUtil;

import javax.annotation.CheckReturnValue;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordUtils {

    private static final Pattern ID_PATTERN = Pattern.compile("[0-9]{2,32}");
    private static final Pattern USER_MENTION_PATTERN = Pattern.compile("(<@!?)?([0-9]{2,32})>?");
    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("(<#)?([0-9]{2,32})>?");

    public static boolean isDiscordId(String str) {
        return ID_PATTERN.matcher(str).matches();
    }

    public static String tagAndId(User u) {
        return String.format("%#s (`%s`)", u, u.getId());
    }

    public static void update() {
        Bot.shardManager.setActivity(Activity.playing(parseAll(Config.getGame())));
    }

    /**
     * Replaces constants in the input string with their values
     * This can be called during init, as long as Config is initialized
     * @param input A string with {constants}
     * @return The string with resolved constants, though variables such as {guilds} are unresolved
     */
    public static String parseConstants(String input) {
        return input
                .replace("{author}", Config.getAuthor())
                .replace("{author_tag}", Config.getAuthorTag())
                .replace("{help_server}", Config.getHelpServer())
                .replace("{website}", Config.getWebsite())
                .replace("{github}", Config.getGithub())
                .replace("{jda_ver}", Bot.jdaVersion)
                .replace("{version}", Bot.getVersion())
                .replace("{invite}", Config.getInvite())
                .replace("{prefix}", Config.getPrefix());
    }

    /**
     * Replaces variables in the input string with their values
     * This must be called after init
     * @param input A string with {variables}
     * @return The string with resolved variables, though constants such as {version} are unresolved
     */
    public static String parseVariables(String input) {
        return input.replace("{guilds}", String.valueOf(Bot.shardManager.getGuilds().size()));
    }

    /**
     * Replaces variables and constants in the input string with their values
     * This must be called after init
     * @param input A string with {variables}
     * @return The string with resolved variables
     */
    public static String parseAll(String input) {
        return parseVariables(parseConstants(input));
    }

    public static User findUser(String search) {
        if (isDiscordId(search)) {
            return Bot.shardManager.retrieveUserById(search).complete();
        }
        Matcher ma = USER_MENTION_PATTERN.matcher(search);
        if (!ma.matches()) {
            return null;
        }
        return Bot.shardManager.retrieveUserById(ma.group(2))
                .onErrorMap(ErrorResponse.UNKNOWN_USER::test, x -> null)
                .onErrorMap(ErrorResponse.UNKNOWN_MEMBER::test, x -> null)
                .complete();
    }

    public static TextChannel findChannel(String search) {
        if (isDiscordId(search)) {
            return Bot.shardManager.getTextChannelById(search);
        }
        Matcher ma = CHANNEL_MENTION_PATTERN.matcher(search);
        return ma.matches() ? Bot.shardManager.getTextChannelById(ma.group(2)) : null;
    }

    /**
     * Gets the emote text associated with true or false.
     */
    public static String getBoolEmote(boolean bool) {
        return bool ? ":white_check_mark:" : ":x:";
    }

    public static CompletableFuture<byte[]> retrieveImage(ImageProxy img) {
        if (img == null) {
            return CompletableFuture.completedFuture(null);
        }
        return img.download().thenApply(is -> {
            try {
                return IOUtil.readFully(is);
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        });
    }

    @CheckReturnValue
    public static WebhookMessageCreateAction<Message> sendImageAsAttachment(SlashCommandInteractionEvent e,
            MessageEmbed emb, String attachmentName) {
        return sendAsAttachment(e, emb, attachmentName, emb.getImage().getUrl(), false);
    }
    @CheckReturnValue
    public static WebhookMessageCreateAction<Message> sendThumbnailAsAttachment(SlashCommandInteractionEvent e,
            MessageEmbed emb, String attachmentName) {
        return sendAsAttachment(e, emb, attachmentName, emb.getThumbnail().getUrl(), true);
    }
    @CheckReturnValue
    private static WebhookMessageCreateAction<Message> sendAsAttachment(SlashCommandInteractionEvent e,
            MessageEmbed emb, String attachmentName, String url, boolean isThumbnail) {
        String attachment = buildAttachmentFilename(attachmentName, url);
        try {
            byte[] data = RequestUtils.download(url);
            MessageEmbed edited = isThumbnail
                    ? new EmbedBuilder(emb).setThumbnail("attachment://" + attachment).build()
                    : new EmbedBuilder(emb).setImage("attachment://" + attachment).build();
            return e.getHook().sendMessageEmbeds(edited).addFiles(FileUpload.fromData(data, attachment));
        } catch (IOException ex) {
            ex.printStackTrace();
            return e.getHook().sendMessageEmbeds(emb);
        }
    }
    @CheckReturnValue
    public static WebhookMessageEditAction<Message> editImageAsAttachment(SlashCommandInteractionEvent e,
            MessageEmbed emb, String attachmentName) {
        String url = emb.getImage().getUrl();
        String attachment = buildAttachmentFilename(attachmentName, url);
        try {
            byte[] data = RequestUtils.download(url);
            return e.getHook().editOriginalEmbeds(new EmbedBuilder(emb).setImage("attachment://" + attachment).build())
                    .setAttachments(FileUpload.fromData(data, attachment));
        } catch (IOException ex) {
            ex.printStackTrace();
            return e.getHook().editOriginalEmbeds(emb);
        }
    }
    private static String buildAttachmentFilename(String attachmentName, String url) {
        if (attachmentName.contains(".")) {
            return attachmentName;
        }
        String urlExtension = url.substring(url.lastIndexOf('.'));
        return attachmentName + urlExtension;
    }

}
