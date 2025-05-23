insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'SQL', null,
        'Detalhes do Plano de Contratações Anual (PCA)', 'pca-ver', 'GRID', '<div></div>', 0, 1,
        (select id from moduloprefeituraportal where modulo = 'CONTRATO'),
        null, null, null, null);
