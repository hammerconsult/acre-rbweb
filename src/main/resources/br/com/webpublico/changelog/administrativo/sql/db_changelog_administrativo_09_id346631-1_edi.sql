insert into movimentoalteracaocont (id, alteracaocontratual_id, finalidade, valor, fontedespesaorc_id, operacao)
select hibernate_sequence.nextval,
       mov.alteracaocontratual_id,
       'ACRESCIMO',
       mov.valor,
       mov.fontedespesaorc_id,
       mov.operacao
from movimentoalteracaocont mov
where mov.operacao = 'TRANSFERENCIA_DOTACAO'
  and mov.dotacaosolicitacaomaterial_id is not null;

insert into movimentoalteracaocont (id, alteracaocontratual_id, finalidade, valor, fontedespesaorc_id, operacao)
select hibernate_sequence.nextval,
       mov.alteracaocontratual_id,
       'SUPRESSAO',
       mov.valor,
       f.fontedespesaorc_id,
       mov.operacao
from movimentoalteracaocont mov
         inner join dotacaosolmatitemfonte f on f.id = mov.dotacaosolicitacaomaterial_id
where mov.operacao = 'TRANSFERENCIA_DOTACAO'
  and mov.dotacaosolicitacaomaterial_id is not null;

delete from movimentoalteracaocont mov
where mov.operacao = 'TRANSFERENCIA_DOTACAO'
  and mov.finalidade = 'NAO_SE_APLICA'
  and mov.dotacaosolicitacaomaterial_id is not null;
