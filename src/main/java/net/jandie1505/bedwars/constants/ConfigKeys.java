package net.jandie1505.bedwars.constants;

public interface ConfigKeys {
    String INTEGRATIONS_SECTION = "integrations";
    String CLOUD_SYSTEM_SECTION = "cloudsystem";
    String GAME_SECTION = "game";

    String TESTING_MODE = "testing_mode";

    String INTEGRATION_CLOUDNET = INTEGRATIONS_SECTION + ".cloudnet";
    String INTEGRATION_SUPERVANISH = INTEGRATIONS_SECTION + ".supervanish";
    String INTEGRATION_PARTY_AND_FRIENDS = INTEGRATIONS_SECTION + ".partyandfriends";
    String INTEGRATION_PLAYERPOINTS = INTEGRATIONS_SECTION + ".playerpoints";
    String INTEGRATION_PLAYERLEVELS  = INTEGRATIONS_SECTION + ".playerlevels";

    String CLOUDSYSTEM_ENABLE = CLOUD_SYSTEM_SECTION + ".enable";
    String CLOUDSYSTEM_INGAME_COMMAND = CLOUD_SYSTEM_SECTION + ".switch_to_ingame_command";
}
