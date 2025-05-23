merge into levantamentobemimovel levantamento using (
select lev.id, lev.UNIDADEADMINISTRATIVA_ID, lev.UNIDADEORCAMENTARIA_ID from LevantamentoBemImovel lev
inner join vwhierarquiaadministrativa vwadm on lev.UNIDADEADMINISTRATIVA_ID = vwadm.SUBORDINADA_ID
inner join VWHIERARQUIAORCAMENTARIA vworc on lev.UNIDADEORCAMENTARIA_ID = vworc.SUBORDINADA_ID
where lev.DATALEVANTAMENTO between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, lev.DATALEVANTAMENTO)
and lev.DATALEVANTAMENTO between vworc.INICIOVIGENCIA and coalesce(vworc.FIMVIGENCIA, lev.DATALEVANTAMENTO)
and vwadm.codigo like '01.25%'
and vworc.codigo like '01.012.501%'
  ) l on (l.id = levantamento.id)
when matched then update set levantamento.UNIDADEORCAMENTARIA_ID = (select subordinada_id from VWHIERARQUIAORCAMENTARIA vworc
where sysdate between vworc.INICIOVIGENCIA and coalesce(vworc.FIMVIGENCIA, sysdate)
and vworc.codigo like '01.017.501.000'),
levantamento.UNIDADEADMINISTRATIVA_ID = (select subordinada_id from vwhierarquiaadministrativa vwadm
where sysdate between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, sysdate)
and vwadm.codigo like '01.25.00.00000.000.00')
