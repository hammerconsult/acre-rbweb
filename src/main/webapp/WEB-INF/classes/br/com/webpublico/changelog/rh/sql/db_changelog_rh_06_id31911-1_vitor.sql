delete from MENU where id = (select id from MENU where LABEL = 'CONTRIBUIÇÃO INDIVIDUALIZADA DE PREVIDÊNCIA');
