package me.ayunami2000.ayunHostnameSpoofer;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.connection.client.InitialInboundConnection;
import com.velocitypowered.proxy.connection.client.LoginInboundConnection;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(id = "ayunhostnamespoofer", name = "ayunHostnameSpoofer", version = "1.0-SNAPSHOT",
        url = "https://shhnowisnottheti.me", description = "Allows you to spoof the hostname of all connections to servers.", authors = {"ayunami2000"})
public class Main {
    private final String token;
    // private int port = 0;

    @Inject
    public Main(ProxyServer server, Logger logger) throws IOException {
        Path configFilePath = Path.of("plugins", "ayunHostnameSpoofer", "config.txt");

        File configFileFile = configFilePath.toFile();
        configFileFile.getParentFile().mkdirs();
        if (configFileFile.createNewFile()) Files.write(configFilePath, "spoofed.hostname".getBytes()); // :0

        token = Files.readString(configFilePath).trim();

        /*
        String configFile = Files.readString(configFilePath).trim();

        if (configFile.contains(":")) {
            String[] configPieces = configFile.split(":");

            token = configPieces[0];
            port = Integer.parseInt(configPieces[1]);
        } else {
            token = configFile;
        }
        */
    }

    @Subscribe
    public void onHandshake(ConnectionHandshakeEvent event) throws NoSuchFieldException, IllegalAccessException {
        LoginInboundConnection connectedPlayer = (LoginInboundConnection) event.getConnection();

        Field delegateField = connectedPlayer.getClass().getDeclaredField("delegate");
        delegateField.setAccessible(true);

        InitialInboundConnection initialInboundConnection = (InitialInboundConnection) delegateField.get(connectedPlayer);

        Field addressField = initialInboundConnection.getClass().getDeclaredField("cleanedAddress");
        addressField.setAccessible(true);

        addressField.set(initialInboundConnection, token);

        // the following does NOT work, for whatever reason:

        /*
        Field handshakeField = initialInboundConnection.getClass().getDeclaredField("handshake");
        handshakeField.setAccessible(true);

        Handshake handshake = (Handshake) handshakeField.get(initialInboundConnection);
        handshake.setServerAddress(token);
        handshake.setPort(port);

        // handshakeField.set(initialInboundConnection, handshake);
        */

        // ez fix: just don't check if the port is 0! :D
    }
}