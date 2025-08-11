package com.tisawesomeness.minecord.interaction;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * Represents a message with state that can be updated with interactions.
 */
public interface UpdatingMessage {

    /**
     * Run when this message is interacted with.
     * @param e the event
     * @return true if the message was modified and should be edited
     */
    boolean onInteract(GenericComponentInteractionCreateEvent e);

    /**
     * Run when a modal attached to this message is submitted.
     * @param e the event
     * @return true if the message was modified and should be edited
     */
    default boolean onSubmit(ModalInteractionEvent e) {
        return false;
    }

    /**
     * Renders the message into a format that can be sent.
     * This method will be called both for message creation and editing.
     * @param supportsInteractions whether the current context supports interactions
     * @return the message
     */
    MessageCreateData render(boolean supportsInteractions);

}
