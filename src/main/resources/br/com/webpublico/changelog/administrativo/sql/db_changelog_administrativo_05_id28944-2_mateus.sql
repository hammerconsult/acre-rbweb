merge into DOCTOCOMPROBLEVBEMIMOVEL doctoComp using (
select docto.id, docto.UNIDADEADMINISTRATIVA_ID, docto.UNIDADEORCAMENTARIA_ID from DOCTOCOMPROBLEVBEMIMOVEL docto
inner join vwhierarquiaadministrativa vwadm on docto.UNIDADEADMINISTRATIVA_ID = vwadm.SUBORDINADA_ID
inner join VWHIERARQUIAORCAMENTARIA vworc on docto.UNIDADEORCAMENTARIA_ID = vworc.SUBORDINADA_ID
where docto.DATADOCUMENTO between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, docto.DATADOCUMENTO)
and docto.DATADOCUMENTO between vworc.INICIOVIGENCIA and coalesce(vworc.FIMVIGENCIA, docto.DATADOCUMENTO)
and vwadm.codigo like '01.25%'
and vworc.codigo like '01.012.501%'
  ) doc on (doc.id = doctoComp.id)
when matched then update set doctoComp.UNIDADEORCAMENTARIA_ID = (select subordinada_id from VWHIERARQUIAORCAMENTARIA vworc
where sysdate between vworc.INICIOVIGENCIA and coalesce(vworc.FIMVIGENCIA, sysdate)
and vworc.codigo like '01.017.501.000'),
doctoComp.UNIDADEADMINISTRATIVA_ID = (select subordinada_id from vwhierarquiaadministrativa vwadm
where sysdate between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, sysdate)
and vwadm.codigo like '01.25.00.00000.000.00')
