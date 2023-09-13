/*
 * Copyright 2014 jamietech. All rights reserved.
 * https://github.com/jamietech/MinecraftServerPing
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */
package br.com.azalim.mcserverping;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * References: http://wiki.vg/Server_List_Ping
 * https://gist.github.com/thinkofdeath/6927216
 */
@Getter
@ToString
public class MCPingResponse {

    /**
     * the MOTD
     */
    private Description description;

    /**
     * {@link Players}
     */
    private Players players;

    /**
     * {@link Version}
     */
    private Version version;

    /**
     * Base64 encoded favicon image
     */
    private String favicon;

    // below 3 are modifications from tis
    /**
     * Whether the server is announcing that it requires players to send public keys to join.
     * These servers enforce chat reports.
     */
    private boolean enforcesSecureChat;

    /**
     * Whether the server is announcing that it enables chat previews. Chat messages are sent to the server while
     * typing so the server can send a formatted preview.
     * Chat preview was added in 1.19 and removed in 1.19.3 due to privacy concerns.
     */
    private boolean previewsChat;

    /**
     * Whether the server is announcing that it prevents chat reports.
     * This is a custom response used to show an icon in the NoChatReports mod.
     */
    private boolean preventsChatReports;

    /**
     * Ping in ms.
     */
    @Setter
    private long ping;

    @Getter
    @ToString
    public static class Description {

        /**
         * Server description text
         */
        private String text;

        public String getStrippedText() {
            return MCPingUtil.stripColors(this.text);
        }

    }

    @Getter
    @ToString
    public static class Players {

        /**
         * Maximum player count
         */
        private int max;

        /**
         * Online player count
         */
        private int online;

        /**
         * List of some players (if any) specified by server
         */
        private List<Player> sample;

    }

    @Getter
    @ToString
    public static class Player {

        /**
         * Name of player
         */
        private String name;

        /**
         * Unknown
         */
        private String id;

    }

    @Getter
    @ToString
    public static class Version {

        /**
         * Version name (ex: 13w41a)
         */
        private String name;
        /**
         * Protocol version
         */
        private int protocol;

    }

}
