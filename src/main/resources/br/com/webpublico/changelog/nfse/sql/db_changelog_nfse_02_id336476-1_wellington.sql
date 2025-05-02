insert into configuracaonfseparametros (ID, TIPOPARAMETRO, VALOR, CONFIGURACAO_ID)
values (hibernate_sequence.nextval, 'PRAZO_AVISO_NOVA_MENSAGEM', '5',
        (select id from configuracaonfse where rownum = 1));
