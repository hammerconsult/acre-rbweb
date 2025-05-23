insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'SQL', null,
        'Detalhes dos Bens Doados', 'bens-doados-ver', 'GRID', '<div></div>', 0, 1,
        (select id from moduloprefeituraportal where modulo = 'COVID_19'),
        null, null, null, null);
