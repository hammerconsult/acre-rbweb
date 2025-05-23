insert into PAGINAPREFEITURAPORTAL
values (HIBERNATE_SEQUENCE.nextval, 608930532, 'SQL',
        'null',
        'Detalhes da Obra', 'detalhe-obra', 'GRID',
        '<div></div>', 0, 1, (select id from MODULOPREFEITURAPORTAL where modulo = 'OBRA'));
