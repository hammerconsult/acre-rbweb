DECLARE
 texto CLOB := '{"consulta":"select distinct vw.descricao as descricao\r\nfrom unidadeorganizacional uni\r\n         inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = uni.id\r\nwhere sysdate between (vw.iniciovigencia) and (coalesce(fimvigencia, sysdate)) $WHERE","count":"select distinct count(1)\r\nfrom unidadeorganizacional uni\r\n         inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = uni.id\r\nwhere sysdate between (vw.iniciovigencia) and (coalesce(fimvigencia, sysdate))","totalRegistros":90,"fields":[{"posicao":1,"descricao":"DESCRICAO","columnName":"DESCRICAO","columnValue":"DESCRICAO","tipoAlinhamento":"ESQUERDA","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false}],"pesquisaveis":[],"tabs":[]}';
BEGIN
update PAGINAPREFEITURAPORTAL set CONTEUDO = texto where NOME = 'Unidades' and CHAVE = 'unidade';
END;
