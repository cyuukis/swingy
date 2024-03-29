package ft.school21.swing.java.controller;

import ft.school21.swing.java.Main;
import ft.school21.swing.java.StartGame;
import ft.school21.swing.java.database.ImplementDB;
import ft.school21.swing.java.model.Enemy;
import ft.school21.swing.java.model.GameActions;
import ft.school21.swing.java.model.Map;
import ft.school21.swing.java.model.PlayArmor.DarkArmor;
import ft.school21.swing.java.model.PlayArmor.DragonScaleArmor;
import ft.school21.swing.java.model.PlayArmor.RagsArmor;
import ft.school21.swing.java.model.PlayArmor.WolfArmor;
import ft.school21.swing.java.model.PlayHelm.DarkHelm;
import ft.school21.swing.java.model.PlayHelm.DragonScaleHelm;
import ft.school21.swing.java.model.PlayHelm.RagsHelm;
import ft.school21.swing.java.model.PlayHelm.WolfHelm;
import ft.school21.swing.java.model.PlayRaces.*;
import ft.school21.swing.java.model.Repositor.Classes;
import ft.school21.swing.java.model.Weapons.*;
import ft.school21.swing.java.view.ChoiceGame;
import ft.school21.swing.java.view.Console;
import ft.school21.swing.java.view.Gui;
import org.hibernate.type.YesNoType;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Controller {

    public static Scanner scanner = new Scanner(System.in);
    public static Long idDel;
    public static String createName;
    public static int createRace;
    public static int createClass;
    public static String createMove;
    public static String createYesNo;
    public static boolean flagDEl;

    public void EnterClass(GameActions newPlayer)
    {
        int classPl;
        if (!Main.flagGui)
            classPl = scanner.nextInt();
        else
            classPl = Controller.createClass;
        switch (classPl)
        {
            case 1:
                newPlayer.setPlayClasses(Classes.Warrior);
                break;
            case 2:
                newPlayer.setPlayClasses(Classes.Wizard);
                break;
            case 3:
                newPlayer.setPlayClasses(Classes.Berserk);
                break;
            case 4:
                newPlayer.setPlayClasses(Classes.Necromancer);
                break;
            default:
                System.err.println("selection range from 1-4");
                System.exit(1);
                break;
        }
    }

    public void EnterName(GameActions newPlayer)
    {
        if (!Main.flagGui) {
            newPlayer.setName(scanner.next());
            scanner.nextLine();
        }
        else {
            newPlayer.setName(createName);
            Main.flagGui = false;
        }
    }

    public void EnterRace(GameActions newPlayer)
    {
        int racePl = 0;
        if (!Main.flagGui)
            racePl = scanner.nextInt();
        else
            racePl = Controller.createRace;
        switch (racePl)
        {
            case 1:
                newPlayer.setPlayRaces(new DarkElf());
                break;
            case 2:
                newPlayer.setPlayRaces(new Dwarf());
                break;
            case 3:
                newPlayer.setPlayRaces(new Elf());
                break;
            case 4:
                newPlayer.setPlayRaces(new Human());
                break;
            case 5:
                newPlayer.setPlayRaces(new Orc());
                break;
            default:
                System.err.println("selection range from 1-5");
                System.exit(1);
                break;
        }
    }

    public void DeletePlayer(ArrayList<GameActions> players)
    {
            if (Main.flagGui != true) {
                try {
                    Long iterPlayer = scanner.nextLong();
                    for (int i = 0; i < players.size(); i++) {
                        if (iterPlayer == players.get(i).getId())
                            ImplementDB.getImplementDB().DeleteHero(iterPlayer);
                    }
                }
                catch (InputMismatchException ex)
                {
                }
            } else {
                for (int i = 0; i < players.size(); i++) {
                    if (Controller.idDel == players.get(i).getId()) {
                        ImplementDB.getImplementDB().DeleteHero(Controller.idDel);
                        flagDEl = false;
                        break;
                    }
                    flagDEl = true;
                }
            }
    }

    public boolean MovePlayer(GameActions player, Map map, ChoiceGame choiceGame)
    {
        String command = null;
        if (!Main.flagGui)
            command = scanner.nextLine();
        else
            command = Controller.createMove;
        if (command.toLowerCase().equals("a")) // left
        {
            player.MinusPositionX(map);
        }
        else if (command.toLowerCase().equals("d")) // right
        {
            player.PlusPositionX(map);
        }
        else if (command.toLowerCase().equals("w")) // up
        {
            player.MinusPositionY(map);
        }
        else if (command.toLowerCase().equals("s")) // down
        {
            player.PlusPositionY(map);
        }
        else if (command.toLowerCase().equals("i"))
        {
//            System.out.println(player.toString());
            Main.flagGui = false;
            choiceGame.getView().DataPlayer(player);
            StartGame.Inf();
        }
        else if (command.toLowerCase().equals("g"))
        {
            Main.flagGui = false;
            if (!StartGame.gui)
            {
                StartGame.gui = true;
                choiceGame = new ChoiceGame(new Gui());
            }
            else
            {
                StartGame.gui = false;
                choiceGame = new ChoiceGame(new Console());
            }
            return false;
        }
        else
            return false;
        return true;
    }

    public boolean ChoiceBattle(ChoiceGame choiceGame)
    {
        Main.flagGui = false;
        choiceGame.getView().StartBattle();
        StartGame.Inf();
        String command = null;
        while (true) {
            if (!Main.flagGui)
                command = scanner.nextLine();
            else
                command = createYesNo;
            if (command.toLowerCase().equals("y")) {
                return true;
            } else if (command.toLowerCase().equals("n")) {
                return new Random().nextBoolean();
            } else {
                System.err.println("Only y | n !!!");
                continue;
            }
        }
    }

    private void RandomChoice(int i, char c, GameActions player)
    {
        if (c == 'w')
        {
            switch (i)
            {
                case 1:
                    player.setPlayWeapon(new Bow());
                    break;
                case 2:
                    player.setPlayWeapon(new Mace());
                    break;
                case 3:
                    player.setPlayWeapon(new MagicWand());
                    break;
                case 4:
                    player.setPlayWeapon(new Staff());
                    break;
                case 5:
                    player.setPlayWeapon(new Sword());
                    break;
            }
        }
        else if (c == 'h')
        {
            switch (i)
            {
                case 1:
                    player.setPlayHelm(new DarkHelm());
                    break;
                case 2:
                    player.setPlayHelm(new DragonScaleHelm());
                    break;
                case 3:
                    player.setPlayHelm(new RagsHelm());
                    break;
                case 4:
                    player.setPlayHelm(new WolfHelm());
                    break;
            }
        }
        else
        {
            switch (i)
            {
                case 1:
                    player.setPlayArmor(new DarkArmor());
                    break;
                case 2:
                    player.setPlayArmor(new DragonScaleArmor());
                    break;
                case 3:
                    player.setPlayArmor(new RagsArmor());
                    break;
                case 4:
                    player.setPlayArmor(new WolfArmor());
                    break;
            }
        }
    }
    private void RandomChoiceArtifact(GameActions player)
    {
        Random random = new Random();
        int rand = random.nextInt(4) + 1;
        if (rand == 2)
        {
            int randWeapon = random.nextInt(5) + 1;
            RandomChoice(randWeapon, 'w', player);
        }
        else if (rand == 3)
        {
            int randHelm = random.nextInt(4) + 1;
            RandomChoice(randHelm, 'h', player);
        }
        else if (rand == 4)
        {
            int randWeapon = random.nextInt(4) + 1;
            RandomChoice(randWeapon, 'a', player);
        }
    }

    public boolean RandomBattle(Map map, GameActions player, ChoiceGame choiceGame) {
        Random random = new Random();
        Enemy enemy = new Enemy(player);

        while (true)
        {
            int i = random.nextInt(6) + 1;
            Main.flagGui = false;
            choiceGame.getView().RandomCube(i);
            StartGame.Inf();
            if (i == 1 || i == 2 || i == 3 || i == 4)
                player.Attack(enemy);
            else
                enemy.Attack(player);
            if (player.getHP() == 0)
                return false;
            else if (enemy.getHP() == 0)
            {
                player.setHP(player.getMaxHP());
                player.setExperience(player.getExperience() + enemy.getLevel() * 342 + 1021);
                player.LevelMap(map);
                RandomChoiceArtifact(player);
                ImplementDB.getImplementDB().UpdateHeroDB(player);
                return true;
            }
        }
    }
}
