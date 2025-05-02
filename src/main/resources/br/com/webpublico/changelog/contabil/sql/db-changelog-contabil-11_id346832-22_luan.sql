insert into PAGINAMENUHPAGINAPREF
values (HIBERNATE_SEQUENCE.NEXTVAL, 'Orientações ao Cidadão',
        (select pmh.id from PAGINAMENUHORIZPORTAL pmh where pmh.tipo = 'PRINCIPAL'), null, null, 15, null, null, null);

insert into SubPaginaMenuHorizontal values (HIBERNATE_SEQUENCE.NEXTVAL, (select p.id from PAGINAMENUHPAGINAPREF p where p.nome = 'Orientações ao Cidadão'),
                                            'Orientações', 'orientacoes', 1);
