package com.hexagram2021.tetrachordlib;

import com.hexagram2021.tetrachordlib.benchmark.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(TetrachordLib.MODID)
public class TetrachordLib {
	public static final String MODID = "tetrachordlib";
	public static final String MODNAME = "Tetrachord Lib";
	public static final String VERSION = ModList.get().getModFileById(MODID).versionString();

	public TetrachordLib() {
		//MinecraftForge.EVENT_BUS.register(new NoHostilesAroundCampfireImproved());
		MinecraftForge.EVENT_BUS.register(this);
	}
}
