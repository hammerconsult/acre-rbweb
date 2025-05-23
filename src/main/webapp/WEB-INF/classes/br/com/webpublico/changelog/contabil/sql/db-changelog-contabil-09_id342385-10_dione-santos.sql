DECLARE
 texto CLOB := '{"consulta":"select distinct fr.codigo, fr.descricao from fontederecursos fr\r\ninner join exercicio e on fr.exercicio_id = e.id\r\nwhere e.ano = $ANO\r\norder by fr.descricao $WHERE","count":"select distinct count(1) from fontederecursos fr\r\ninner join exercicio e on fr.exercicio_id = e.id\r\nwhere e.ano = $ANO\r\n","totalRegistros":18,"fields":[{"posicao":1,"descricao":"CODIGO","columnName":"CODIGO","columnValue":"CODIGO","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":2,"descricao":"DESCRICAO","columnName":"DESCRICAO","columnValue":"DESCRICAO","tipoAlinhamento":"ESQUERDA","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false}],"pesquisaveis":[],"tabs":[]}';
BEGIN
update PAGINAPREFEITURAPORTAL set CONTEUDO = texto where NOME = 'Fontes' and CHAVE = 'fonte-de-recurso';
END;
