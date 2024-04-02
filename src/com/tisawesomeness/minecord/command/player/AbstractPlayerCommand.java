package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.RequestUtils;
import com.tisawesomeness.minecord.util.Utils;
import com.tisawesomeness.minecord.util.type.FutureCallback;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AbstractPlayerCommand extends SlashCommand {

    protected static void handleMojangIOE(Throwable ex, SlashCommandInteractionEvent e, String errorMessage) {
        handleIOE(ex, e, errorMessage, true);
    }
    protected static void handleIOE(Throwable ex, SlashCommandInteractionEvent e, String errorMessage) {
        handleIOE(ex, e, errorMessage, false);
    }
    @SneakyThrows
    private static void handleIOE(Throwable ex, SlashCommandInteractionEvent e, String errorMessage, boolean sendError) {
        Throwable cause = ex.getCause();
        if (cause instanceof IOException) {
            System.err.println(errorMessage);
            if (sendError) {
                e.getHook().sendMessage(":x: There was an error contacting the Mojang API.").setEphemeral(true).queue();
            }
            return;
        }
        throw cause;
    }

    protected <T> FutureCallback.Builder<T> newCallbackBuilder(CompletableFuture<T> future, SlashCommandInteractionEvent e) {
        return FutureCallback.builder(future).onUncaught(ex -> handleUncaught(ex, e));
    }

    private void handleUncaught(Throwable ex, SlashCommandInteractionEvent e) {
        try {
            System.err.println("Uncaught exception in command " + debugRunSlashCommand(e));
            handleException(ex, e);
        } catch (Exception ex2) {
            System.err.println("Somehow, there was an exception processing an uncaught exception");
            ex2.printStackTrace();
        }
    }

    /**
     * If Crafatar reuploading is enabled, images in the embed will be downloaded and sent with the embed as attachments.
     * Otherwise, the embed will be sent as-is. If an image file fails to download, the embed will link to the image
     * URL as normal. Assumes .png images.
     * @param e event, a defer reply request must have been sent before
     * @param emb the embed to send
     */
    protected static void uploadOrEmbedImages(SlashCommandInteractionEvent e, MessageEmbed emb) {
        if (Config.getReuploadCrafatarImages()) {
            EmbedBuilder eb = new EmbedBuilder(emb);
            List<FileUpload> files = new ArrayList<>();

            MessageEmbed.AuthorInfo author = emb.getAuthor();
            if (author != null) {
                tryDownloadImage(author.getIconUrl(),
                        attachment -> eb.setAuthor(author.getName(), author.getUrl(), attachment)).ifPresent(files::add);
            }
            String imageUrl = Utils.mapNullable(emb.getImage(), MessageEmbed.ImageInfo::getUrl);
            tryDownloadImage(imageUrl, eb::setImage).ifPresent(files::add);
            String thumbnailUrl = Utils.mapNullable(emb.getThumbnail(), MessageEmbed.Thumbnail::getUrl);
            tryDownloadImage(thumbnailUrl, eb::setThumbnail).ifPresent(files::add);

            if (!files.isEmpty()) {
                e.getHook().sendMessageEmbeds(eb.build()).addFiles(files).queue();
                return;
            }
        }
        e.getHook().sendMessageEmbeds(emb).queue();
    }

    private static Optional<FileUpload> tryDownloadImage(String url, Consumer<String> attachmentConsumer) {
        if (url == null) {
            return Optional.empty();
        }
        try {
            byte[] data = RequestUtils.download(url);
            String fileName = UUID.randomUUID() + ".png";
            attachmentConsumer.accept("attachment://" + fileName);
            return Optional.of(FileUpload.fromData(data, fileName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

}
