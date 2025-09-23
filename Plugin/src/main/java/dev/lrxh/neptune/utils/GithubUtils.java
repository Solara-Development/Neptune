package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.Properties;

@UtilityClass
public class GithubUtils {

    private String commitId = "UNKNOWN";
    private String commitMessage = "UNKNOWN";

    public void loadGitInfo() {
        try (InputStream input = GithubUtils.class.getClassLoader().getResourceAsStream("git.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);

                commitId = props.getProperty("git.commit.id.abbrev", "UNKNOWN");
                commitMessage = props.getProperty("git.commit.message.short", "UNKNOWN");

                ServerUtils.info("Loaded Git info: " + commitId + " - " + commitMessage);
            } else {
                ServerUtils.info("git.properties not found in resources!");
            }
        } catch (Exception e) {
            ServerUtils.error("Failed to load git.properties: " + e.getMessage());
        }
    }

    public String getCommitId() {
        return commitId;
    }

    public String getCommitMessage() {
        return commitMessage;
    }
}
