import com.csz.tool.userTool;
import com.csz.ui.LogIn;

public class Main {
    public static void main(String[] args) {
        LogIn logIn = new LogIn();
        logIn.start();

        System.out.println(userTool.verifyPassword("z#11"));

    }
}
