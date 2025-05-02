insert into paginaprefeituraportal
values (hibernate_sequence.nextval, 608930532, 'SQL', null,
        'Obras Paralizadas', 'obra-paralizada', 'TABELA',
        '<div></div>', 0, 1,
        (select id from moduloprefeituraportal where modulo = 'OBRA'),
        null, null, null, null);
