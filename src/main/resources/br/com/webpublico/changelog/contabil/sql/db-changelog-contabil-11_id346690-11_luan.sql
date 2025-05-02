insert into SubPaginaMenuHorizontal values (HIBERNATE_SEQUENCE.NEXTVAL, (select p.id from PAGINAMENUHPAGINAPREF p where p.nome = 'Obras'), 'De olho na obra', 'https://deolhonaobra.riobranco.ac.gov.br/', 1);
insert into SubPaginaMenuHorizontal values (HIBERNATE_SEQUENCE.NEXTVAL, (select p.id from PAGINAMENUHPAGINAPREF p where p.nome = 'Obras'), 'Obras Paralizadas', 'obra-paralizada', 2);
insert into SubPaginaMenuHorizontal values (HIBERNATE_SEQUENCE.NEXTVAL, (select p.id from PAGINAMENUHPAGINAPREF p where p.nome = 'Obras'), 'Fiscais e gestores de contrato', 'fiscal-contrato', 3);
