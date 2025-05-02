insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'SQL', null,
        'Detalhes da Emenda', 'emenda-ver', 'GRID',
        '<div></div>', 0, 1,
        (select id from moduloprefeituraportal where modulo = 'EMENDA'),
        null, null, null, null);
