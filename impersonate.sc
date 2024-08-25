// Impersonate by CommandLeo

global_target = null;

__config() -> {
    'commands' -> {
        'start <player>' -> 'impersonate',
        'stop' -> 'stopImpersonating'
    },
    'arguments' -> {
        'player' -> {
            'type' -> 'players',
            'single' -> true
        }
    },
    'scope' -> 'player'
};

_error(error) -> (
    print(format(str('r %s', error)));
    run('playsound block.note_block.didgeridoo master @s');
    exit();
);

impersonate(player) -> (
    target = player(player);
    if(!target, _error('That player is not online'));
    if(target~'player_type' != 'fake', _error('Only fake players can be targeted'));
    global_target = player;
    modify(player(), 'gamemode', 'spectator');
    modify(target, 'flying', 1);
    modify(player(), 'location', target~'location');
    print(format('f » ', 'g Started impersonating ', 'd ' + player));
);

stopImpersonating() -> (
    if(!global_target, _error('Not currently impersonating anyone'));
    print(format('f » ', 'g Stopped impersonating ', 'd ' + global_target));
    global_target = null;
);

__on_player_message(player, message) -> (
    if(player() == player && global_target && player(global_target),
        print(player('all'), '<' + player(global_target)~'display_name' + '> ' + message);
        return('cancel');
    );
);

__on_tick() -> (
    if(!global_target, return());
    target = player(global_target);
    if(!target, return());
    modify(target, 'location', player()~'location');
);

__on_player_interacts_with_entity(player, entity, hand) -> (
    if(hand == 'mainhand' && player == player() && entity == player(global_target), modify(entity, 'swing'));
);