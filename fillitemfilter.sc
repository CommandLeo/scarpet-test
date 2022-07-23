global_dummy_item = ['structure_void', '{display:{Name:\'"╚═Dummy═╝"\'}}'];
global_item_list = item_list();

__config() -> {
    'commands' -> {
        '<from_pos> <to_pos> items <items>' -> 'fillItemFilterFromItems',
        '<from_pos> <to_pos> layout <layout>' -> 'fillItemFilterFromItemLayout',
    },
    'arguments' -> {
        'layout' -> {
            'type' -> 'term',
            'suggester' -> _(arg) -> map(list_files('item_layouts', 'shared_text'), slice(_, length('item_layouts') + 1)),
            'case_sensitive' -> false
        },
        'items' -> {
            'type' -> 'text',
            'suggester' -> _(args) -> (
                i = args:'items';
                items = split(' ', i);
                if(length(items) && slice(i, -1) != ' ', delete(items, -1));
                items_string = join(' ', items);
                return(if(items, map(global_item_list, str('%s %s', items_string, _)), global_item_list));
            ),
            'case_sensitive' -> false
        },
        'from_pos' -> {
            'type' -> 'pos'
        },
        'to_pos' -> {
            'type' -> 'pos'
        }
    }
};

_error(error) -> exit(print(format(str('r %s', error))));

_scanStrip(from_pos, to_pos) -> (
    [x1, y1, z1] = from_pos;
    [x2, y2, z2] = to_pos;
    [dx, dy, dz] = map(to_pos - from_pos, if(_ < 0, -1, 1));
    if(x1 != x2, return(map(range(x1, x2 + dx, dx), [_, y1, z1])));
    if(y1 != y2, return(map(range(y1, y2 + dy, dy), [x1, _, z1])));
    if(z1 != z2, return(map(range(z1, z2 + dz, dz), [x1, y1, _])));
);

_fill(pos, item) -> (

    if(global_item_list~item == null, return());

    inventory_set(pos, 0, 2, if(item == 'air', ...global_dummy_item, item));
    inventory_set(pos, 1, if(stack_limit(item) == 16, 11, 17), ...global_dummy_item);
    inventory_set(pos, 2, 1, ...global_dummy_item);
    inventory_set(pos, 3, 1, ...global_dummy_item);
    inventory_set(pos, 4, 1, ...global_dummy_item);

    for(diamond(pos, 2), if(_ == 'comparator', block_tick(_)));
);

fillItemFilterFromItems(from_pos, to_pos, item_string) -> (
    items = split(' ', item_string);
    fillItemFilter(from_pos, to_pos, items);
);

fillItemFilterFromItemLayout(from_pos, to_pos, layout) -> (
    items = read_file(str('item_layouts/%s', layout), 'shared_text');
    fillItemFilter(from_pos, to_pos, items);
);

fillItemFilter(from_pos, to_pos, items) -> (
    if(length(filter(to_pos - from_pos, _ == 0)) != 2, _error('The area must be a row of blocks'));

    invalid_items = {};
    for(items, if(global_item_list~_ == null, invalid_items += _));
    if(invalid_items, _error(str('Invalid items: %s', join(', ', keys(invalid_items)))));

    positions = _scanStrip(from_pos, to_pos);
    i = 0;
    for(positions,
        pos = _;
        if(block(pos) == 'hopper',
            item = items:i;
            _fill(pos, item);
            i += 1;
        );
    );
);