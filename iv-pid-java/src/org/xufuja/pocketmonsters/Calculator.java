package org.xufuja.pocketmonsters;

import org.xufuja.Counter;
import org.xufuja.ui.*;

public class Calculator {
    private static final long MULTIPLIER = 1103515245L;
    private static final long INVERSEMULTIPLIER = 4005161829L;
    private static final long INCREMENT = 24691;
    private UserInput userInput;

    public Calculator(UserInput userInput) {
        this.userInput = userInput;
    }

    public int randomNumberGenerator(Counter state) {
        long temp = state.getCount() * MULTIPLIER + INCREMENT;
        state.setCount(Integer.toUnsignedLong((int) temp));

        return (int) (state.getCount() >>> 16);
    }

    public int antiRandomNumberGenerator(Counter state) {
        long temp = INVERSEMULTIPLIER * (state.getCount() - INCREMENT);
        state.setCount(Integer.toUnsignedLong((int) temp));

        return (int) (state.getCount() >>> 16);
    }

    public boolean minIvTest(int n, int hp, int at, int df) {
        return (((31 << 10) & n) >= (df << 10)) && (((31 << 5) & n) >= (at << 5)) && ((31 & n) >= hp);
    }

    public boolean exactIvTest(int n, int hp, int at, int df) {
        int first = (0x7FFF & n);
        int second = ((df << 10) | (at << 5) | hp);
        return first == second;
    }

    /*boolean GetIVtester(boolean exact) {
        return exact ? exactIvTest() : minIvTest();
    }*/
    public boolean pidTest(long pid, int nature, int ability) {

        long natureMod = pid % 25;
        long abilityMod = pid % 2;
        boolean hit = false;

        return (nature == -1 || (int) natureMod == nature) && (ability == 2 || (int) abilityMod == ability);
    }

    public boolean hiddenPowerPreTest(int iv, int hpt, int hpp) {
        boolean typeOk = hpt == -1;
        boolean powerOk = hpp == -1;

        if (typeOk && powerOk) {
            return true;
        }
        int spa = ((iv >> 5) & 31), spd = ((iv >> 10) & 31), spe = (iv & 31);
        if (!typeOk) {
      /* Checks if the wanted type is one of
         the possible types from the known IVs
      */
            int hp_type = (((spe & 1) << 3) | ((spa & 1) << 4) | ((spd & 1) << 5)); // NOT the type ID! type ID is hp_type*15/63
      /* The wanted type is possible iff the wanted type range and
         the hp_type values range (which is 8 consecutive values
         as HP, Attack and Defense IVs can only increase hp_type
         by 1, 2 and 4 each or leave it as it is) overlap
      */
            typeOk = hp_type * 15 / 63 <= hpt && (hp_type | 7) * 15 / 63 >= hpt;
            if (!typeOk)
                return false;
        }
        if (!powerOk) {
            /* Checks if the maximum possible HP power from the known IVs is enough */
            int max_possible_hp_power = ((((spe & 2) << 2) | ((spa & 2) << 3) | ((spd & 2) << 4)) | 7) * 40 / 63 + 30;
            powerOk = max_possible_hp_power >= hpp;
            if (!powerOk)
                return false;
        }
        return true;
    }

    public boolean hiddenPowerTest(int iv1, int iv2, int hpt, int hpp) {
        boolean typeOk = hpt == -1;
        boolean powerOk = hpp == -1;
        if (typeOk && powerOk)   // any HP is allowed
            return true;

        int hp = (iv1 & 31), at = ((iv1 >> 5) & 31), df = ((iv1 >> 10) & 31), spa = ((iv2 >> 5) & 31), spd = ((iv2 >> 10) & 31), spe = (iv2 & 31);
        if (!typeOk) {
            int hp_type = ((hp & 1) | ((at & 1) << 1) | ((df & 1) << 2) | ((spe & 1) << 3) | ((spa & 1) << 4) | ((spd & 1) << 5)) * 15 / 63;
            typeOk = hp_type == hpt;
            if (!typeOk)
                return false;
        }
        if (!powerOk) {
            int hp_power = (((hp & 2) >> 1) | (at & 2) | ((df & 2) << 1) | ((spe & 2) << 2) | ((spa & 2) << 3) | ((spd & 2) << 4)) * 40 / 63 + 30;
            powerOk = hp_power >= hpp;
            if (!powerOk)
                return false;
        }
        return true;
    }

    public boolean xorTest(int pid_l, int pid_h, int IdXorSid) {
        return IdXorSid == 1 || ((pid_l ^ pid_h ^ IdXorSid) & 0xFFF8) == 0;
    }

    public void findPid(Counter useSeed, int iv1, int iv2, PocketMonsterData pData, int method, Counter count) {
        int pid_h, pid_l;
        pid_h = antiRandomNumberGenerator(useSeed);
        pid_l = antiRandomNumberGenerator(useSeed);
        long pid = (pid_l | ((long) pid_h << 16));

        for (int i = 0; i < 2; pid ^= 0x80008000L, i++)
            if (pidTest(pid, pData.getNature(), pData.getAbility())
                    && hiddenPowerTest(iv1, iv2, pData.getHiddenPowerType(), pData.getHiddenPowerPower())
                    && xorTest(pid_l, pid_h, pData.getIdXorSid()))
                userInput.print(pid, iv1, iv2, method, (int) count.incrementCountPre());
    }

