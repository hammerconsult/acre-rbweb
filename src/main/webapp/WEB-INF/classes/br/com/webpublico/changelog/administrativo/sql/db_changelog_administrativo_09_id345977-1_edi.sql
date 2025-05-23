alter table LOTEPROPFORNEC add valor_backup numeric(19, 2);
update LOTEPROPFORNEC set valor_backup = valor where id is not null;
alter table LOTEPROPFORNEC drop column valor;
alter table LOTEPROPFORNEC rename column valor_backup to valor;

alter table LOTEPROPFORNEC_AUD add valor_backup numeric(19, 2);
update LOTEPROPFORNEC_AUD set valor_backup = valor where id is not null;
alter table LOTEPROPFORNEC_AUD drop column valor;
alter table LOTEPROPFORNEC_AUD rename column valor_backup to valor;


alter table LOTEPROCESSODECOMPRA add valor_backup numeric(19, 2);
update LOTEPROCESSODECOMPRA set valor_backup = valor where id is not null;
alter table LOTEPROCESSODECOMPRA drop column valor;
alter table LOTEPROCESSODECOMPRA rename column valor_backup to valor;

alter table LOTEPROCESSODECOMPRA_AUD add valor_backup numeric(19, 2);
update LOTEPROCESSODECOMPRA_AUD set valor_backup = valor where id is not null;
alter table LOTEPROCESSODECOMPRA_AUD drop column valor;
alter table LOTEPROCESSODECOMPRA_AUD rename column valor_backup to valor;
