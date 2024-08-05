__config() -> {
    'commands' -> {
        '' -> 'toggle'
    },
    'scope' -> 'global'
};

global_enabled = {};

toggle() -> (
    if(!delete(global_enabled, player()), global_enabled += player());
    print(format('f » ', 'g Laser pointer is now ', if(has(global_enabled, player()), 'l enabled', 'r disabled')));
);

laserPointer() -> (
    for(player('all'),
        pos = query(_, 'trace', 5, 'exact');
        if(pos, for(global_enabled, draw_shape('sphere', 2, 'center', pos, 'radius', 0.05, 'fill', 0xff0000aa, 'color', 0, 'player', _)));
    );
    schedule(0, 'laserPointer');
);

__on_start() -> (
    laserPointer();
)