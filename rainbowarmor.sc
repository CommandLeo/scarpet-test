// Rainbow Armor by CommandLeo

__config() -> {
    'commands' -> {
        '' -> 'toggle'
    },
    'scope' -> 'player'
};

global_equipment = ['boots', 'leggings', 'chestplate', 'helmet'];

hsvToRgb(h, s, v) -> (
    f(n, outer(h), outer(s), outer(v)) -> (
        k = (n + h / 60) % 6;
        return(v - v * s * relu(min(k, 4 - k, 1)));
    );
    return([f(5), f(3), f(1)] * 255);
);

rgbToDecimal(r, g, b) -> r * 2^16 + g * 2^8 + b;

toggle() -> (
    global_enabled = !global_enabled;
    if(!global_enabled,
       loop(4, inventory_set(player(), 36 + _, 0)); 
    );
);

__on_start() -> (
    global_h = 0;
    global_enabled = false;
);

__on_tick() -> (
    if(!global_enabled, return());
    global_h = (global_h + 4) % 360;
    color = rgbToDecimal(...hsvToRgb(global_h, 1, 1));
    for(global_equipment,
        slot = 36 + _i;
        piece = str('leather_%s', _);
        inventory_set(player(), slot, 1, piece, if(system_info('game_pack_version') >= 33, {'components' -> {'dyed_color' -> color}, 'id' -> piece}, {'display' -> {'color' -> color}}));
    );
);