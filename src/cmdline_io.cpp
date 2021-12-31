/**
  * @file cmdline_io.cpp
  * @brief Command line I/O for iv-pid
  */

#include <iostream>
#include <iomanip>    // padding natures and types
#include <stdint.h>
#include <conio.h>  // getch()
#include "strings.h"
#include "io.h"
using namespace std;

char Y_CHAR = 'y';
char N_CHAR = 'n';
const char
* CHAINED_SHINY_METHOD = "Chained shiny",
* NATURE[25] = {
  "Hardy", "Lonely", "Brave", "Adamant", "Naughty", "Bold",
  "Docile", "Relaxed", "Impish", "Lax", "Timid", "Hasty",
  "Serious", "Jolly", "Naive", "Modest", "Mild", "Quiet",
  "Bashful", "Rash", "Calm", "Gentle", "Sassy", "Careful", "Quirky"
},
* HP_TYPE[16] = {
  "Fighting", "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel",
  "Fire", "Water", "Grass", "Electric", "Psychic", "Ice", "Dragon", "Dark"
},
* MODE_MSG = "0: Exit\n"
"1: IV --> PID\n"
"2: Minimum IV + HP --> PID\n"
"3: Minimum IV + ID + SID --> shiny PID\n"
"4: Minimum IV + HP + ID + SID --> shiny PID\n"
"5: IV + ID + SID --> chained shiny PID\n"
"6: PID --> IV\n"
"7: Shiny PID + ID --> SID\n"
"Mode",
* PID_ASK_MSG = "PID (decimal)",
* ID_ASK_MSG = "ID",
* SID_ASK_MSG = "SID",
* IV_ASK_MSG = "HP, Attack, Defense, Special Attack, Special Defense & Speed IVs",
* NATURE_ASK_MSG = "Nature ID (-1 if any, 90 to show list)",
* HP_TYPE_ASK_MSG = "HP Type ID (-1 if any, 90 to show list)",
* HP_POWER_ASK_MSG = "Minimum HP Power (-1 if any)",
* ABILITY_ASK_MSG = "Ability? (n = any, 1 = first, 2 = second)",
* GBA_ASK_MSG1 = "Test GBA methods?",
* GBA_ASK_MSG2 = "Even rare ones? (The author does not know whether they are possible)",
* ABILITY_TXT = "Ability",
* AB_F_S[2] = { "First", "Second" },
* GEN_VAL_TXT = "Gender value",
* HP_TXT = "Hidden Power",
* NO_PID_MSG = "No valid PID found",
* IMPOSSIBLE_PID_MSG = "That PID is impossible due to RNG mechanism",
* END_OF_RESULTS_MSG = "End of results",
* PAUSE_MSG = "Press any key to continue or Esc to exit...",
* LICENSE = "License",
* TEXT_BEFORE_GH_LINK = "You can read the code, make\nsuggestions and report bugs at";

void Pause() {
	cout << PAUSE_MSG;
	fflush(stdin);
	if (getch() == 27) {
		cout << '\n';
		exit(0);
	}
}

void Print(uint32_t pid, uint16_t iv1, uint16_t iv2, int method, int count) {
	if (count != 1 && count % 3 == 1)
		Pause();

	int hp = (iv1 & 31), at = ((iv1 >> 5) & 31), df = ((iv1 >> 10) & 31), spa = ((iv2 >> 5) & 31), spd = ((iv2 >> 10) & 31), spe = (iv2 & 31);
	int hp_power = (((hp & 2) >> 1) | (at & 2) | ((df & 2) << 1) | ((spe & 2) << 2) | ((spa & 2) << 3) | ((spd & 2) << 4)) * 40 / 63 + 30;
	int hp_type = ((hp & 1) | ((at & 1) << 1) | ((df & 1) << 2) | ((spe & 1) << 3) | ((spa & 1) << 4) | ((spd & 1) << 5)) * 15 / 63;
	cout << BAR << count << ": " << pid << " (" << METHOD[method] << ")\n" << ABILITY_TXT << ": " << AB_F_S[pid % 2]
		<< "\nIVs/Nat: " << hp << "-" << at << "-" << df << "-" << spa << "-" << spd << "-" << spe << "/" << NATURE[pid % 25]
		<< "\n" << GEN_VAL_TXT << ": " << (pid & 255) << "\n" << HP_TXT << ": " << HP_TYPE[hp_type] << "-" << hp_power
		<< BAR;
}

