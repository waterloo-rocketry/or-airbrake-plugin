// from: https://github.com/stwind/gradle-jni

package jni;

public class Tools {
    static {
        LibraryLoader.load(Tools.class, "tools");
    }

    public String foo() {
        return "foo";
    }

    public native String bar();
}