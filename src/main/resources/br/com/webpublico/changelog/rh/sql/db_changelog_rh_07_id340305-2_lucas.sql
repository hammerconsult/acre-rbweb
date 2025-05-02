insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, tipoValor, SOMAVALORRETROATIVO)
select hibernate_sequence.nextval,
       dados.dataAtual,
       dados.operacao,
       dados.baseFP,
       dados.idEvento,
       dados.tipoValor,
       dados.somaRetroativo
from (select distinct base.id      baseFP,
                      current_date dataAtual,
                      'ADICAO'     operacao,
                      585805838 as idEvento,
                      'NORMAL'  as tipoValor,
                      1         as somaRetroativo
      from BeneficioPensaoAlimenticia benef
               inner join valorPensaoAlimenticia valor on benef.ID = valor.BENEFICIOPENSAOALIMENTICIA_ID
               inner join basefp base on valor.BASEFP_ID = base.ID
      where current_date between valor.inicioVigencia and coalesce(valor.finalVigencia, current_date)
        and current_date between benef.inicioVigencia and coalesce(benef.finalVigencia, current_date)
        and base.ID not in (select distinct b.id
                            from basefp b
                                     inner join ItemBaseFP item on b.ID = item.BASEFP_ID
                            where item.EVENTOFP_ID  = 585805838
                              and b.id = base.id)) dados;
