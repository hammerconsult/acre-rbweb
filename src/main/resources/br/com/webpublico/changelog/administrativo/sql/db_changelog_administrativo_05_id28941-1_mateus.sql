merge into levantamentobemimovel levantamento using (
select lev.id, lev.GRUPOBEM_ID from LevantamentoBemImovel lev
inner join vwhierarquiaadministrativa vwadm on lev.UNIDADEADMINISTRATIVA_ID = vwadm.SUBORDINADA_ID
inner join VWHIERARQUIAORCAMENTARIA vworc on lev.UNIDADEORCAMENTARIA_ID = vworc.SUBORDINADA_ID
inner join grupobem gb on lev.GRUPOBEM_ID = gb.id
where gb.codigo = '01.01.02.0093'
and lev.DATALEVANTAMENTO between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, lev.DATALEVANTAMENTO)
and lev.DATALEVANTAMENTO between vworc.INICIOVIGENCIA and coalesce(vworc.FIMVIGENCIA, lev.DATALEVANTAMENTO)
and vwadm.codigo like '01.11%'
and vworc.codigo like '01.012.001%'
  ) l on (l.id = levantamento.id)
when matched then update set levantamento.GRUPOBEM_ID = (
select gb.id from grupobem gb
where gb.codigo = '01.01.02.0091')
