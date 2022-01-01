//Statistic Display by CommandLeo

global_total_text = ' §lTotal';
global_block_list = block_list();
global_item_list = item_list();
global_server_whitelisted = system_info('server_whitelisted') || length(system_info('server_whitelist')) > 0;
global_app_name = system_info('app_name');
global_game_major_target = system_info('game_major_target');
global_hex_charset = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'];

pickaxes = filter(global_item_list, _~'_pickaxe');
axes = filter(global_item_list, _~'_axe');
shovels = filter(global_item_list, _~'_shovel');
hoes = filter(global_item_list, _~'_hoe');

display_names = read_file(str('display_names/%d', global_game_major_target), 'json');
global_misc_stats = display_names:'misc';
global_block_names = display_names:'blocks';
global_item_names = display_names:'items';
global_entity_names = display_names:'entities';
global_categories = {'mined' -> '%s Mined', 'crafted' -> '%s Crafted', 'used' -> '%s Used', 'broken' -> '%s Broken', 'picked_up' -> '%s Picked Up', 'dropped' -> '%s Dropped', 'killed' -> '%s Killed', 'killed_by' -> 'Killed by %s', 'custom' -> '%s', 'extra' -> '%s', 'digs' -> 'Digs [%s]'};
global_extra_stats = {'bedrock_removed' -> 'Bedrock Removed', 'ping' -> 'Ping', 'health' -> 'Health', 'xp_level' -> 'Levels of Experience', 'hunger' -> 'Hunger', 'saturation' -> 'Saturation', 'air' -> 'Remaining Air'};
global_dig_data = {'combined_blocks' -> [null, 'Combined Blocks'], 'total' -> [[...pickaxes, ...shovels, ...axes, ...hoes, 'shears'], 'Total'], 'pick' -> [pickaxes, 'Pickaxe'], 'shovel' -> [shovels, 'Shovel'], 'pickshovel' -> [[...pickaxes, ...shovels], 'Pickaxe & Shovel'], 'axe' -> [axes, 'Axe'], 'hoe' -> [hoes, 'Hoe']};

global_help_pages = [
    [
        'y /app_name mined <block> ', 'f ｜ ', 'g Amount of <block> mined', ' \n',
        'y /app_name used <item> ', 'f ｜ ', 'g Amount of <item> used or placed', ' \n',
        'y /app_name crafted <item> ', 'f ｜ ', 'g Amount of <item> crafted', ' \n',
        'y /app_name dropped <item> ', 'f ｜ ', 'g Amount of <item> dropped', ' \n',
        'y /app_name picked_up <item> ', 'f ｜ ', 'g Amount of <item> picked up', ' \n',
        'y /app_name broken <item> ', 'f ｜ ', 'g Amount of <item> broken', '^g (that ran out of durability)', ' \n',
        'y /app_name killed <entity> ', 'f ｜ ', 'g Amount of <entity> killed', ' \n',
        'y /app_name killed_by <entity> ', 'f ｜ ', 'g Amount of times <entity> killed you', ' \n',
        'y /app_name misc <misc_stat> ', 'f ｜ ', 'g Misc statistics, e.g. deaths, mob_kills, play_time, aviate_one_cm', ' \n',
        'y /app_name extra <extra_stat> ', 'f ｜ ', 'g Extra statistics that are not normally in the game, e.g. xp_level, ping, health, hunger', ' \n',
        'y /app_name bedrock_removed ', 'f ｜ ', 'g Amount of bedrock removed by hand using pistons and tnt', ' \n',
        'y /app_name digs <tool> ', 'f ｜ ', 'g Amount of digs with <tool> (combined_blocks by default)', ' \n',
        'y /app_name combined <combined_stat> ', 'f ｜ ', 'g Various statics combined together', ' \n',
    ],
    [
        'y /app_name get <category> <entry> [<player>]', 'f ｜ ', 'g Prints the value of a stat', ' \n',
        'y /app_name hide ', 'f ｜ ', 'g Hides the scoreboard', ' \n',
        'y /app_name show ', 'f ｜ ', 'g Shows the scoreboard', '  \n',
        'y /app_name bots [on/off/toggle]', 'f ｜ ', 'g A shortcut for /app_name settings botsIncluded', '  \n',
        'y /app_name settings botsIncluded [on/off/toggle] ', 'f ｜ ', 'g Includes or excludes bots in the scoreboard', ' \n',
        'y /app_name settings digDisplay [on/off/toggle] ', 'f ｜ ', 'g Shows or hides digs in the player list footer', ' \n',
        'y /app_name settings digDisplayColor <hex_color> ', 'f ｜ ', 'g Changes the color of digs display for yourself (leave empty to reset)', ' \n',
        'y /app_name settings statColor <hex_color> ', 'f ｜ ', 'g Changes the color of the scoreboard name for everyone (leave empty to reset)', ' \n',
    ],   
    [
        'y /app_name carousel start ', 'f ｜ ', 'g Starts a carousel of statistics', ' \n',
        'y /app_name carousel stop ', 'f ｜ ', 'g Stops the carousel', ' \n',
        'y /app_name carousel interval [<seconds>] ', 'f ｜ ', 'g Gets or sets the interval of the carousel', ' \n',
        'y /app_name carousel list ', 'f ｜ ', 'g Lists carousel entries', ' \n',
        'y /app_name carousel add <category> <entry> ', 'f ｜ ', 'g Adds an entry to the carousel', ' \n',
        'y /app_name carousel remove <index> ', 'f ｜ ', 'g Removes an entry from the carousel', ' \n',
    ]
];

