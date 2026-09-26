import com.csz.model.Enemy;
import com.csz.model.Player;
import com.csz.model.User;
import com.csz.ui.Game;
import com.csz.ui.event.Battle;

public class Main {
    public static void main(String[] args) {
//        LogIn logIn = new LogIn();
//        logIn.start();

        Game game = new Game();
        Player player = new Player(new User("csz","csz123"),51,100,30,100,5,10);

//        Battle.encounterBattle(player, new Enemy[]{new Enemy()});

        Battle.roleAttributeUI(player);
        Battle.roleAttributeUI(new Enemy());
    }
}
