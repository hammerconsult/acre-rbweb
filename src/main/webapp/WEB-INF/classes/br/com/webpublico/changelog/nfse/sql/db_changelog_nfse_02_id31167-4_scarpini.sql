insert into CONFIGURACAONFSEPARAMETROS
values (HIBERNATE_SEQUENCE.nextval, 'DIA_LIMITE_CANCELAMENTO_NOTA',15, (SELECT MAX(ID) FROM CONFIGURACAONFSE));
