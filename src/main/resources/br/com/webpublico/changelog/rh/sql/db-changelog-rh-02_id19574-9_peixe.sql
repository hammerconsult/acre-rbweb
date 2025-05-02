alter table PERIODOAQUISITIVOEXCLUIDO add migracaochave varchar(100);
insert into PERIODOAQUISITIVOEXCLUIDO (id, inicioVigencia, finalVigencia, tipoPeriodoAquisitivo, migracaochave)
select hibernate_sequence.nextval, '09/01/2000', '01/01/2005', 'LICENCA', trim(cargo.DESCRICAO)||'-'||cargo.CODIGODOCARGO||'-'||cargo.TIPOPCS from cargo where cargo.PERIODOAQUISITIVOEXCLUIDO_ID is null;
update cargo set PERIODOAQUISITIVOEXCLUIDO_ID = (select max(id) from PERIODOAQUISITIVOEXCLUIDO where migracaoChave like trim(cargo.DESCRICAO)||'-'||cargo.CODIGODOCARGO||'-'||cargo.TIPOPCS);
alter table PERIODOAQUISITIVOEXCLUIDO drop column migracaochave;
