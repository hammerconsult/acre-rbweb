update loteformulariocotacao set tipocontrolelote = 'QUANTIDADE';

update lotecotacao set tipoobjetocompra = (
select lfc.tipoobjetocompra
   from cotacao c
  inner join lotecotacao lc on lc.cotacao_id = c.id
  inner join loteformulariocotacao lfc on lfc.formulariocotacao_id = c.formulariocotacao_id
                                      and lfc.numero = lc.numero
where lotecotacao.id = lc.id);

update lotecotacao set tipocontrolelote = (
select lfc.tipocontrolelote
   from cotacao c
  inner join lotecotacao lc on lc.cotacao_id = c.id
  inner join loteformulariocotacao lfc on lfc.formulariocotacao_id = c.formulariocotacao_id
                                      and lfc.numero = lc.numero
where lotecotacao.id = lc.id);
