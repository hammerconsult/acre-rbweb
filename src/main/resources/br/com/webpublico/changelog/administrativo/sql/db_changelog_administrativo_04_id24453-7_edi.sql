update solicitacaomaterial sol set sol.tipoobjetocompra = 'PERMANENTE_MOVEL'
 where sol.id in (
 select
   s.id
  from solicitacaomaterial s
    inner join cotacao c on c.id = s.cotacao_id
    inner join formulariocotacao fc on fc.id = c.formulariocotacao_id
    inner join LoteFormularioCotacao lote on lote.formulariocotacao_id = fc.id
    where lote.tipoobjetocompra = 'PERMANENTE_MOVEL'
    and sol.id = s.id);


update solicitacaomaterial sol set sol.tipoobjetocompra = 'CONSUMO'
 where sol.id in (
 select
   s.id
  from solicitacaomaterial s
    inner join cotacao c on c.id = s.cotacao_id
    inner join formulariocotacao fc on fc.id = c.formulariocotacao_id
    inner join LoteFormularioCotacao lote on lote.formulariocotacao_id = fc.id
    where lote.tipoobjetocompra = 'CONSUMO'
    and sol.id = s.id);


update solicitacaomaterial sol set sol.tipoobjetocompra = 'SERVICO'
 where sol.id in (
 select
   s.id
  from solicitacaomaterial s
    inner join cotacao c on c.id = s.cotacao_id
    inner join formulariocotacao fc on fc.id = c.formulariocotacao_id
    inner join LoteFormularioCotacao lote on lote.formulariocotacao_id = fc.id
    where lote.tipoobjetocompra = 'SERVICO'
     and sol.id = s.id);


update solicitacaomaterial sol set sol.tipoobjetocompra = 'PERMANENTE_IMOVEL'
 where sol.id in (
 select
   s.id
  from solicitacaomaterial s
    inner join cotacao c on c.id = s.cotacao_id
    inner join formulariocotacao fc on fc.id = c.formulariocotacao_id
    inner join LoteFormularioCotacao lote on lote.formulariocotacao_id = fc.id
    where lote.tipoobjetocompra = 'PERMANENTE_IMOVEL'
      and sol.id = s.id)
