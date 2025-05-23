insert into CONFIGURACAOANEXOSRREO (id, exercicio_id, linhaTotalGeral, linhaTotalIntraOrcamentario)
values (hibernate_sequence.nextval, (select id from exercicio where ano = 2021), 106, 105)
