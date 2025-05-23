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
             (select id from basefp where codigo = '1202') as baseFP,
             id                                            as idEvento,
             'NORMAL'                                      as tipoValor,
             0                                             as somaRetroativo
      from eventofp
      where codigo in ('248', '1101', '1102', '1104')
        and id not in (select it.eventofp_id
                       from itembasefp it
                                inner join basefp b on it.BASEFP_ID = b.ID
                       where b.codigo = '1202')
     ) dados;
