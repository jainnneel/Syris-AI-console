package ai.syris.app;

import java.net.URL;

public class Test {

    void test() {
        ClassLoader classLoader = Main.class.getClassLoader();
        URL resourceURL = classLoader.getResource("styles.css");
        System.out.println(resourceURL.getPath());
    }

    public static void main(String[] args) {

        new Test().test();

    }
}
