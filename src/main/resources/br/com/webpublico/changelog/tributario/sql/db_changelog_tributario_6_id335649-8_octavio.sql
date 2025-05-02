insert into
configuracaotributobi (id, tributo_id, tipotributobi)
values (hibernate_sequence.nextval, (select id from tributo where codigo = 52 and descricao = 'TSU'), 'IPTU');
