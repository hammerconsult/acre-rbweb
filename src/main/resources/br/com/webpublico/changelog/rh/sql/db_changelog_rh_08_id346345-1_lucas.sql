insert into moduloexportacao (id, codigo, descricao)
values (HIBERNATE_SEQUENCE.nextval, 11, 'RPA');

insert into grupoexportacao (id, codigo, descricao, moduloexportacao_id, nomereduzido, tipogrupoexportacao)
values (HIBERNATE_SEQUENCE.nextval, 1161, 'Valor do Serviço', (select id from moduloexportacao where codigo = 11), 'VALORSERVICO', 'MENSAL');

insert into grupoexportacao (id, codigo, descricao, moduloexportacao_id, nomereduzido, tipogrupoexportacao)
values (HIBERNATE_SEQUENCE.nextval, 1162, 'Previdência Patronal', (select id from moduloexportacao where codigo = 11), 'PREVIDENCIAPATRONAL', 'MENSAL');

insert into grupoexportacao (id, codigo, descricao, moduloexportacao_id, nomereduzido, tipogrupoexportacao)
values (HIBERNATE_SEQUENCE.nextval, 1163, 'Previdência Prestador', (select id from moduloexportacao where codigo = 11), 'PREVIDENCIAPRESTADOR', 'MENSAL');

insert into grupoexportacao (id, codigo, descricao, moduloexportacao_id, nomereduzido, tipogrupoexportacao)
values (HIBERNATE_SEQUENCE.nextval, 1164, 'SEST', (select id from moduloexportacao where codigo = 11), 'SEST', 'MENSAL');

insert into grupoexportacao (id, codigo, descricao, moduloexportacao_id, nomereduzido, tipogrupoexportacao)
values (HIBERNATE_SEQUENCE.nextval, 1165, 'SENAT', (select id from moduloexportacao where codigo = 11), 'SENAT', 'MENSAL');
