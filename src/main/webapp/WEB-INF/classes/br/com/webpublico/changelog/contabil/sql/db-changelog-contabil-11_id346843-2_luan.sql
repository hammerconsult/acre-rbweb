insert into SubPaginaMenuHorizontal
values (HIBERNATE_SEQUENCE.NEXTVAL,
        (select p.id from PAGINAMENUHPAGINAPREF p where p.nome = 'Estatísticas'),
        'Panorama IBGE', 'https://cidades.ibge.gov.br/brasil/ac/rio-branco/panorama', 1);

insert into SubPaginaMenuHorizontal
values (HIBERNATE_SEQUENCE.NEXTVAL,
        (select p.id from PAGINAMENUHPAGINAPREF p where p.nome = 'Estatísticas'),
        'Sistema Integrado de Geotecnologia de Rio Branco (RBGeo)', 'https://rbgeo.riobranco.ac.gov.br/portal/home/', 2);

insert into SubPaginaMenuHorizontal
values (HIBERNATE_SEQUENCE.NEXTVAL,
        (select p.id from PAGINAMENUHPAGINAPREF p where p.nome = 'Estatísticas'),
        'Serviço Geológico do Brasil', 'https://p3m-beta.cprm.gov.br/#/map', 3);
