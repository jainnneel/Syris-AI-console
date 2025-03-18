package ai.syris.app;

public class ConfigMapper {

    public static Config toEntity(ConfigDTO dto, User user) {
        if (dto == null) {
            return null;
        }

        Config config = new Config();

        // Mapping GeneralSettingsDTO
        GeneralSettingsDTO generalSettings = dto.getGeneralSettings();
        if (generalSettings != null) {
            config.setWidgetPosition(generalSettings.isRememberPosition());
            // Assume widgetX & widgetY need to be retrieved from somewhere
            config.setWidgetX(0.0);
            config.setWidgetY(0.0);
        }

        // Mapping AudioSettingsDTO
        AudioSettingsDTO audioSettings = dto.getAudioSettings();
        if (audioSettings != null) {
            config.setMicrophoneVolume(audioSettings.getMicVolume());
        }

        // Mapping SpeechModelSettingsDTO
        SpeechModelSettingsDTO speechSettings = dto.getSpeechModelSettings();
        if (speechSettings != null) {
            config.setMedicalSpeciality(mapMedicalSpeciality(speechSettings.getSpecialty()));
        }

        return config;
    }

    private static Config.MedicalSpeciality mapMedicalSpeciality(String specialty) {
        if (specialty == null) return null;
        switch (specialty.toUpperCase()) {
            case "RADIOLOGY":
                return Config.MedicalSpeciality.RADIOLOGY;
            default:
                throw new IllegalArgumentException("Unsupported Medical Specialty: " + specialty);
        }
    }

}
