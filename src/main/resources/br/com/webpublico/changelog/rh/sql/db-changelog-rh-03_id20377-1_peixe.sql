alter table cargoconfianca add ATOLEGALTEMP_ID NUMBER(19,0);
update cargoconfianca cargo set cargo.ATOLEGALTEMP_ID = (select ato.atoLegal_id  from cargoConfianca c inner join atoDePessoal ato on ato.id = c.ATODEPESSOAL_ID where c.id = cargo.id);
alter table cargoconfianca drop column atoDePessoal_id;
alter table cargoconfianca add atoDePessoal_id NUMBER(19,0);
update CARGOCONFIANCA cargo set cargo.atoDePessoal_id = cargo.ATOLEGALTEMP_ID;


alter table cargoconfianca_aud add ATOLEGALTEMP_ID NUMBER(19,0);
update cargoconfianca_aud cargo set cargo.ATOLEGALTEMP_ID = (select ato.atoLegal_id  from cargoConfianca_aud c inner join atoDePessoal_aud ato on ato.id = c.ATODEPESSOAL_ID where c.id = cargo.id);
alter table cargoconfianca_aud drop column atoDePessoal_id;
alter table cargoconfianca_aud add atoDePessoal_id NUMBER(19,0);
update CARGOCONFIANCA_aud cargo set cargo.atoDePessoal_id = cargo.ATOLEGALTEMP_ID;