__config() -> {
    'resources' -> [
        {
        'source' -> str('https://raw.githubusercontent.com/CommandLeo/scarpet/main/resources/stat/display_names/%d.json', global_game_major_target),
        'target' -> str('display_names/%d.json', global_game_major_target)
        },
        {
            'source' -> 'https://raw.githubusercontent.com/CommandLeo/scarpet/main/resources/stat/combined/',
            'target' -> 'combined/'
        }
    ],
    'commands' -> {
        '' -> 'menu',
        'hide' -> 'hide',
        'show' -> 'show',
        'help' -> ['help', 1],
        'help <page>' -> 'help',

        'bots' -> ['toggleBots', null],
        'bots on' -> ['toggleBots', true],
        'bots off' -> ['toggleBots', false],
        'bots toggle' -> ['toggleBots', null],
        'settings botsIncluded on' -> ['toggleBots', true],
        'settings botsIncluded off' -> ['toggleBots', false],
        'settings botsIncluded toggle' -> ['toggleBots', null],
        'settings digDisplay' -> ['toggleDigDisplay', null],
        'settings digDisplay on' -> ['toggleDigDisplay', true],
        'settings digDisplay off' -> ['toggleDigDisplay', false],
        'settings digDisplay toggle' -> ['toggleDigDisplay', null],
        'settings digDisplayColor' -> ['setDigDisplayColor', null],
        'settings digDisplayColor <hex_color>' -> 'setDigDisplayColor',
        'settings statColor' -> ['setStatColor', null],
        'settings statColor <hex_color>' -> 'setStatColor',

        'mined <block>' -> ['changeStat', 'mined'],
        'crafted <item>' -> ['changeStat', 'crafted'],
        'used <item>' -> ['changeStat', 'used'],
        'broken <item>' -> ['changeStat', 'broken'],
        'picked_up <item>' -> ['changeStat', 'picked_up'],
        'dropped <item>' -> ['changeStat', 'dropped'],
        'killed <entity>' -> ['changeStat', 'killed'],
        'killed_by <entity>' -> ['changeStat', 'killed_by'],
        'misc <misc>' -> ['changeStat', 'custom'],
        'extra <extra>' -> ['changeStat', 'extra'],
        'bedrock_removed' -> ['changeStat', 'bedrock_removed', 'extra'],
        'digs <dig>' -> ['changeStat', 'digs'],
        'digs' -> ['changeStat', 'combined_blocks', 'digs'],
        'combined <combined>' -> ['changeStat', 'combined'],

        'get mined <block>' -> ['printStatValue', null, 'mined'],
        'get crafted <item>' -> ['printStatValue', null, 'crafted'],
        'get used <item>' -> ['printStatValue', null, 'used'],
        'get broken <item>' -> ['printStatValue', null, 'broken'],
        'get picked_up <item>' -> ['printStatValue', null, 'picked_up'],
        'get dropped <item>' -> ['printStatValue', null, 'dropped'],
        'get killed <entity>' -> ['printStatValue', null, 'killed'],
        'get killed_by <entity>' -> ['printStatValue', null, 'killed_by'],
        'get misc <misc>' -> ['printStatValue', null, 'custom'],
        'get extra <extra>' -> ['printStatValue', null, 'extra'],
        'get digs <dig>' -> ['printStatValue', null, 'digs'],
        'get combined <combined>' -> ['printStatValue', 'combined', null],
        'get mined <block> <player>' -> ['printStatValue', 'mined'],
        'get crafted <item> <player>' -> ['printStatValue', 'crafted'],
        'get used <item> <player>' -> ['printStatValue', 'used'],
        'get broken <item> <player>' -> ['printStatValue', 'broken'],
        'get picked_up <item> <player>' -> ['printStatValue', 'picked_up'],
        'get dropped <item> <player>' -> ['printStatValue', 'dropped'],
        'get killed <entity> <player>' -> ['printStatValue', 'killed'],
        'get killed_by <entity> <player>' -> ['printStatValue', 'killed_by'],
        'get misc <misc> <player>' -> ['printStatValue', 'custom'],
        'get extra <extra> <player>' -> ['printStatValue', 'extra'],
        'get digs <dig> <player>' -> ['printStatValue', 'digs'],
        'get combined <combined> <player>' -> ['printStatValue', 'combined'],

        'carousel start' -> 'startCarousel',
        'carousel stop' -> 'stopCarousel',
        'carousel interval' -> _() -> print(format('f » ', 'g Carousel interval is currently set to ', str('d %d ', global_carousel_data:'interval' / 20), 'g seconds')),
        'carousel interval <seconds>' -> 'setCarouselInterval',
        'carousel remove <index>' -> 'removeCarouselEntry',
        'carousel add mined <block>' -> ['addCarouselEntry', 'mined'],
        'carousel add crafted <item>' -> ['addCarouselEntry', 'crafted'],
        'carousel add used <item>' -> ['addCarouselEntry', 'used'],
        'carousel add broken <item>' -> ['addCarouselEntry', 'broken'],
        'carousel add picked_up <item>' -> ['addCarouselEntry', 'picked_up'],
        'carousel add dropped <item>' -> ['addCarouselEntry', 'dropped'],
        'carousel add killed <entity>' -> ['addCarouselEntry', 'killed'],
        'carousel add killed_by <entity>' -> ['addCarouselEntry', 'killed_by'],
        'carousel add misc <misc>' -> ['addCarouselEntry', 'custom'],
        'carousel add extra <extra>' -> ['addCarouselEntry', 'extra'],
        'carousel add digs <dig>' -> ['addCarouselEntry', 'digs'],
        'carousel add combined <combined>' -> ['addCarouselEntry', 'combined'],
        'carousel list' -> 'listCarouselEntries'
    },
    'arguments' -> {
        'block' -> {
            'type' -> 'term',
            'options' -> global_block_list,
            'suggestions' -> global_block_list,
            'case_sensitive' -> false
        },
        'item' -> {
            'type' -> 'term',
            'options' -> global_item_list,
            'suggestions' -> global_item_list,
            'case_sensitive' -> false
        },
        'entity' -> {
            'type' -> 'term',
            'options' -> entity_types('*'),
            'suggestions' -> entity_types('*'),
            'case_sensitive' -> false
        },
        'misc' -> {
            'type' -> 'term',
            'options' -> keys(global_misc_stats),
            'suggestions' -> keys(global_misc_stats),
            'case_sensitive' -> false
        },
        'extra' -> {
            'type' -> 'term',
            'options' -> keys(global_extra_stats),
            'suggestions' -> keys(global_extra_stats),
            'case_sensitive' -> false
        },
        'dig' -> {
            'type' -> 'term',
            'options' -> keys(global_dig_data),
            'suggestions' -> keys(global_dig_data),
            'case_sensitive' -> false
        },
        'combined' -> {
            'type' -> 'term',
            'suggester' -> _(args) -> map(list_files('combined', 'text'), slice(_, length('combined') + 1)),
            'case_sensitive' -> false
        },
        'hex_color' -> {
            'type' -> 'term',
            'suggester' -> _(args) -> (
                color = upper(args:'hex_color' || '');
                if(!color || (length(color) < 6 && all(split(color), global_hex_charset~_ != null)), map(global_hex_charset, color + _), []);
            ),
            'case_sensitive' -> false
        },
        'player' -> {
            'type' -> 'players',
            'single' -> true
        },
        'page' -> {
            'type' -> 'int',
            'min' -> 1,
            'max' -> length(global_help_pages),
            'suggest' -> [range(length(global_help_pages))] + 1
        },
        'seconds' -> {
            'type' -> 'int',
            'min' -> 1,
            'max' -> 3600,
            'suggest' -> []
        },
        'index' -> {
            'type' -> 'int',
            'suggest' -> []
        }
    },
    'requires' -> {
        'carpet' -> '>=1.4.38'
    },
    'scope' -> 'global'
};

