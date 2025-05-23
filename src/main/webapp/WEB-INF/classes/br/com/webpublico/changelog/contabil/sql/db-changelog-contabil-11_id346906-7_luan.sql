insert into PAGINAMENUHPAGINAPREF
values (HIBERNATE_SEQUENCE.NEXTVAL, 'Patrimônio',
        (select pmh.id from PAGINAMENUHORIZPORTAL pmh where pmh.tipo = 'PRINCIPAL'),
        null, null, 4, null, null, null);

insert into subpaginamenuhorizontal
values (hibernate_sequence.nextval, (select p.id from paginamenuhpaginapref p where p.nome = 'Patrimônio'), 'Bens Móveis', 'bens-moveis', 1);

insert into subpaginamenuhorizontal
values (hibernate_sequence.nextval, (select p.id from paginamenuhpaginapref p where p.nome = 'Patrimônio'), 'Bens Imóveis', 'bens-imoveis', 2);

insert into subpaginamenuhorizontal
values (hibernate_sequence.nextval, (select p.id from paginamenuhpaginapref p where p.nome = 'Patrimônio'), 'Frota de Veículos', 'frota-veiculos', 3);

insert into subpaginamenuhorizontal
values (hibernate_sequence.nextval, (select p.id from paginamenuhpaginapref p where p.nome = 'Patrimônio'), 'Manutenção de Veículos', 'manutencao-veiculo', 4);
