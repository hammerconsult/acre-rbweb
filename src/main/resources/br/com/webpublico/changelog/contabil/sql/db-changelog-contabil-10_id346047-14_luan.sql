insert into PAGINAMENUHPAGINAPREF
values (hibernate_sequence.nextval,
        'PLANEJAMENTO MUNICIPAL',
        (select id from paginamenuhorizportal where tipo = 'MENU_HORIZONTAL'),
        (select id from paginaprefeituraportal where chave = 'planejamento-municipal' and nome = 'Planejamento Municipal'),
        null, 7, null, null, null)
;
