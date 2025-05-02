update ENTRADAMATERIAL ent
set ent.DATACONCLUSAO = (select em.DATAENTRADA
                         from entradamaterial em
                         where em.id = ent.id)
where trunc(ent.DATAENTRADA) <= to_date('05/12/2023', 'dd/MM/yyyy')
  and ent.NUMERO is not null;


update ENTRADAMATERIAL ent
set ent.DATACONCLUSAO = (select max(liq.DATALIQUIDACAO)
                         from LIQUIDACAO liq
                                  inner join liquidacaodoctofiscal ldf on ldf.LIQUIDACAO_ID = liq.id
                                  inner join doctofiscalentradacompra dec on dec.DOCTOFISCALLIQUIDACAO_ID = ldf.DOCTOFISCALLIQUIDACAO_ID
    inner join entradacompramaterial ecm on ecm.id = dec.ENTRADACOMPRAMATERIAL_ID
where ent.id = ecm.id)
where ent.DATACONCLUSAO is null
  and ent.NUMERO is not null;
