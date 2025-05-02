update configuracaotributario
set codigoapresentante = '56E',
    nomeapresentante = 'MUNICIPIO DE RIO BRANCO'
where id = (select id from configuracaotributario order by vigencia desc fetch first 1 rows only)
