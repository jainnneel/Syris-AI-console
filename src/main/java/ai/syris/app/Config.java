package ai.syris.app;


public class Config {

    private Long id;
    private User user;
    private String widgetSize;
    private boolean widgetPosition;
    private double widgetX;
    private double widgetY;
    private int microphoneVolume;
    private String micToggleShortcut;
    private BackgroundVoiceSuppression backgroundVoiceSuppression;
    private MedicalSpeciality medicalSpeciality;
    private EnglishAccent englishAccent;
    private EnglishSpelling englishSpelling;

    public enum BackgroundVoiceSuppression {
        OFF, LOW, MEDIUM, HIGH, VERY_HIGH
    }

    public enum MedicalSpeciality {
        RADIOLOGY
    }

    public enum EnglishAccent {
        INDIAN, ENGLISH
    }

    public enum EnglishSpelling {
        US, UK
    }

    public String getMicToggleShortcut() {
        return micToggleShortcut;
    }

    public void setMicToggleShortcut(String micToggleShortcut) {
        this.micToggleShortcut = micToggleShortcut;
    }

    public String getWidgetSize() {
        return widgetSize;
    }

    public void setWidgetSize(String widgetSize) {
        this.widgetSize = widgetSize;
    }

    public boolean isWidgetPosition() {
        return widgetPosition;
    }

    public void setWidgetPosition(boolean widgetPosition) {
        this.widgetPosition = widgetPosition;
    }

    public double getWidgetX() {
        return widgetX;
    }

    public void setWidgetX(double widgetX) {
        this.widgetX = widgetX;
    }

    public double getWidgetY() {
        return widgetY;
    }

    public void setWidgetY(double widgetY) {
        this.widgetY = widgetY;
    }

    public int getMicrophoneVolume() {
        return microphoneVolume;
    }

    public void setMicrophoneVolume(int microphoneVolume) {
        this.microphoneVolume = microphoneVolume;
    }

    public BackgroundVoiceSuppression getBackgroundVoiceSuppression() {
        return backgroundVoiceSuppression;
    }

    public void setBackgroundVoiceSuppression(BackgroundVoiceSuppression backgroundVoiceSuppression) {
        this.backgroundVoiceSuppression = backgroundVoiceSuppression;
    }

    public MedicalSpeciality getMedicalSpeciality() {
        return medicalSpeciality;
    }

    public void setMedicalSpeciality(MedicalSpeciality medicalSpeciality) {
        this.medicalSpeciality = medicalSpeciality;
    }

    public EnglishAccent getEnglishAccent() {
        return englishAccent;
    }

    public void setEnglishAccent(EnglishAccent englishAccent) {
        this.englishAccent = englishAccent;
    }

    public EnglishSpelling getEnglishSpelling() {
        return englishSpelling;
    }

    public void setEnglishSpelling(EnglishSpelling englishSpelling) {
        this.englishSpelling = englishSpelling;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
