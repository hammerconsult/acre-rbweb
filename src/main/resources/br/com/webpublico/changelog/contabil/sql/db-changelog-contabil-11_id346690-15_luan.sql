insert into paginaprefeituraportal
values (hibernate_sequence.nextval, 608930532, 'SQL', null,
        'Fiscais e Gestores de Contrato', 'fiscal-contrato', 'TABELA',
        '<div></div>', 0, 1,
        (select id from moduloprefeituraportal where modulo = 'CONTRATO'),
        null, null, null, null);