bool AvoidLoops() {
	if (!cin) {
		cin.clear();
		while (cin.get() != '\n') {};
		return true;
	}
	return false;
}

void ShowAllNatures() {
	for (int x = 0; x < 25; x++)
		cout << left << x << (x < 10 ? ":  " : ": ") << setw(11) << NATURE[x] << (x % 5 == 4 ? '\n' : ' ');
}

void ShowAllTypes() {
	for (int x = 0; x < 16; x++)
		cout << left << x << (x < 10 ? ":  " : ": ") << setw(11) << HP_TYPE[x] << (x % 5 == 4 ? '\n' : ' ');

	cout << '\n';
}

void AskIVs(int& hp, int& at, int& df, int& spa, int& spd, int& spe) {
	do {
		cout << IV_ASK_MSG << ": ";
		cin >> hp >> at >> df >> spa >> spd >> spe;
		if (AvoidLoops()) spe = -1;
	} while (hp < 0 || hp > 31 || at < 0 || at > 31 || df < 0 || df > 31 || spa < 0 || spa > 31
		|| spd < 0 || spd > 31 || spe < 0 || spe > 31);
}

uint32_t AskPID() {
	uint32_t pid;
	do {
		cout << PID_ASK_MSG << ": ";
		cin >> pid;
	} while (AvoidLoops());
	return pid;
}

int AskNature() {
	int nature;
	do {
		cout << NATURE_ASK_MSG << ": ";
		cin >> nature;
		if (AvoidLoops()) nature = 25;
		if (nature > 88 && nature < 100)
			ShowAllNatures();
	} while (nature < -1 || nature > 24);
	return nature;
}

int AskHiddenPowerType() {
	int type;
	do {
		cout << HP_TYPE_ASK_MSG << ": ";
		cin >> type;
		if (AvoidLoops()) type = 16;
		if (type > 88 && type < 100)
			ShowAllTypes();
	} while (type < -1 || type > 15);
	return type;
}

int AskHiddenPowerBasePower() {
	int power;
	do {
		cout << HP_POWER_ASK_MSG << ": ";
		cin >> power;
		if (AvoidLoops()) power = 71;
	} while (power < -1 || power > 70);
	return power;
}

int AskAbility() {
	char ability;
	do {
		cout << ABILITY_ASK_MSG << ": ";
		cin >> ability;
	} while (ability != '2' && ability != '1' && ability != 'n');
	return ((ability == 'n') + (ability != '1'));
}

bool AskYN(const char* msg) {
	char yn;
	do {
		cout << msg << " (" << Y_CHAR << "/" << N_CHAR << "): ";
		cin >> yn;
	} while (yn != N_CHAR && yn != Y_CHAR);

	return yn != N_CHAR;
}

int AskMethods() {
	bool gba_r = false;
	bool gba_yn = AskYN(GBA_ASK_MSG1);
	if (gba_yn)
		gba_r = AskYN(GBA_ASK_MSG2);

	return gba_yn + gba_r;
}

uint16_t AskID(bool SID) {
	uint32_t id;  // so it is rejected if it is over 0xFFFF
	do {
		cout << (SID ? SID_ASK_MSG : ID_ASK_MSG) << ": ";
		cin >> id;
	} while (AvoidLoops() || id > 65535);

	return (uint16_t)id;
}

uint16_t AskSID() {
	return AskID(true);
}

char AskMode() {
	char m;
	cout << "\n" << BAR << MODE_MSG << ": ";
	cin >> m;
	cout << BAR << "\n";

	return m;
}

void ShowSIDRange(uint16_t sid_max) {
	cout << "\nSID: " << (sid_max - 7) << "~" << sid_max << "\n";
}

void NoPIDMsg() {
	cout << BAR << "\n" << NO_PID_MSG << ". ";
}

void NoIVMsg() {
	cout << BAR << "\n" << IMPOSSIBLE_PID_MSG << ".\n";
}

void EndOfResults() {
	cout << "\n" << END_OF_RESULTS_MSG << ". ";
}

void WelcomeMessage() {
	cout << BAR << "\n" << PROGRAM_NAME << " " << VERSION << " - "
		<< LICENSE << " GPLv2\n" << TEXT_BEFORE_GH_LINK << ":\n" << LINK;
}
