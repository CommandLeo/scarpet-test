// Here by CommandLeo

__config() -> {
    'commands' -> {
        '' -> ['here', 0],
        'g <duration>' -> 'here'
    },
    'args' -> {
        'duration' -> {
            'type' -> 'int',
            'min' -> 1,
            'max' -> 100000,
            'suggest' -> [5, 15, 30, 60]
        }
    },
    'scope' -> 'player'
};

here(duration) -> (
    [x, y, z] = pos(player());
    print(player('all'), str('%s » [x:%d, y:%d, z:%d, dim:%s]', player(), x, y, z, player()~'dimension'));
    if(duration > 0,
        if(player()~'permission_level' > 0,
            modify(player(), 'effect', 'glowing', number(duration)*20, 0, 0, 0),
            print(format('r Only server admins can use the glowing functionality!'))
        )
    );
);