insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'COMUM > PESSOA FISICA RFB > LISTA',
        '/comum/pessoafisicarfb/lista.xhtml', 0, 'GERENCIAMENTO');

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
values (HIBERNATE_SEQUENCE.nextval, 'ATUALIZAÇÃO PESSOAS FÍSICAS (RFB)',
        '/comum/pessoafisicarfb/lista.xhtml',
        (select id from menu where LABEL = 'GERENCIAMENTO DE RECURSOS'),
        (select max(ordem) from menu
         where PAI_ID = (select id from menu where label = 'GERENCIAMENTO DE RECURSOS')) + 10);
