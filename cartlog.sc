__config() -> {
    'commands' -> {
        '' -> 'toggle',
        'pos' -> 'toggle_pos',
        'speed' -> 'toggle_speed',
        'fill_level' -> 'toggle_fill_level',
        'locked' -> 'toggle_locked',
        'waterlogged' -> 'toggle_waterlogged',
        'hitbox' -> 'toggle_hitbox'
    },
    'scope' -> 'player'
};

toggle() -> global_on = !global_on;

toggle_pos() -> global_toggles:'pos' = !global_toggles:'pos';

toggle_speed() -> global_toggles:'speed' = !global_toggles:'speed';

toggle_fill_level() -> global_toggles:'fill_level' = !global_toggles:'fill_level';

toggle_locked() -> global_toggles:'locked' = !global_toggles:'locked';

toggle_waterlogged() -> global_toggles:'waterlogged' = !global_toggles:'waterlogged';

toggle_hitbox() -> global_toggles:'hitbox' = !global_toggles:'hitbox';

__on_start() -> (
    global_toggles = {
        'pos' -> true,
        'speed' -> true,
        'fill_level' -> true,
        'locked' -> true,
        'hitbox' -> true,
        'waterlogged' -> true
    };
);

__on_tick() -> (
    if(!global_on, return());
    for(entity_list('minecarts'),
        i = 0;
        cart = _;
        fill_level = if(inventory_has_items(cart), floor(1 + reduce(inventory_get(cart), _a + if(_ , _:1 / stack_limit(_:0), 0), 0) / inventory_size(cart) * 14), 0);
        speed_x = max(-8, min(8, cart~'motion_z' * 20));
        speed_y = cart~'motion_y' * 20;
        speed_z = max(-8, min(8, cart~'motion_x' * 20));
        locked = !query(cart, 'nbt', 'Enabled');

        if(global_toggles:'locked' && cart~'type' == 'hopper_minecart', draw_shape('label', 1, {'player' -> player(), 'pos' -> [0, 1, 0], 'follow' -> _, 'height' -> i+=1, 'text' -> '', 'value' -> str('Locked: %b', locked)}));
        if(global_toggles:'fill_level' && inventory_has_items(cart) != null, draw_shape('label', 1, {'player' -> player(), 'pos' -> [0, 1, 0], 'follow' -> _, 'height' -> i+=1, 'text' -> '', 'value' -> str('Fill Level: %d', fill_level)}));
        if(global_toggles:'waterlogged', draw_shape('label', 1, {'player' -> player(), 'pos' -> [0, 1, 0], 'follow' -> _, 'height' -> i+=1, 'text' -> '', 'value' -> str('In Water: %b', liquid(block(pos(cart))))}));
        if(global_toggles:'speed',
            draw_shape('label', 1, {'player' -> player(), 'pos' -> [0, 1, 0], 'follow' -> _, 'height' -> i+=1, 'text' -> '', 'value' -> str('Speed Z: %.2f bps', speed_x)});
            draw_shape('label', 1, {'player' -> player(), 'pos' -> [0, 1, 0], 'follow' -> _, 'height' -> i+=1, 'text' -> '', 'value' -> str('Speed Y: %.2f bps', speed_y)});
            draw_shape('label', 1, {'player' -> player(), 'pos' -> [0, 1, 0], 'follow' -> _, 'height' -> i+=1, 'text' -> '', 'value' -> str('Speed X: %.2f bps', speed_z)});
        );
        if(global_toggles:'pos',
            draw_shape('label', 1, {'player' -> player(), 'pos' -> [0, 1, 0], 'follow' -> _, 'height' -> i+=1, 'text' -> '', 'value' -> str('Z: %.2f', cart~'z')});
            draw_shape('label', 1, {'player' -> player(), 'pos' -> [0, 1, 0], 'follow' -> _, 'height' -> i+=1, 'text' -> '', 'value' -> str('Y: %.2f', cart~'y')});
            draw_shape('label', 1, {'player' -> player(), 'pos' -> [0, 1, 0], 'follow' -> _, 'height' -> i+=1, 'text' -> '', 'value' -> str('X: %.2f', cart~'x')});
        );

        if(global_toggles:'hitbox', draw_shape('box', 1, {'player' -> player(), 'from' -> pos(cart) - [0.98/2, 0, 0.98/2], 'to' -> pos(cart) + [0.98/2, 0.7, 0.98/2], 'fill' -> 0x9b59b688}));
    );
)