display_names = read_file(str('display_names/%d', global_game_major_target), 'json');
global_misc_stats = display_names:'misc';
global_block_names = display_names:'blocks';
global_item_names = display_names:'items';
global_entity_names = display_names:'entities';
global_categories = {'mined' -> '%s Mined', 'crafted' -> '%s Crafted', 'used' -> '%s Used', 'broken' -> '%s Broken', 'picked_up' -> '%s Picked Up', 'dropped' -> '%s Dropped', 'killed' -> '%s Killed', 'killed_by' -> 'Killed by %s', 'custom' -> '%s', 'extra' -> '%s', 'digs' -> 'Digs [%s]'};
global_extra_stats = {'bedrock_removed' -> 'Bedrock Removed', 'ping' -> 'Ping', 'health' -> 'Health', 'xp_level' -> 'Levels of Experience', 'hunger' -> 'Hunger', 'saturation' -> 'Saturation', 'air' -> 'Remaining Air'};
global_dig_data = {'combined_blocks' -> [null, 'Combined Blocks'], 'total' -> [[...pickaxes, ...shovels, ...axes, ...hoes, 'shears'], 'Total'], 'pick' -> [pickaxes, 'Pickaxe'], 'shovel' -> [shovels, 'Shovel'], 'pickshovel' -> [[...pickaxes, ...shovels], 'Pickaxe & Shovel'], 'axe' -> [axes, 'Axe'], 'hoe' -> [hoes, 'Hoe']};

