insert into folhadepagamento (id, ano, calculadaem, efetivadaem, mes, TIPOFOLHADEPAGAMENTO, UNIDADEORGANIZACIONAL_ID, COMPETENCIAFP_ID, VERSAO) values
  (hibernate_sequence.nextval, 2015, to_date('22/06/2015', 'dd/mm/yyyy'), to_date('22/06/2015', 'dd/mm/yyyy'), 5, 'RESCISAO', 8756991, 653567712, 26);

insert into FICHAFINANCEIRAFP (id, FOLHADEPAGAMENTO_ID, RECURSOfp_id, VINCULOFP_ID, CREDITOSALARIOPAGO, UNIDADEORGANIZACIONAL_ID) values
  (hibernate_sequence.nextval, (select id from FOLHADEPAGAMENTO where COMPETENCIAFP_ID = 653567712 and VERSAO = 26), 639944869, 638897241, 0, 58758540);

update ITEMFICHAFINANCEIRAFP set FICHAFINANCEIRAFP_ID = (select ficha.id from FICHAFINANCEIRAFP ficha
  inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.id
where folha.COMPETENCIAFP_ID = 653567712 and ficha.VINCULOFP_ID = 638897241 and folha.VERSAO = 26) where id in (653572427, 653572426, 653572428, 653572429);

insert into folhadepagamento (id, ano, calculadaem, efetivadaem, mes, TIPOFOLHADEPAGAMENTO, UNIDADEORGANIZACIONAL_ID, COMPETENCIAFP_ID, VERSAO) values
  (hibernate_sequence.nextval, 2015, to_date('22/06/2015', 'dd/mm/yyyy'), to_date('22/06/2015', 'dd/mm/yyyy'), 5, 'RESCISAO', 8756991, 653567712, 31);

insert into FICHAFINANCEIRAFP (id, FOLHADEPAGAMENTO_ID, RECURSOfp_id, VINCULOFP_ID, CREDITOSALARIOPAGO, UNIDADEORGANIZACIONAL_ID) values
  (hibernate_sequence.nextval, (select id from FOLHADEPAGAMENTO where COMPETENCIAFP_ID = 653567712 and VERSAO = 31), 639944869, 638897241, 0, 58758540);

update ITEMFICHAFINANCEIRAFP set FICHAFINANCEIRAFP_ID = (select ficha.id from FICHAFINANCEIRAFP ficha
  inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.id
where folha.COMPETENCIAFP_ID = 653567712 and ficha.VINCULOFP_ID = 638897241 and folha.VERSAO = 31) where id in (653572433, 653572434, 653572435);

insert into folhadepagamento (id, ano, calculadaem, efetivadaem, mes, TIPOFOLHADEPAGAMENTO, UNIDADEORGANIZACIONAL_ID, COMPETENCIAFP_ID, VERSAO) values
  (hibernate_sequence.nextval, 2015, to_date('22/06/2015', 'dd/mm/yyyy'), to_date('22/06/2015', 'dd/mm/yyyy'), 5, 'RESCISAO', 8756991, 653567712, 45);

insert into FICHAFINANCEIRAFP (id, FOLHADEPAGAMENTO_ID, RECURSOfp_id, VINCULOFP_ID, CREDITOSALARIOPAGO, UNIDADEORGANIZACIONAL_ID) values
  (hibernate_sequence.nextval, (select id from FOLHADEPAGAMENTO where COMPETENCIAFP_ID = 653567712 and VERSAO = 45), 639944869, 638897241, 0, 58758540);

update ITEMFICHAFINANCEIRAFP set FICHAFINANCEIRAFP_ID = (select ficha.id from FICHAFINANCEIRAFP ficha
  inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.id
where folha.COMPETENCIAFP_ID = 653567712 and ficha.VINCULOFP_ID = 638897241 and folha.VERSAO = 45) where id in (653572440, 653572441);

insert into ITEMFICHAFINANCEIRAFP(id, valor, valorreferencia, eventofp_id, fichafinanceirafp_id, unidadereferencia, tipocalculofp, valorbasedecalculo, ano, mes, tipoeventofp) values
(hibernate_sequence.nextval, 18606.4, 0, 585813121, (select ficha.id from FICHAFINANCEIRAFP ficha
  inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.id
where folha.COMPETENCIAFP_ID = 653567712 and ficha.VINCULOFP_ID = 638897241 and folha.VERSAO = 45), 'V', 'NORMAL', 0, 2015, 6, 'VANTAGEM');

delete from FICHAFINANCEIRAFP where FOLHADEPAGAMENTO_ID = (select id from FOLHADEPAGAMENTO where COMPETENCIAFP_ID = 653567712 and VERSAO = 99) and VINCULOFP_ID = 638897241;
