 merge into propostaconcessaodiaria p using (
select propostaId,
       case when sum(valorInscricao) - sum(valorBaixa) = 0 then 'COMPROVADO' else 'A_COMPROVAR' end as situacao
  from (
select prop.id as propostaId,
       prop.VALOR as valorDiaria,
       case
          when cont.TIPOLANCAMENTO = 'NORMAL' then cont.VALOR
          else cont.VALOR * - 1
       end as valorInscricao,
       0 as valorBaixa
  from propostaconcessaodiaria prop
 inner join DIARIACONTABILIZACAO cont on prop.id = cont.PROPOSTACONCESSAODIARIA_ID
 where prop.SITUACAODIARIA is null
   and cont.OPERACAODIARIACONTABILIZACAO = 'INSCRICAO'
 union all
select prop.id as propostaId,
       prop.VALOR as valorDiaria,
       0 as valorInscricao,
       case
          when cont.TIPOLANCAMENTO = 'NORMAL' then cont.VALOR
          else cont.VALOR * - 1
       end as valorBaixa
  from propostaconcessaodiaria prop
 inner join DIARIACONTABILIZACAO cont on prop.id = cont.PROPOSTACONCESSAODIARIA_ID
 where prop.SITUACAODIARIA is null
   and cont.OPERACAODIARIACONTABILIZACAO = 'BAIXA')
 group by propostaId, valorDiaria ) dados on (p.id = dados.propostaId)
  when matched then update set p.SITUACAODIARIA = dados.situacao
