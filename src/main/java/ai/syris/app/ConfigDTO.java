package ai.syris.app;

public class ConfigDTO {
    private GeneralSettingsDTO generalSettings = new GeneralSettingsDTO();
    private AudioSettingsDTO audioSettings = new AudioSettingsDTO();
    private KeyboardShortcutsDTO keyboardShortcuts = new KeyboardShortcutsDTO();
    private SpeechModelSettingsDTO speechModelSettings = new SpeechModelSettingsDTO();

    // Checks if any of the child DTOs have changes
    public boolean hasChanges() {
        return generalSettings.hasChanges() ||
//                audioSettings.hasChanges() ||
                keyboardShortcuts.hasChanges() ||
                speechModelSettings.hasChanges();
    }

    public GeneralSettingsDTO getGeneralSettings() {
        return generalSettings;
    }

    public void setGeneralSettings(GeneralSettingsDTO generalSettings) {
        this.generalSettings = generalSettings;
    }

    public AudioSettingsDTO getAudioSettings() {
        return audioSettings;
    }

    public void setAudioSettings(AudioSettingsDTO audioSettings) {
        this.audioSettings = audioSettings;
    }

    public KeyboardShortcutsDTO getKeyboardShortcuts() {
        return keyboardShortcuts;
    }

    public void setKeyboardShortcuts(KeyboardShortcutsDTO keyboardShortcuts) {
        this.keyboardShortcuts = keyboardShortcuts;
    }

    public SpeechModelSettingsDTO getSpeechModelSettings() {
        return speechModelSettings;
    }

    public void setSpeechModelSettings(SpeechModelSettingsDTO speechModelSettings) {
        this.speechModelSettings = speechModelSettings;
    }
}

class GeneralSettingsDTO {
    private String widgetSize = "Large";
    private boolean rememberPosition = false;
    private double microEditorSize = 20;

    // Default values for comparison
    private String defaultWidgetSize = widgetSize;
    private boolean defaultRememberPosition = rememberPosition;
    private double defaultMicroEditorSize = microEditorSize;

    // Commit current values as defaults
    public void commit() {
        defaultWidgetSize = widgetSize;
        defaultRememberPosition = rememberPosition;
        defaultMicroEditorSize = microEditorSize;
    }

    public String getWidgetSize() {
        return widgetSize;
    }

    public void setWidgetSize(String widgetSize) {
        this.widgetSize = widgetSize;
    }

    public boolean isRememberPosition() {
        return rememberPosition;
    }

    public void setRememberPosition(boolean rememberPosition) {
        this.rememberPosition = rememberPosition;
    }

    public double getMicroEditorSize() {
        return microEditorSize;
    }

    public void setMicroEditorSize(double microEditorSize) {
        this.microEditorSize = microEditorSize;
    }

    public String getDefaultWidgetSize() {
        return defaultWidgetSize;
    }

    public void setDefaultWidgetSize(String defaultWidgetSize) {
        this.defaultWidgetSize = defaultWidgetSize;
    }

    public boolean isDefaultRememberPosition() {
        return defaultRememberPosition;
    }

    public void setDefaultRememberPosition(boolean defaultRememberPosition) {
        this.defaultRememberPosition = defaultRememberPosition;
    }

    public double getDefaultMicroEditorSize() {
        return defaultMicroEditorSize;
    }

    public void setDefaultMicroEditorSize(double defaultMicroEditorSize) {
        this.defaultMicroEditorSize = defaultMicroEditorSize;
    }

    // Check if any field is different from the committed/default state
    public boolean hasChanges() {
        return !widgetSize.equals(defaultWidgetSize) ||
                rememberPosition != defaultRememberPosition ||
                microEditorSize != defaultMicroEditorSize;
    }
}

class AudioSettingsDTO {
    private int micVolume = 50;
    private String selectedMicrophone = "--- SELECT MIC ---";

    // Default values for comparison
    private int defaultMicVolume = micVolume;
    private String defaultSelectedMicrophone = selectedMicrophone;

    public void commit() {
        defaultMicVolume = micVolume;
        defaultSelectedMicrophone = selectedMicrophone;
    }

    public int getMicVolume() {
        return micVolume;
    }

    public void setMicVolume(int micVolume) {
        this.micVolume = micVolume;
    }

    public String getSelectedMicrophone() {
        return selectedMicrophone;
    }

    public void setSelectedMicrophone(String selectedMicrophone) {
        this.selectedMicrophone = selectedMicrophone;
    }

    public int getDefaultMicVolume() {
        return defaultMicVolume;
    }

    public void setDefaultMicVolume(int defaultMicVolume) {
        this.defaultMicVolume = defaultMicVolume;
    }

    public String getDefaultSelectedMicrophone() {
        return defaultSelectedMicrophone;
    }

    public void setDefaultSelectedMicrophone(String defaultSelectedMicrophone) {
        this.defaultSelectedMicrophone = defaultSelectedMicrophone;
    }

    public boolean hasChanges() {
        return micVolume != defaultMicVolume ||
                !selectedMicrophone.equals(defaultSelectedMicrophone);
    }
}

class KeyboardShortcutsDTO {
    private String micToggleShortcut = "F4";

    // Default value for comparison
    private String defaultMicToggleShortcut = micToggleShortcut;

    public void commit() {
        defaultMicToggleShortcut = micToggleShortcut;
    }

    public boolean hasChanges() {
        return !micToggleShortcut.equals(defaultMicToggleShortcut);
    }

    public String getMicToggleShortcut() {
        return micToggleShortcut;
    }

    public void setMicToggleShortcut(String micToggleShortcut) {
        this.micToggleShortcut = micToggleShortcut;
    }

    public String getDefaultMicToggleShortcut() {
        return defaultMicToggleShortcut;
    }

    public void setDefaultMicToggleShortcut(String defaultMicToggleShortcut) {
        this.defaultMicToggleShortcut = defaultMicToggleShortcut;
    }
}

class SpeechModelSettingsDTO {
    private String specialty = "Radiology";

    // Default value for comparison
    private String defaultSpecialty = specialty;

    public void commit() {
        defaultSpecialty = specialty;
    }

    public boolean hasChanges() {
        return !specialty.equals(defaultSpecialty);
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getDefaultSpecialty() {
        return defaultSpecialty;
    }

    public void setDefaultSpecialty(String defaultSpecialty) {
        this.defaultSpecialty = defaultSpecialty;
    }
}