global_player_name = 'PallaPalla';

__config() -> {
    'commands' -> {
        'chat <text>' -> 'chat',
        'init' -> 'init'
    },
    'command_permission' -> _(player) -> str(player) == 'CommandLeo',
    'scope' -> 'player'
};

chat(msg) -> print(player('all'), str('<%s> %s', global_player_name, msg));

init() -> (
    run(str('/player %s spawn', global_player_name));
    modify(player(), 'gamemode', 'spectator');
    modify(player(global_player_name), 'flying', 1)
);

__on_tick() -> (
    p = player(global_player_name);
    if(!p, return());
    modify(p, 'location', player()~'location')
);

__on_player_interacts_with_entity(player, entity, hand) -> (
    if(hand == 'mainhand' && str(player) == 'CommandLeo' && str(entity) == global_player_name, modify(entity, 'swing'));
);