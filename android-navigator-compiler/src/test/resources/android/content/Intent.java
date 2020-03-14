package android.content;

/**
 * Dummy Intent class used only for unit tests as 'java-library' modules cannot be
 * dependent on android dependencies.
 */
public class Intent {
    public Intent(Context packageContext, Class<?> cls) {
    }

    public Intent putExtra(String name, int value) {
        return this;
    }
    public int getIntExtra(String name, int defaultValue) {
        return 0;
    }
}