menu() -> (
    texts = [
        'fs ' + ' ' * 80, ' \n',
        '#FED330b Statistic Display ', 'g by ', 'yb CommandLeo', ' \n\n',
        'g Type ', 'y /app_name help', '^g Click to run the command', '!/app_name help', 'g  to see all the commands', '  \n',
        'fs ' + ' ' * 80
    ];
    print(format(map(texts, replace(_, 'app_name', global_app_name))));
);

help(page) -> (
    l = length(global_help_pages);
    if(page < 1 || page > l, exit(print('§cInvalid page number')));
    page = page - 1;
    texts = ['fs ' + ' ' * 80, ' \n', ...global_help_pages:page, 'fs ' + ' ' * 31, 'y \ ', 'y «', str('!/app_name help %d', (page - 1) % l + 1), str('g \ Page %d/%d ', page + 1, l), 'y »', str('!/app_name help %d', (page + 1) % l + 1), '  ', 'fs ' + ' ' * 31];
    print(format(map(texts, replace(_, 'app_name', global_app_name))));
);

parseCombinedFile(name) -> (
    file = read_file('combined/' + name, 'text');
    if(file == null, exit(print('§cCombined statistic not found')));
    display_name = file:0;
    if(!display_name, exit(print('§cNo display name was specified')));
    category = file:1;
    if(global_categories~category == null, exit(print('§cInvalid category')));
    if(length(file) <= 2, exit(print('§cNo entries were found')));
    entries = slice(file, 2);
    return([display_name, category, entries]);
);

