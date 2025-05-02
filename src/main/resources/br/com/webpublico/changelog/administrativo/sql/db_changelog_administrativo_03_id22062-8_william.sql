insert into PARAMETROPATRIMONIOTAGOCC VALUES(hibernate_sequence.nextval, '01/01/1980', null,
(select id from tagocc where codigo = 62), (select id from tagocc where codigo = 58), (select id from parametropatrimonio))
