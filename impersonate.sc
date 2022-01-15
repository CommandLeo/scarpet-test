global_target = 'PallaPalla';
global_player = 'CommandLeo';

__config() -> {
    'commands' -> {
        'chat <text>' -> 'chat',
        'init' -> 'init'
    },
    'scope' -> 'player'
};

chat(msg) -> print(player('all'), str('<%s> %s', global_target, msg));

init() -> (
    run(str('/player %s spawn', global_target));
    modify(player(), 'gamemode', 'spectator');
    modify(player(global_target), 'flying', 1)
);

__on_tick() -> (
    p = player(global_target);
    if(!p, return());
    modify(p, 'location', player(global_player)~'location')
);

__on_player_interacts_with_entity(player, entity, hand) -> (
    if(hand == 'mainhand' && str(player) == 'CommandLeo' && str(entity) == global_target, modify(entity, 'swing'));
);