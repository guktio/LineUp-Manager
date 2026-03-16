package com.grenade.main.dto;

import java.util.List;

import com.grenade.main.entity.Grenade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
@AllArgsConstructor
public class GrenadeRequest{
    public String name;
    public String command;
    public Grenade.MapType map;
    public Grenade.GrenadeType grenadeType;
    public String side;
    public String speed;
    public List<String> buttons;
    public String media;
    public String description;
}
