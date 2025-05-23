update UNIDADEORGANIZACIONAL set escola = 0;
update UNIDADEORGANIZACIONAL set escola = 1 where CODIGOINEP is not null;
