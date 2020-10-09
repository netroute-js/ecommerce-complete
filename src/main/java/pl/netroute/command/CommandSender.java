package pl.netroute.command;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class CommandSender {
    private final CommandGateway commandGateway;

    public void sendSyncCommand(Command command) {
        commandGateway
                .sendAndWait(
                        command,
                        2L,
                        TimeUnit.SECONDS
                );
    }

}
