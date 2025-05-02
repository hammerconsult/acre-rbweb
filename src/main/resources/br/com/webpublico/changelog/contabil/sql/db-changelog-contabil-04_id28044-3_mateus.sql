merge into TransporteDeSaldoAbertura o
using (
select tsa.id, cab.TIPOMOVIMENTOCONTABIL from TransporteDeSaldoAbertura tsa
inner join eventocontabil ev on tsa.EVENTOCONTABIL_ID = ev.id
inner join VWHIERARQUIAORCAMENTARIA vw on tsa.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID
inner join entidade ent on vw.ENTIDADE_ID = ent.id
inner join configuracaoevento conf on EV.id = CONF.eventocontabil_id
INNER JOIN CONFIGABERTURAFECHAMENTOEX cab on cab.id = conf.id
where trunc(tsa.data) = to_date('01/01/2019', 'dd/mm/yyyy')
and to_date('01/01/2019', 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date('01/01/2019', 'dd/mm/yyyy'))
and ent.tipounidade IN ('EMPRESAPUBLICA', 'SOCIEDADE_DE_ECONOMIA_MISTA')
and cab.PATRIMONIOLIQUIDO = 'PRIVADO'
) ts on (ts.id = o.id)
when matched then update set o.tipomovimentocontabil = ts.TIPOMOVIMENTOCONTABIL
