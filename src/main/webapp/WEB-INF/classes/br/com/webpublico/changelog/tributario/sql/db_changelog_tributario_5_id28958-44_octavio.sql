update configuracaocontabil set classetribcontrestituicao_id = (select id from classecredor where codigo = '46' and tipoclassecredor = 'RESTITUICAO')
where classetribcontrestituicao_id is null;
