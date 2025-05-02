insert into configuracaowebservice (id, tipo, chave, url, usuario, senha, detalhe, configuracaotributario_id)
select hibernate_sequence.nextval, 'PROTESTO', 'PREPRODUCAO', 'https://craac.cra21.com.br/craac/xml/protestos.php', 'reusprefeitura', 'Riobranco2021',
        'http://integracaoprotesto.webpublico.riobranco.ac.gov.br/integracao', (select id from configuracaotributario order by vigencia desc fetch first 1 rows only) from dual
where not exists(select * from configuracaowebservice where tipo = 'PROTESTO' and chave = 'PREPRODUCAO');

insert into configuracaowebservice (id, tipo, chave, url, usuario, senha, detalhe, configuracaotributario_id)
select hibernate_sequence.nextval, 'PROTESTO', 'WPPRODUCAO', 'https://craac.crabr.com.br/craac/xml/protestos.php', 'reusprefeitura', 'Riobranco2021',
        'http://integracaoprotesto.webpublico.riobranco.ac.gov.br/integracao', (select id from configuracaotributario order by vigencia desc fetch first 1 rows only) from dual
where not exists(select * from configuracaowebservice where tipo = 'PROTESTO' and chave = 'WPPRODUCAO');
