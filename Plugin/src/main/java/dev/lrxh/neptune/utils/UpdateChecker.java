package dev.lrxh.neptune.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

@UtilityClass
public class UpdateChecker {
    int behindBy = 0;
    private void checkForUpdates() throws IOException, InterruptedException {
        if (!SettingsLocale.CHECK_FOR_UPDATES.getBoolean()) return;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/Solara-Development/Neptune/compare/master..." + GithubUtils.getCommitId()))
                .header("Accept", "application/vnd.github+json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        String status = json.get("status").getAsString();
        if (Objects.equals(status, "behind")) {
            behindBy = json.get("behind_by").getAsInt();
            Neptune.get().getLogger().info("Your Neptune version is behind by " + behindBy + (behindBy == 1 ? " version" : " versions") + "!");
            Neptune.get().getLogger().info("It is recommended to update to the latest version available here: https://github.com/Solara-Development/Neptune#-installation");
        }
    }
    public void run() {
        Bukkit.getScheduler().runTaskAsynchronously(Neptune.get(), () -> {
            try {
                checkForUpdates();
            } catch (Exception e) {
                Neptune.get().getLogger().warning("Failed to check for updates! " + e.getMessage());
            }
        });
    }
}
