package org.example.myapp;

import lombok.Data;

@Data
public class ParentDTO {
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
}

@Data
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

    // Check if any field is different from the committed/default state
    public boolean hasChanges() {
        return !widgetSize.equals(defaultWidgetSize) ||
                rememberPosition != defaultRememberPosition ||
                microEditorSize != defaultMicroEditorSize;
    }
}

@Data
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

    public boolean hasChanges() {
        return micVolume != defaultMicVolume ||
                !selectedMicrophone.equals(defaultSelectedMicrophone);
    }
}

@Data
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
}

@Data
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
}