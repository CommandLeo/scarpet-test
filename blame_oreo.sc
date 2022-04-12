__config() -> {'scope' -> 'global'};

global_blame_count = load_app_data():'blame_count';

__on_player_message(player, message) -> (
    if(message~'!blame', schedule(0, _() -> print(player('all'), format(str('i That\'s Oreo\'s fault, the blame counter is now at %d', global_blame_count += 1)))));
);

__on_close() -> store_app_data({'blame_count' -> global_blame_count});