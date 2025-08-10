package com.tisawesomeness.minecord.interaction;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InteractionListener extends ListenerAdapter {

    @Override
    public void onGenericComponentInteractionCreate(GenericComponentInteractionCreateEvent e) {
        InteractionTracker.onInteract(e);
    }
    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        InteractionTracker.onSubmit(e);
    }

}
