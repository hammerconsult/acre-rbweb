update itemintencaoregistropreco set quantidade = 1 where (quantidade is null or quantidade = 0);
update itemintencaoregistropreco set valor = 0 where (valor is null );

update itemparticipanteirp set quantidade = 1 where (quantidade is null or quantidade = 0);
update itemparticipanteirp set valor = 0 where valor is null;

update itemloteformulariocotacao set quantidade = 1 where (quantidade is null or quantidade = 0);
update itemloteformulariocotacao set valor = 0 where valor is null;

update itemcotacao set quantidade = 1 where (quantidade is null or quantidade = 0);
update itemcotacao set valorunitario = 0 where valorunitario is null;
update itemcotacao set valortotal = 0 where valortotal is null;

update itemsolicitacao set quantidade = 1 where (quantidade is null or quantidade = 0);
update itemprocessodecompra set quantidade = 1 where (quantidade is null or quantidade = 0);
