insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'SQL', null,
        'Bens Móveis', 'bens-moveis', 'TABELA', '<div></div>', 0, 1,
        (select id from moduloprefeituraportal where modulo = 'BENS_PATRIMONIAIS'), 1, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'SQL', null,
        'Bens Imóveis', 'bens-imoveis', 'TABELA', '<div></div>', 0, 1,
        (select id from moduloprefeituraportal where modulo = 'BENS_PATRIMONIAIS'), 1, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'SQL', null,
        'Frota de Veículos', 'frota-veiculos', 'TABELA', '<div></div>', 0, 1,
        (select id from moduloprefeituraportal where modulo = 'BENS_PATRIMONIAIS'), 1, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'SQL', null,
        'Detalhes do Veículo', 'veiculo-ver', 'GRID', '<div></div>', 0, 1,
        (select id from moduloprefeituraportal where modulo = 'BENS_PATRIMONIAIS'), 1, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'SQL', null,
        'Manutenção de Veículos', 'manutencao-veiculo', 'TABELA', '<div></div>', 0, 1,
        (select id from moduloprefeituraportal where modulo = 'BENS_PATRIMONIAIS'), 1, null, null, null);
