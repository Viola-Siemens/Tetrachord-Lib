package com.hexagram2021.tetrachordlib;

import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(TetrachordLib.MODID)
public class TetrachordLib {
	public static final String MODID = "tetrachordlib";
	public static final String MODNAME = "Tetrachord Lib";
	public static final String VERSION = ModList.get().getModFileById(MODID).versionString();

	public TetrachordLib() {
	}
}