isInvalidEntry(entry) -> entry != global_total_text && !(global_stat:0 == 'digs' && global_server_whitelisted && system_info('server_whitelist')~entry != null) && (!player(entry) || (!global_display_bots && player(entry)~'player_type' == 'fake'));

removeInvalidEntries() -> (
    for(scoreboard('stats'), if(isInvalidEntry(_), scoreboard_remove('stats', _)));
);

calculateTotal() -> (
    for(scoreboard('stats'), if(_ != global_total_text, total += scoreboard('stats', _)));
    scoreboard('stats', global_total_text, total);
);

getDisplayName(category, event) -> (
    if(category == 'digs', return(global_dig_data:event:1));
    return(if(
        category == 'used' || category == 'crafted' || category == 'dropped' || category == 'picked_up', global_item_names, 
        category == 'mined', global_block_names,
        category == 'killed' || category == 'killed_by', global_entity_names,
        category == 'custom', global_misc_stats,
        category == 'extra', global_extra_stats
    ):event || event);
);

getStat(player, category, event) -> (
    if(category == 'digs',
        if(!player(player), return(global_digs:event:str(player)));
        if(event == 'combined_blocks',
            return(reduce(global_block_list, _a + statistic(player, 'mined', _), 0)),
            tools = global_dig_data:event:0;
            if(!tools, exit('§cNo dig type with that name'));
            return(reduce(tools, _a + statistic(player, 'used', _), 0))
        );
    );
    if(category == 'combined',
        if(event == global_stat:1 && global_combined, [category, entries] = global_combined, [display_name, category, entries] = parseCombinedFile(event));
        return(reduce(entries, _a + statistic(player, category, _), 0));
    );
    if(category == 'extra',
        if(event == 'bedrock_removed', return(global_bedrock_removed:(player(player)~'uuid')));
        return(player(player)~event);
    );
    return(statistic(player, category, event));
);

displayDigs(player) -> (
    color = global_display_digs_color:(player~'uuid') || 'FFEE44';
    display_title(player, 'player_list_footer', format(str('#%s ⚒ %s', color, global_digs:'total':str(player)), '#343A40  ｜ ', str('#%s ⬛ %s', color, global_digs:'combined_blocks':str(player)), '#343A40  ｜ ', str('#%s ⛏ %s', color, global_digs:'pick':str(player))));
);

hide() -> (
    if(scoreboard_property('stats', 'display_slot')~'sidebar' != null, scoreboard_display('sidebar', null));
);

show() -> (
    scoreboard_display('sidebar', 'stats');
);

toggleBots(value) -> (
    global_display_bots = if(value == null, !global_display_bots, value);
    print(format('f » ', 'g Bots are now ', ...if(global_display_bots, ['l included', 'g  in '], ['r excluded', 'g  from ']), 'g the sidebar'));
    if(!global_stat, return());
    bots = filter(player('all'), _~'player_type' == 'fake');
    for(bots, updateStat(_));
    calculateTotal();
);

toggleDigDisplay(value) -> (
    uuid = player()~'uuid';
    global_display_digs:uuid = if(value == null, global_display_digs:uuid == false, value);
    print(format('f » ', 'g Digs are now ', if(global_display_digs:uuid, 'l displayed', 'r hidden'), 'g  in the player list footer'));
    if(global_display_digs:uuid, displayDigs(player()), display_title(player(), 'player_list_footer'));
);

setDigDisplayColor(color) -> (
    uuid = player()~'uuid';
    if(!color, 
        delete(global_display_digs_color:uuid),
        color = upper(replace(color, '#', ''));
        if(length(color) != 6 || !all(split(color), global_hex_charset~_ != null), exit(print('§cInvalid hex color')));
        global_display_digs_color:uuid = color;
    );
    if(global_display_digs:uuid != false, displayDigs(player()));
);

