package org.xufuja.ui;

import org.xufuja.Counter;
import org.xufuja.pocketmonsters.*;

import java.util.Scanner;
import java.util.stream.Stream;

public class UserInput {
    private Scanner scanner;
    private Calculator calculator;
    private String programName = "IV⇆ PID";
    private String version = "v0.1.1";
    private String bar = "\n=================================\n";
    private String git = "https://github.com/josemam/IV-PID";

    private Character yes = 'y';
    private Character no = 'n';
    private String chainedShinyMethod = "Chained shiny";
    private String[] methods = new String[]{"A-B-C-D", "A-B-D-E", "A-B-C-E", "A-B-D-F", "A-B-E-F"};
    private String[] natures = new String[]{"Hardy", "Lonely", "Brave", "Adamant", "Naughty", "Bold",
            "Docile", "Relaxed", "Impish", "Lax", "Timid", "Hasty",
            "Serious", "Jolly", "Naive", "Modest", "Mild", "Quiet",
            "Bashful", "Rash", "Calm", "Gentle", "Sassy", "Careful", "Quirky"};
    private String[] hiddenPowerTypes = new String[]{"Fighting", "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel",
            "Fire", "Water", "Grass", "Electric", "Psychic", "Ice", "Dragon", "Dark"};
    private String[] modes = new String[]{"0: Exit", "1: IV --> PID", "2: Minimum IV + HP --> PID",
            "3: Minimum IV + ID + SID --> shiny PID", "4: Minimum IV + HP + ID + SID --> shiny PID",
            "5: IV + ID + SID --> chained shiny PID", "6: PID --> IV", "7: Shiny PID + ID --> SID"};
    private String askPid = "PID, 90 to enter hex value";
    private String askId = "ID";
    private String askSid = "SID";
    private String askIv = "HP, Attack, Defense, Special Attack, Special Defense & Speed IVs";
    private String askNature = "Nature ID (-1 if any, 90 to show list)";
    private String askHiddenPowerType = "HP Type ID (-1 if any, 90 to show list)";
    private String askHiddenPowerPower = "Minimum HP Power (-1 if any)";
    private String askHiddenAbility = "Ability? (n = any, 1 = first, 2 = second)";
    private String askGba = "Test GBA methods?";
    private String askGbaRare = "Even rare ones? (The author does not know whether they are possible)";
    private String ability = "Ability";
    private String[] abilities = new String[]{"First", "Second"};
    private String genderValue = "Gender value";
    private String hiddenPower = "Hidden Power";
    private String noPid = "No valid PID found";
    private String impossiblePid = "That PID is impossible due to RNG mechanism";
    private String results = "End of results";
    private String pause = "Press any key to continue or Esc to exit...";
    private String license = "License";
    private String beforeGit = "You can read the code, make\nsuggestions and report bugs at";

    public UserInput(Scanner scanner) {
        this.scanner = scanner;
        this.calculator = new Calculator(this);
    }

    public void welcome() {
        System.out.println(String.format("%1$s\n%2$s %3$s - %4$s GPLv2\n%5$s:\n%6$s", this.bar, this.programName, this.version, this.license, this.beforeGit, this.git));
        askMode();
    }

    public void print(long pid, int iv1, int iv2, int method, int count) {
        if (count != 1 && count % 3 == 1)
            System.out.println("Pause");

        int hp = (iv1 & 31), at = ((iv1 >> 5) & 31), df = ((iv1 >> 10) & 31), spa = ((iv2 >> 5) & 31), spd = ((iv2 >> 10) & 31), spe = (iv2 & 31);
        int hp_power = (((hp & 2) >> 1) | (at & 2) | ((df & 2) << 1) | ((spe & 2) << 2) | ((spa & 2) << 3) | ((spd & 2) << 4)) * 40 / 63 + 30;
        int hp_type = ((hp & 1) | ((at & 1) << 1) | ((df & 1) << 2) | ((spe & 1) << 3) | ((spa & 1) << 4) | ((spd & 1) << 5)) * 15 / 63;
        System.out.println(String.format("%1$s\n%2$s: %3$d (%20$s) Method: %19$s\n%4$s: %5$s\nIVs/Nat: %6$s %7$s %8$s %9$s %10$s %11$s/%12$s\n%13$s: %14$s\n%15$s: %16$s - %17$s\n%18$s", this.bar, count, pid, this.ability, this.abilities[(int) Long.remainderUnsigned(pid, 2)], hp, at, df, spa, spd, spe, this.natures[(int) Long.remainderUnsigned(pid, 25)], this.genderValue, pid & 255, this.hiddenPower, this.hiddenPowerTypes[hp_type], hp_power, this.bar, method == -1 ? this.chainedShinyMethod : this.methods[method], Long.toHexString(pid).toUpperCase()));
    }

