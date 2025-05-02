insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'HTML',
        '{"consulta":"select * from dual $WHERE","count":"select 1 from dual $WHERE","totalRegistros":null,"fields":[],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Rol das Informações Classificadas em Cada Grau de Sigilo', 'rol-informacoes-classificadas-sigilo', 'GRID',
        '<div>A Prefeitura Municipal de Rio Branco informa que até o momento não há informações classificadas nos termos do
§1º, art. 24 da Lei nº 12.527/201, sendo observado somente o sigilo estabelecido em leis específicas (sigilo fiscal,
pessoal, dentre outros).</div>',
        0, 1, (select id from moduloprefeituraportal where modulo = 'PORTAL'),
        null, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'HTML',
        '{"consulta":"select * from dual $WHERE","count":"select 1 from dual $WHERE","totalRegistros":null,"fields":[],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Rol das Informações Desclassificadas nos Últimos 12 Meses',
        'rol-informacoes-desclassificadas-ultimos-12-meses', 'GRID',
        '<div>A Prefeitura Municipal de Rio Branco informa que até o momento não há informações desclassificadas nos últimos
12 meses nos termos do §1º, art. 24 da Lei nº 12.527/2011.</div>',
        0, 1, (select id from moduloprefeituraportal where modulo = 'PORTAL'),
        null, null, null, null);

insert into PAGINAMENUHPAGINAPREF
values (HIBERNATE_SEQUENCE.NEXTVAL, 'Informações Classificadas',
        (select pmh.id from PAGINAMENUHORIZPORTAL pmh where pmh.tipo = 'PRINCIPAL'),
        null, null, 13, null, null, null);

insert into SubPaginaMenuHorizontal
values (HIBERNATE_SEQUENCE.NEXTVAL,
        (select p.id from PAGINAMENUHPAGINAPREF p where p.nome = 'Informações Classificadas'),
        'Rol das Informações Classificadas em Cada Grau de Sigilo',
        'rol-informacoes-classificadas-sigilo', 1);

insert into SubPaginaMenuHorizontal
values (HIBERNATE_SEQUENCE.NEXTVAL,
        (select p.id from PAGINAMENUHPAGINAPREF p where p.nome = 'Informações Classificadas'),
        'Rol das Informações Desclassificadas nos Últimos 12 Meses',
        'rol-informacoes-desclassificadas-ultimos-12-meses', 2);
