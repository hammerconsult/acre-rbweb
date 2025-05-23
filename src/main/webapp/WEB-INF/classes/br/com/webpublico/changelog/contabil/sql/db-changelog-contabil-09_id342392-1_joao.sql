insert into PAGINAPREFEITURAPORTAL
values (HIBERNATE_SEQUENCE.nextval, 608930532, 'LINK', '{"consulta":"select * from dual $WHERE","count":"select count(1) from dual","totalRegistros":1,"fields":[],"pesquisaveis":[],"tabs":[]}',
        'Licitações, Contratos e Obras', 'http://portalcgm.riobranco.ac.gov.br/portal/licitacoes-e-contratos/demonstrativo-de-licitacoes-e-contratos/', 'TABELA',
        '<div></div>', 0, 1, (select id from MODULOPREFEITURAPORTAL where MODULO = 'CONTRATO'));
