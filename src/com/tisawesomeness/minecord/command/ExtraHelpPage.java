package com.tisawesomeness.minecord.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ExtraHelpPage {
    USERNAME_INPUT(
            "usernameInput",
            "Username Input Help",
            "Show help for invalid names and names with spaces",
            new String[]{"nameInput"},
            "Valid usernames are 3-16 characters long and only contain letters, numbers, and underscores.\n" +
                    "However, invalid usernames such as `8`, `Cool.J`, and `Will Wall` have existed.\n" +
                    "If a username contains spaces, surround the name in quotes (\"\") or backticks (\\`\\`).\n" +
                    "\n" +
                    "The Mojang API supports usernames with up to 25 ASCII letters, numbers, spaces, and the characters `_!@$-.?` (Sorry, `Seng\u00E5ngaren`).\n" +
                    "If two or more accounts share the same username, use the UUID instead.\n" +
                    "\n" +
                    "Examples:\n" +
                    "- `{&}profile Cool.J 5` --> `Cool.J` is the username\n" +
                    "- `{&}profile Will Wall 5` --> `Will` is the username\n" +
                    "- `{&}body \"Will Wall\" 5` --> `Will Wall` is the username"
    ),
    UUID_INPUT(
            "uuidInput",
            "UUID Input Help",
            "Show help for NBT formats for UUIDs",
            new String[0],
            "A [UUID](https://minecraft.gamepedia.com/Universally_unique_identifier) (Universally Unique IDentifier) is a player's unique ID.\n" +
                    "UUIDs can be in any format shown in `{&}uuid`.\n" +
                    "\n" +
                    "**Short**: `f6489b797a9f49e2980e265a05dbc3af`\n" +
                    "**Long**: `f6489b79-7a9f-49e2-980e-265a05dbc3af`\n" +
                    "**1.16+ NBT**: `[I;-163013767,2057259490,-1743903142,98288559]`\n" +
                    "**Pre-1.16 NBT**: `UUIDMost:-700138796005504542,UUIDLeast:-7490006962183355473`"
    ),
    PHD(
            "phd",
            "Pseudo Hard-Deletion",
            "Show help for pseudo hard-deleted accounts",
            new String[0],
            "A **pseudo hard-deleted** account (or **PHD** for short) is an account that has been partially deleted from Mojang's account database.\n" +
                    "PHD accounts can be looked up by UUID, but not by name.\n" +
                    "All player commands except `{&}uuid` will check if an account is PHD if you enter a UUID.\n" +
                    "\n" +
                    "PHD accounts still have their name history, but no accessible account type, skin, or cape.\n" +
                    "`{&}skin`, `{&}cape`, and render commands will not work, but `{&}history` and `{&}profile` will."
    );

    /**
     * The internal ID of the help page
     */
    @Getter private final String name;
    @Getter private final String title;
    @Getter private final String description;
    private final String[] aliases;
    private final String help;

    public String getHelp() {
        return help.replace("{&}", "/");
    }

    public static ExtraHelpPage from(String name) {
        return Arrays.stream(values())
                .filter(page -> page.matches(name))
                .findFirst()
                .orElse(null);
    }
    private boolean matches(String name) {
        return name.equalsIgnoreCase(this.name) || Arrays.stream(aliases)
                .anyMatch(alias -> alias.equalsIgnoreCase(name));
    }
}
