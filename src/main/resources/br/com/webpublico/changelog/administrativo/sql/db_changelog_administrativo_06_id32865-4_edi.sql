update lotecotacao lt set lt.tipocontrole =
(select distinct fc.tipocontrolelicitacao from cotacao cot
  inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id
 inner join lotecotacao lote on lote.cotacao_id = cot.id
 where lote.id = lt.id);

 update itemcotacao it set it.tipocontrole =
(select distinct fc.tipocontrolelicitacao from cotacao cot
 inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id
 inner join lotecotacao lote on lote.cotacao_id = cot.id
 inner join itemcotacao item on item.lotecotacao_id = lote.id
 where item.id = it.id);
