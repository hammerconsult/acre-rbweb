update loteformulariocotacao lt set lt.tipocontrole =
(select distinct fc.tipocontrolelicitacao from formulariocotacao fc
 inner join loteformulariocotacao lote on lote.formulariocotacao_id = fc.id
 where lote.id = lt.id);



 update itemloteformulariocotacao it set it.tipocontrole =
(select distinct fc.tipocontrolelicitacao from formulariocotacao fc
 inner join loteformulariocotacao lote on lote.formulariocotacao_id = fc.id
 inner join itemloteformulariocotacao item on item.loteformulariocotacao_id = lote.id
 where item.id = it.id);