    public void askMode() {
        boolean exit = false;
        int input = -1;
        while (!exit) {
            System.out.println(this.bar);
            String modes = "";
            for (String mode : this.modes) {
                modes += mode + "\n";
            }
            System.out.println(modes);
            input = Integer.valueOf(scanner.nextLine());
            switch (input) {
                case 1: {
                    ivToPid(false, false);
                    break;
                }
                case 2: {
                    ivToPid(true, false);
                    break;
                }
                case 3: {
                    ivToPid(false, true);
                    break;
                }
                case 4: {
                    ivToPid(true, true);
                    break;
                }
                case 5: {
                    ivToChainedShinyPid();
                    break;
                }
                case 6: {
                    pidtoIv();
                    break;
                }
                case 7: {
                    getSid();
                    break;
                }
                case 0: {
                    exit = true;
                    break;
                }
                default: {
                    System.out.println("That is not an option!");
                    break;
                }
            }
        }
    }

    public void ivToPid(boolean fixedHiddenPower, boolean shiny) {
        boolean exact = !fixedHiddenPower && !shiny;
        int[] iv = askIv();
        PocketMonsterData pocketMonsterData = new PocketMonsterData(iv[0], iv[1], iv[2], iv[3], iv[4], iv[5]);
        pocketMonsterData.setNature(askNature());
        pocketMonsterData.setAbility(askAbility());

        int gba = askMethod();
        if (fixedHiddenPower) {
            pocketMonsterData.setHiddenPowerType(askHiddenPowerType());
            pocketMonsterData.setHiddenPowerPower(askHiddenPowerPower());
        }
        if (shiny) {
            int id = askId(false);
            int sid = askSid();
            pocketMonsterData.setIdXorSid((id ^ sid) & 0xFFF8);
        }
        Counter count = new Counter(0);
        this.calculator.testAllPossibleSeedsBackwards(pocketMonsterData, gba, exact, count);

    }

    public void ivToChainedShinyPid() {
        boolean exact = true;
        int[] iv = askIv();
        PocketMonsterData pocketMonsterData = new PocketMonsterData(iv[0], iv[1], iv[2], iv[3], iv[4], iv[5]);
        pocketMonsterData.setNature(askNature());
        pocketMonsterData.setAbility(askAbility());
        int id = askId(false);
        int sid = askSid();
        pocketMonsterData.setIdXorSid((id ^ sid) & 0xFFF8);

        Counter count = new Counter(0);
        this.calculator.testAllPossibleSeedsBackwards(pocketMonsterData, -1, exact, count);
    }

    public void pidtoIv() {
        long pid = askPid();
        int gba = askMethod();

        Counter count = new Counter(0);
        this.calculator.testAllPossibleSeedsForwards(pid, gba, count);
    }

    public void getSid() {
        long pid = askPid();
        int id = askId(false);

        showSidRange(maxSidforShiny(pid, id));

    }

    public void showSidRange(int sidMax) {
        System.out.println(String.format("SID: %1$s ~ %2$s", sidMax - 7, sidMax));
    }

    public int maxSidforShiny(long pid, int id) {
        int pid_h = (int) pid >>> 16;
        int pid_l = Short.toUnsignedInt((short) pid);

        return ((pid_h ^ pid_l) ^ id) | 7;
    }

    public int[] askIv() {
        boolean exit = false;
        String input = "";
        while (!exit) {
            System.out.println(String.format("%1$s:", this.askIv));
            input = this.scanner.nextLine();
            if (input.split(" ").length == 6) {
                exit = true;
            } else {
                System.out.println("Not valid!");
            }
        }
        return Stream.of(input.split(" ")).mapToInt(Integer::parseInt).toArray();
    }