setStatColor(color) -> (
    if(player()~'permission_level' == 0, exit(print('§cYou must be an operator to modify this setting')));
    if(!color,
        global_stat_color = null,
        color = upper(replace(color, '#', ''));
        if(length(color) != 6 || !all(split(color), global_hex_charset~_ != null), exit(print('§cInvalid hex color')));
        global_stat_color = color;
    );
    scoreboard_property('stats', 'display_name', format(str('#%s %s', global_stat_color || 'FFEE44', scoreboard_property('stats', 'display_name'))));
);

printStatValue(event, player, category) -> (
    player = player || player();
    value = getStat(player, category, event);
    if(!value, exit(print('§cNo value was found')));
    print(format('f » ', str('g Value of \'%s\' for %s is ', str(global_categories:category, getDisplayName(category, event)), player), 'y ' + value));
);

changeStat(event, category) -> (
    if(global_carousel_active, exit(print('§cCouldn\'t change the displayed statistic, a carousel is currently active')));
    showStat(category, event);
    logger(str('[Stat] Stat Change | %s -> %s.%s', player(), category, event));
);

showStat(category, event) -> (
    if(category == 'combined', [display_name, combined_category, entries] = parseCombinedFile(event); global_combined = [combined_category, entries]);
    global_stat = [category, event];
    scoreboard_property('stats', 'display_name', format(str('#%s ' + (display_name || global_categories:category), global_stat_color || 'FFEE44', getDisplayName(category, event))));
    list = {};
    for(player('all'), list += str(_));
    if(category == 'digs' && global_server_whitelisted, for(system_info('server_whitelist'), list += _));
    for(list, updateStat(_));
    removeInvalidEntries();
    calculateTotal();
    show();
);

updateStat(player) -> (
    if(isInvalidEntry(str(player)), return(scoreboard_remove('stats', player)));
    stat = getStat(player, ...global_stat);
    if(stat, scoreboard('stats', player, stat), scoreboard_remove('stats', player));
);

updateDigs(player) -> (
    if(global_server_whitelisted && system_info('server_whitelist')~str(player) == null, return());
    for(keys(global_dig_data),
        global_digs:_ = global_digs:_ || {};
        amount = getStat(player, 'digs', _);
        if(amount > 0, global_digs:_:str(player) = amount);
    );
    if(global_display_digs:(player(player)~'uuid') != false, displayDigs(player(player)));
);

// CAROUSEL

startCarousel() -> (
    if(global_carousel_active, exit(print('§cThere\'s already a carousel active')));
    interval = global_carousel_data:'interval';
    entries = global_carousel_data:'entries';
    if(!entries, exit(print('§cNo entries were found')));
    if(!interval, exit(print('§cNo interval was provided')));
    print(format('f » ', 'g You ', 'l started ', 'g the carousel'));
    logger(str('[Stat] Carousel Start | %s', player()));
    global_carousel_active = true;
    carousel(interval, entries, 0);
);

stopCarousel() -> (
    if(!global_carousel_active, exit(print('§cThere is no carousel active')));
    print(format('f » ', 'g You ', 'r stopped ', 'g the carousel'));
    logger(str('[Stat] Carousel Stop | %s', player()));
    global_carousel_active = false;
);

setCarouselInterval(seconds) -> (
    global_carousel_data:'interval' = seconds * 20;
    print(format('f » ', 'g Carousel interval was set to ', str('d %d ', seconds), 'g seconds'));
    logger(str('[Stat] Carousel Interval Change | %s -> %d', player(), seconds));
);

addCarouselEntry(entry, category) -> (
    global_carousel_data:'entries' += [category, entry];
    print(format('f » ', 'g Successfully added an entry to the carousel'));
);

removeCarouselEntry(index) -> (
    entries = global_carousel_data:'entries';
    if(index >= length(entries), exit(print('§cInvalid index')));
    delete(entries, index);
);

