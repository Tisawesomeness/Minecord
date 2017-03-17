package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.HeapUtils;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DumpCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"dump",
			"Dumps a memory heap to file. **This might break things, make sure you know what you are doing.**",
			null,
			null,
			0,
			true,
			true,
			false
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		HeapUtils.dumpHeap("dump/heap.bin", true);
		return new Result(Outcome.SUCCESS, ":white_check_mark: Memory usage dumped.");
	}
	
}
