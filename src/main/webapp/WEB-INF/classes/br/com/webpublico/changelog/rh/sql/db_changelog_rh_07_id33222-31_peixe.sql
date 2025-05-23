insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, tipoValor, SOMAVALORRETROATIVO)
select hibernate_sequence.nextval,
       dados.dataAtual,
       dados.operacao,
       dados.baseFP,
       dados.idEvento,
       dados.tipoValor,
       dados.somaRetroativo
from (select current_date                                     dataAtual,
             'ADICAO'                                         operacao,
             (select id from basefp where codigo = '1205') as baseFP,
             id                                            as idEvento,
             'NORMAL'                                      as tipoValor,
             0                                             as somaRetroativo
      from eventofp
      where coalesce(VERBAFIXA, 0) = 0
        AND TIPOEVENTOFP = 'VANTAGEM'
        and id in (select ev.eventofp_id from EVENTOFPTIPOFOLHA ev where ev.TIPOFOLHADEPAGAMENTO = 'NORMAL')
        and coalesce(ATIVO, 0) = 1
        and id not in (select ev.id
                       from eventofp ev
                       where coalesce(ev.VERBAFIXA, 0) = 1
                         and coalesce(ev.ATIVO, 0) = 1)
      order by cast(codigo as int)) dados
