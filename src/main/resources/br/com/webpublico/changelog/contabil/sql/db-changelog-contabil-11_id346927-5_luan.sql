insert into moduloprefeituraportal values (hibernate_sequence.nextval, 'DADOS_ABERTOS', 608930532, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'SQL',
        '{"consulta":"select nome, chave, ''height: 100px;'' as style, ''menu-strip-branco'' as classediv \r\nfrom paginaprefeituraportal \r\nwhere modulo_id = (select id from moduloprefeituraportal where modulo = ''DADOS_ABERTOS'') \r\n$WHERE \r\norder by ordem","count":"select count(id) from paginaprefeituraportal where modulo_id = (select id from moduloprefeituraportal where modulo = ''DADOS_ABERTOS'') $WHERE","totalRegistros":2,"fields":[{"posicao":1,"descricao":"Nome","columnName":"NOME","columnValue":"NOME","tipoAlinhamento":"CENTRO","totalizar":false,"link":{"chaveDestino":"CHAVE","columnOrigem":"NOME","columnDestino":""},"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":2,"descricao":"CHAVE","columnName":"CHAVE","columnValue":"CHAVE","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":true},{"posicao":3,"descricao":"STYLE","columnName":"STYLE","columnValue":"STYLE","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":true},{"posicao":4,"descricao":"CLASSEDIV","columnName":"CLASSEDIV","columnValue":"CLASSEDIV","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":true}],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Dados Abertos', 'dados-abertos', 'TABELA_UNICA',
        '<div></div>', 0, 1, (select id from moduloprefeituraportal where modulo = 'PORTAL'), null, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'HTML',
        '{"consulta":"select * from dual $WHERE","count":"select 1 from dual","totalRegistros":null,"fields":[],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Política de Dados Abertos', 'politica-dados-abertos', 'GRID',
        '<div></div>', 0, 1, (select id from moduloprefeituraportal where modulo = 'DADOS_ABERTOS'), 1, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'HTML',
        '{"consulta":"select * from dual $WHERE","count":"select 1 from dual","totalRegistros":null,"fields":[],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Plano de Dados Abertos', 'plano-dados-abertos', 'GRID',
        '<div></div>', 0, 1, (select id from moduloprefeituraportal where modulo = 'DADOS_ABERTOS'), 2, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'HTML',
        '{"consulta":"select * from dual $WHERE","count":"select 1 from dual","totalRegistros":null,"fields":[],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Portal de Dados Abertos', 'portal-dados-abertos', 'GRID',
        '<div></div>', 0, 1, (select id from moduloprefeituraportal where modulo = 'DADOS_ABERTOS'), 3, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'HTML',
        '{"consulta":"select * from dual $WHERE","count":"select 1 from dual","totalRegistros":null,"fields":[],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Inventário de Dados Abertos', 'inventario-dados-abertos', 'GRID',
        '<div></div>', 0, 1, (select id from moduloprefeituraportal where modulo = 'DADOS_ABERTOS'), 4, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'HTML',
        '{"consulta":"select * from dual $WHERE","count":"select 1 from dual","totalRegistros":null,"fields":[],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Feedback', 'novo-feedback', 'GRID',
        '<div></div>', 0, 1, (select id from moduloprefeituraportal where modulo = 'DADOS_ABERTOS'), 5, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'SQL',
        '{"consulta":"select nome, descricao, datafeedback from feedbackportaltransp $WHERE order by id desc","count":"select count(id) from feedbackportaltransp $WHERE","totalRegistros":1,"fields":[{"posicao":0,"descricao":"Data","columnName":"DATAFEEDBACK","columnValue":"DATAFEEDBACK","tipoAlinhamento":"ESQUERDA","totalizar":false,"link":null,"tipo":"DATE","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":1,"descricao":"Nome","columnName":"NOME","columnValue":"NOME","tipoAlinhamento":"ESQUERDA","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":2,"descricao":"Feedback","columnName":"DESCRICAO","columnValue":"DESCRICAO","tipoAlinhamento":"ESQUERDA","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false}],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Feedbacks', 'feedbacks', 'TABELA',
        '<div></div>', 0, 1, (select id from moduloprefeituraportal where modulo = 'DADOS_ABERTOS'), 6, null, null, null);

insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'HTML',
        '{"consulta":"select * from dual $WHERE","count":"select 1 from dual","totalRegistros":null,"fields":[],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Lei do Governo Digital (LGD)', 'lgd', 'GRID',
        '<div></div>', 0, 1, (select id from moduloprefeituraportal where modulo = 'DADOS_ABERTOS'), 7, null, null, null);

insert into PAGINAMENUHPAGINAPREF
values (HIBERNATE_SEQUENCE.NEXTVAL, 'Dados Abertos',
        (select pmh.id from PAGINAMENUHORIZPORTAL pmh where pmh.tipo = 'PRINCIPAL'),
        null, null, 17, null, null, null);

insert into SubPaginaMenuHorizontal
values (HIBERNATE_SEQUENCE.NEXTVAL, (select p.id from PAGINAMENUHPAGINAPREF p where p.nome = 'Dados Abertos'), 'Políticas de Dados Abertos', 'dados-abertos', 1);
