
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is the static version of a villager, meaning this villager does NOT move..
public class StaticVillager extends Villager {
    Animation idle;
    Array<TextureAtlas.AtlasRegion> regions;

    public StaticVillager(GameScreen screen, TiledMap map, TextureAtlas atlas, int villager) {
        super(screen, map, atlas, villager);
    }

    // SImilar to the dynamic villager, sets the villager's frames dependant upon what villager it is (1, 2, 3, etc).
    protected void createRegions() {
        regions = new Array<TextureAtlas.AtlasRegion>();

        switch (villager) {
            case 0:
                regions.addAll(atlas.findRegion("villager1idle1"), atlas.findRegion("villager1idle2"), atlas.findRegion("villager1idle3"));
                break;
            case 1:
                regions.addAll(atlas.findRegion("villager2idle1"), atlas.findRegion("villager2idle2"));
                break;
            case 2:
                regions.addAll(atlas.findRegion("villager3idle1"), atlas.findRegion("villager3idle2"));
                break;
            case 3:
                regions.addAll(atlas.findRegion("villager4idle1"), atlas.findRegion("villager4idle2"));
                break;
            case 5:
                regions.addAll(atlas.findRegion("villager6idle1"), atlas.findRegion("villager6idle2"));
                break;
            case 6:
                regions.addAll(atlas.findRegion("villager7idle1"), atlas.findRegion("villager7idle2"));
                break;
            case 8:
                regions.addAll(atlas.findRegion("villager9idle1"), atlas.findRegion("villager9idle2"));
                break;
            case 9:
                regions.addAll(atlas.findRegion("villager10idle1"), atlas.findRegion("villager10idle2"));
                break;
            case 10:
                regions.addAll(atlas.findRegion("villager11idle1"), atlas.findRegion("villager11idle2"));
                break;
            case 11:
                regions.addAll(atlas.findRegion("villager12idle1"), atlas.findRegion("villager12idle2"), atlas.findRegion("villager12idle3"));
                break;
            case 12:
                regions.addAll(atlas.findRegion("villager13idle1"), atlas.findRegion("villager13idle2"));
                break;
            case 13:
                regions.addAll(atlas.findRegion("villager14idle1"), atlas.findRegion("villager14idle2"));
                break;
            case 14:
                regions.addAll(atlas.findRegion("villager15idle1"), atlas.findRegion("villager15idle2"));
                break;
            case 15:
                regions.addAll(atlas.findRegion("villager16idle1"), atlas.findRegion("villager16idle2"));
                break;
            case 16:
                regions.addAll(atlas.findRegion("villager17idle1"), atlas.findRegion("villager17idle2"));
                break;
            case 17:
                regions.addAll(atlas.findRegion("villager18idle1"), atlas.findRegion("villager18idle2"));
                break;
            case 18:
                regions.addAll(atlas.findRegion("villager19idle1"), atlas.findRegion("villager19idle2"));
                break;
            case 19:
                regions.addAll(atlas.findRegion("villager20idle1"), atlas.findRegion("villager20idle2"));
                break;
            case 20:
                regions.addAll(atlas.findRegion("villager21idle1"), atlas.findRegion("villager21idle2"));
                break;
            case 21:
                regions.addAll(atlas.findRegion("villager22idle1"), atlas.findRegion("villager22idle2"));
                break;
            case 22:
                regions.addAll(atlas.findRegion("villager23idle1"), atlas.findRegion("villager23idle2"), atlas.findRegion("villager23idle3"));
                break;
            case 23:
                regions.addAll(atlas.findRegion("villager24idle1"), atlas.findRegion("villager24idle2"));
                break;
            case 24:
                regions.addAll(atlas.findRegion("villager25idle1"), atlas.findRegion("villager25idle2"));
                break;
            case 25:
                regions.addAll(atlas.findRegion("villager26idle1"), atlas.findRegion("villager26idle2"));
                break;
            case 26:
                regions.addAll(atlas.findRegion("villager27idle1"), atlas.findRegion("villager27idle2"));
                break;
        }
    }

    protected void update(float deltaTime) {

    }

    // Sets the scant animations the villager has.
    protected void createAnimations() {
        if (regions.size > 2)
            idle = new Animation(0.25f, regions.get(0), regions.get(1), regions.get(0), regions.get(2));
        else
            idle = new Animation(0.5f, regions.get(0), regions.get(1));
    }

    protected boolean overrideCheck() {
        return false;
    }

    protected boolean priorities(int cState) {
        return false;
    }

    protected void chooseSprite()
    {
        Animation anim = idle;
        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 8 / 7, anim.getKeyFrame(animationTime, true).getRegionHeight() * 8 / 7);
    }
}
