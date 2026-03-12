package com.grenade.main.dto;

import java.util.List;

import com.grenade.main.entity.Grenade;

import lombok.Builder;

@Builder
public record GrenadeRequestServer(
    Grenade.MapType map,
    String command,
    Grenade.GrenadeType grenadeType,
    Grenade.Side side,
    List<String> buttons
) {
}
// {"command":"setpos 2011.7571 -430.06525 -350.14062; setang 1.48 179.21 0.00",
// "grenadeType":"weapon_flashbang",
// "map":"DE_NUKE",
// "team":"CT",
// "speed":283.78757,
// "buttons":["W","LMB","Space","Shift"]}