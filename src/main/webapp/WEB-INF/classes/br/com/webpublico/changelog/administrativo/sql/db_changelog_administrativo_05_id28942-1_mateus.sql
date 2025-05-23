merge into levantamentobemimovel levantamento using (
select lev.id, lev.UNIDADEADMINISTRATIVA_ID, lev.UNIDADEORCAMENTARIA_ID from LevantamentoBemImovel lev
inner join vwhierarquiaadministrativa vwadm on lev.UNIDADEADMINISTRATIVA_ID = vwadm.SUBORDINADA_ID
inner join VWHIERARQUIAORCAMENTARIA vworc on lev.UNIDADEORCAMENTARIA_ID = vworc.SUBORDINADA_ID
where lev.DATALEVANTAMENTO between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, lev.DATALEVANTAMENTO)
and lev.DATALEVANTAMENTO between vworc.INICIOVIGENCIA and coalesce(vworc.FIMVIGENCIA, lev.DATALEVANTAMENTO)
and vwadm.codigo like '01.13%'
and vworc.codigo like '01.014.003%'
  ) l on (l.id = levantamento.id)
when matched then update set levantamento.UNIDADEORCAMENTARIA_ID = (select subordinada_id from VWHIERARQUIAORCAMENTARIA vworc
where levantamento.datalevantamento between vworc.INICIOVIGENCIA and coalesce(vworc.FIMVIGENCIA, levantamento.datalevantamento)
and vworc.codigo like '01.014.001.000'),
levantamento.UNIDADEADMINISTRATIVA_ID = (select subordinada_id from vwhierarquiaadministrativa vwadm
where levantamento.datalevantamento between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, levantamento.datalevantamento)
and vwadm.codigo like '01.13.00.00000.000.00')