    public long askPid() {
        boolean exit = false;
        long input = -1;
        while (!exit) {
            System.out.println(String.format("%1$s:", this.askPid));
            input = Long.parseLong(this.scanner.nextLine());
            if (input == 90) {
                input = hexToDecimal(this.scanner);
                exit = true;
            } else if (input < 0 || input > 4294967295L) {
                System.out.println("Not valid!");
            } else {
                exit = true;
            }
        }
        return input;
    }

    public long hexToDecimal(Scanner scanner) {
        System.out.println("Enter hex PID:");
        long out = Long.parseLong(scanner.nextLine(), 16);
        System.out.println("Proceeding with: " + out);
        return out;
    }

    public int askId(boolean sid) {
        boolean exit = false;
        int input = -1;
        while (!exit) {
            System.out.println(String.format("%1$s:", sid ? this.askSid : this.askId));
            input = Integer.parseInt(this.scanner.nextLine());
            if (input < 0 || input > 65535) {
                System.out.println("Not valid!");
            } else {
                exit = true;
            }
        }
        return input;
    }

    public int askSid() {
        return askId(true);
    }

    public int askNature() {
        boolean exit = false;
        int input = -1;
        while (!exit) {
            System.out.println(String.format("%1$s:", this.askNature));
            input = Integer.parseInt(this.scanner.nextLine());
            if (input == 90) {
                showNatures();
            } else if (input < -1 || input > 24) {
                System.out.println("Not valid!");
            } else {
                exit = true;
            }
        }
        return input;
    }

    public void showNatures() {
        String natures = "";
        for (int i = 0; i < this.natures.length; i++) {
            natures += String.format("%1$2s ", i) + this.natures[i] + "\n";
        }
        System.out.println(natures);
    }

    public int askHiddenPowerType() {
        boolean exit = false;
        int input = -1;
        while (!exit) {
            System.out.println(String.format("%1$s:", this.askHiddenPowerType));
            input = Integer.valueOf(this.scanner.nextLine());
            if (input == 90) {
                showHiddenPowerTypes();
            } else if (input < 0 || input > 15) {
                System.out.println("Not valid!");
            } else {
                exit = true;
            }
        }
        return input;
    }

    public void showHiddenPowerTypes() {
        String types = "";
        for (int i = 0; i < this.hiddenPowerTypes.length; i++) {
            types += String.format("%1$2s ", i) + this.hiddenPowerTypes[i] + "\n";
        }
        System.out.println(types);
    }

    public int askHiddenPowerPower() {
        boolean exit = false;
        int input = -1;
        while (!exit) {
            System.out.println(String.format("%1$s:", this.askHiddenPowerPower));
            input = Integer.valueOf(this.scanner.nextLine());
            if (input < -1 || input > 70) {
                System.out.println("Not valid!");
            } else {
                exit = true;
            }
        }
        return input;
    }

    public int askAbility() {
        boolean exit = false;
        char input = ' ';
        while (!exit) {
            System.out.println(String.format("%1$s:", this.askHiddenAbility));
            input = this.scanner.nextLine().charAt(0);
            if (input == 'n' || input == '1' || input == '2') {
                exit = true;
            } else {
                System.out.println("Not valid!");
            }
        }
        int val1 = (input == 'n') ? 1 : 0;
        int val2 = (input != '1') ? 1 : 0;
        return (val1 + val2);
    }

    public int askMethod() {
        boolean rare = false;
        boolean gba = askYesOrNo(this.askGba);
        if (gba) {
            rare = askYesOrNo(this.askGbaRare);
        }
        return (gba ? 1 : 0) + (rare ? 1 : 0);
    }

    public boolean askYesOrNo(String message) {
        boolean exit = false;
        String input = "";
        while (!exit) {
            System.out.println(String.format("%1$s (%2$s/%3$s):", message, this.yes, this.no));
            input = this.scanner.nextLine().toLowerCase();
            if (input.charAt(0) == this.yes || input.charAt(0) == this.no) {
                exit = true;
            } else {
                System.out.println("Not valid!");
            }
        }
        return input.charAt(0) == this.yes;
    }

}
