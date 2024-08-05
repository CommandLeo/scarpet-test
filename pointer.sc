__config()->{'scope'->'global'};

__on_tick()->(
    for(player('*'), if(_~'holds':0 == 'trident', _point(_)));
);

_point(name)->(
    trace = player(name)~'trace';
    if(trace, draw_shape('box', 2, {'from'->trace, 'to'->trace, 'fill'->882432938, 'color'->0}));
);
