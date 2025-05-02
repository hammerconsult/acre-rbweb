insert into configuracaonfseparametros (ID, TIPOPARAMETRO, VALOR, CONFIGURACAO_ID)
values (hibernate_sequence.nextval, 'UTILIZA_BANCO_CACHE', 'TRUE', (select id from configuracaonfse where rownum = 1));
