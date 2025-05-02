alter table FuncaoGratificada add ATOLEGALTEMP_ID NUMBER(19,0);
update FuncaoGratificada cargo set cargo.ATOLEGALTEMP_ID = (select ato.atoLegal_id  from FuncaoGratificada c inner join atoDePessoal ato on ato.id = c.ATODEPESSOAL_ID where c.id = cargo.id);
alter table FuncaoGratificada drop column atoDePessoal_id;
alter table FuncaoGratificada add atoDePessoal_id NUMBER(19,0);
update FuncaoGratificada cargo set cargo.atoDePessoal_id = cargo.ATOLEGALTEMP_ID;


alter table FuncaoGratificada_aud add ATOLEGALTEMP_ID NUMBER(19,0);
update FuncaoGratificada_aud cargo set cargo.ATOLEGALTEMP_ID = (select ato.atoLegal_id  from FuncaoGratificada_aud c inner join atoDePessoal_aud ato on ato.id = c.ATODEPESSOAL_ID where c.id = cargo.id);
alter table FuncaoGratificada_aud drop column atoDePessoal_id;
alter table FuncaoGratificada_aud add atoDePessoal_id NUMBER(19,0);
update FuncaoGratificada_aud cargo set cargo.atoDePessoal_id = cargo.ATOLEGALTEMP_ID;