    public void testAllPossibleSeedsBackwards(PocketMonsterData pData, int gba, boolean exact, Counter count) {

        for (int spa_ = (exact ? pData.getSpecialAttack() : 31); spa_ >= pData.getSpecialAttack(); spa_--)
            for (int spd_ = (exact ? pData.getSpecialDefense() : 31); spd_ >= pData.getSpecialDefense(); spd_--)
                for (int spe_ = (exact ? pData.getSpeed() : 31); spe_ >= pData.getSpeed(); spe_--) {
                    long high_seed = (long) (spe_ | ((long) spa_ << 5) | ((long) spd_ << 10)) << 16;
                    for (long low_seed = 0; low_seed < 65536; low_seed++)
                        generalTest(low_seed | high_seed, pData, gba, exact, count);
                }
    }

    public void testAllPossibleSeedsForwards(long pid, int gba, Counter count) {
        long high_seed = Integer.toUnsignedLong((int) pid << 16); // Any RNG state that leads to the PID must start with this
        int high_pid = (int) pid >>> 16;
        for (long low_seed = 0; low_seed < 65536; low_seed++)
            if (highPidMatches(low_seed | high_seed, high_pid))
                getFromSeed(low_seed | high_seed, count, gba);
    }

    public boolean highPidMatches(long state, int pid_h) {
        Counter useState = new Counter(state);
        return randomNumberGenerator(useState) == pid_h;
    }

    public void getFromSeed(long seed, Counter count, int gba) {
        Counter useSeed = new Counter(seed);
        long pid = ((useSeed.getCount() >>> 16) | ((long) randomNumberGenerator(useSeed) << 16));
        int n2, n3;
        n2 = randomNumberGenerator(useSeed);
        n3 = randomNumberGenerator(useSeed);
        userInput.print(pid, n2, n3, 0, (int) count.incrementCountPre());

        if (gba == 1) {
            int n4;
            n4 = randomNumberGenerator(useSeed);
            userInput.print(pid, n3, n4, 1, (int) count.incrementCountPre());
            userInput.print(pid, n2, n4, 2, (int) count.incrementCountPre());

            if (gba > 1) {
                int n5;
                n5 = randomNumberGenerator(useSeed);
                userInput.print(pid, n3, n5, 3, (int) count.incrementCountPre());
                userInput.print(pid, n4, n5, 4, (int) count.incrementCountPre());
            }
        }
    }

    public void findChainedPid(Counter useSeed, int iv1, int iv2, PocketMonsterData pData, Counter count) {
        int pid_corrector = 0;
        for (int i = 15; i >= 3; i--)
            pid_corrector |= ((antiRandomNumberGenerator(useSeed) & 1) << i);

        int pid_h, pid_l;
        pid_h = ((antiRandomNumberGenerator(useSeed) & 7) | ((pData.getIdXorSid() ^ pid_corrector) & (~7)));
        pid_l = ((antiRandomNumberGenerator(useSeed) & 7) | pid_corrector);
        long pid = (pid_l | ((long) pid_h << 16));
        if (pidTest(pid, pData.getNature(), pData.getAbility()))
            userInput.print(pid, iv1, iv2, -1, (int) count.incrementCountPre());
    }

    public void generalTest(long seed, PocketMonsterData pData, int gba, boolean exact, Counter count) {

        Counter useSeed = new Counter(seed);
        int n2 = (int) (useSeed.getCount() >>> 16);
        if (!hiddenPowerPreTest(n2, pData.getHiddenPowerType(), pData.getHiddenPowerPower()))
            return;

        int n1 = (int) antiRandomNumberGenerator(useSeed);

        if (exact ? exactIvTest(n1, pData.getHp(), pData.getAttack(), pData.getDefense()) : minIvTest(n1, pData.getHp(), pData.getAttack(), pData.getDefense())) {
            if (gba == -1)
                findChainedPid(useSeed, n1, n2, pData, count);

            findPid(useSeed, n1, n2, pData, 0, count);
            if (gba == 1) {
                Counter copySeed = new Counter(useSeed.getCount());
                antiRandomNumberGenerator(copySeed);
                findPid(copySeed, n1, n2, pData, 1, count);

                if (gba > 1) {
                    antiRandomNumberGenerator(copySeed);
                    findPid(copySeed, n1, n2, pData, 4, count);
                }
            }
        }

        if (gba == 1) {
            n1 = antiRandomNumberGenerator(useSeed);
            if (exact ? exactIvTest(n1, pData.getHp(), pData.getAttack(), pData.getDefense()) : minIvTest(n1, pData.getHp(), pData.getAttack(), pData.getDefense())) {
                findPid(useSeed, n1, n2, pData, 2, count);

                if (gba > 1) {
                    antiRandomNumberGenerator(useSeed);
                    findPid(useSeed, n1, n2, pData, 3, count);
                }
            }
        }
    }
}
