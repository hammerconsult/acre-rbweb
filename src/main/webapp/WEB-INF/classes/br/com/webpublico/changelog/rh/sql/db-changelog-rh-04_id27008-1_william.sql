update enquadramentofuncional set FINALVIGENCIA = '15/01/92' where id =640065626;
update enquadramentofuncional set CONTRATOSERVIDOR_ID = 831201439 where CONTRATOSERVIDOR_ID = 638921964 and id <> 640065626;
update enquadramentofuncional set FINALVIGENCIA = '30/09/18' where id = 772151734;
delete ENQUADRAMENTOFUNCIONAL where id =831209690;
insert into enquadramentofuncional values (hibernate_sequence.nextval, '04/06/03', '16/01/92', 640025214, 831201439, 640027382, sysdate, sysdate, null, null, null, 831209710);
update fichafinanceirafp set VINCULOFP_ID = 831201439 where VINCULOFP_ID = 638921964;