listCarouselEntries() -> (
    entries = global_carousel_data:'entries';
    if(!length(entries), exit(print(format('f » ', 'g No entries to show, the carousel is empty'))));
    print(format(reduce(entries, [..._a, ' \n  ', '#EB4D4Bb ❌', '^r Remove entry', str('?/%s carousel remove %d', global_app_name, _i), '  ', str('g %s.%s', _)], ['f » ', 'g Carousel entries: ', '#26DE81b (+)', '^l Add more entries', str('?/%s carousel add ', global_app_name)])));
);

carousel(interval, entries, i) -> (
    if(global_carousel_active,
        stat = entries:i;
        showStat(...stat);
        schedule(interval, 'carousel', interval, entries, (i + 1) % length(entries));
    );
);

// EVENTS

__on_statistic(player, category, event, value) -> (
    if(category == 'used' && global_dig_data:'total':0~event != null, schedule(0, 'updateDigs', player));
    if(!global_stat || global_stat:1 == 'bedrock_removed', exit());
    if(global_stat == [category, event] || (global_stat == ['digs', 'combined_blocks'] && category == 'mined') || (category == 'used' && global_stat:0 == 'digs' && global_dig_data:(global_stat:1):0~event) || (global_stat:0 == 'combined' && global_combined:0 == category && global_combined:1~event != null), schedule(0, 'updateStat', player); schedule(0, 'calculateTotal'));
);

__on_player_places_block(player, item_tuple, hand, block) -> (
    if(!block~'piston', exit());
    facing_pos = pos_offset(block, block_state(block, 'facing'));
    facing_block = block(facing_pos);
    if(facing_block != 'bedrock', exit());
    schedule(2, _(outer(facing_pos), outer(facing_block), outer(player)) -> 
        if(block(facing_pos) != 'bedrock',
            global_bedrock_removed:(player~'uuid') += 1;
            if(global_stat == ['extra', 'bedrock_removed'], updateStat(player); calculateTotal());
        );
    );
);

__on_tick() -> (
    return();
    if(!global_stat, return());
    if((global_stat:0 == 'extra' && global_stat:1 != 'bedrock_removed') || global_stat == ['custom', 'play_time'] || global_stat == ['custom', 'play_one_minute'], for(player('all'), schedule(0, 'updateStat', _)); schedule(0, 'calculateTotal'));
);

__on_player_connects(player) -> (
    schedule(0, 'updateDigs', player);
    if(global_stat, schedule(0, 'updateStat', player); schedule(0, 'calculateTotal'));
);

__on_player_disconnects(player, reason) -> (
    if(global_stat, schedule(0, 'updateStat', player); schedule(0, 'calculateTotal'));
);

__on_close() -> (
    write_file('bedrock_removed', 'json', global_bedrock_removed);
    write_file('carousel', 'json', global_carousel_data);
    settings = {
        'stat' -> global_stat,
        'display_bots' -> global_display_bots,
        'display_digs' -> global_display_digs,
        'display_digs_color' -> global_display_digs_color,
        'stat_color' -> global_stat_color
    };
    write_file('settings', 'json', settings);
    for(global_digs, write_file(str('digs/%s', _), 'json', global_digs:_));
);

// INITIALISATION

__on_start() -> (
    global_bedrock_removed = read_file('bedrock_removed', 'json') || {};
    global_digs = {};
    for(list_files('digs', 'json'), global_digs:slice(_, length('digs') + 1) = read_file(_, 'json'));
    global_carousel_data = read_file('carousel', 'json') || {'interval' -> 20, 'entries' -> []};
    settings = read_file('settings', 'json');
    global_stat = settings:'stat' || [];
    global_display_bots = settings:'display_bots';
    global_display_digs = settings:'display_digs' || {};
    global_display_digs_color = settings:'display_digs_color' || {};
    global_stat_color = settings:'stat_color';
    if(global_stat:0 == 'combined', [display_name, combined_category, entries] = parseCombinedFile(global_stat:1); global_combined = [combined_category, entries]);

    if(scoreboard()~'stats' == null, scoreboard_add('stats'));
    //scoreboard_display('list', 'stats'); // had to comment out because of a vanilla bug
    removeInvalidEntries();
    for(player('all'), updateDigs(_));
);