merge into DOCTOCOMPROBLEVBEMIMOVEL doctoComp using (
                                                      select docto.id, docto.UNIDADEADMINISTRATIVA_ID, docto.UNIDADEORCAMENTARIA_ID from DOCTOCOMPROBLEVBEMIMOVEL docto
                                                        inner join vwhierarquiaadministrativa vwadm on docto.UNIDADEADMINISTRATIVA_ID = vwadm.SUBORDINADA_ID
                                                        inner join VWHIERARQUIAORCAMENTARIA vworc on docto.UNIDADEORCAMENTARIA_ID = vworc.SUBORDINADA_ID
                                                      where docto.DATADOCUMENTO between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, docto.DATADOCUMENTO)
                                                            and docto.DATADOCUMENTO between vworc.INICIOVIGENCIA and coalesce(vworc.FIMVIGENCIA, docto.DATADOCUMENTO)
                                                            and vwadm.codigo like '01.13%'
                                                            and vworc.codigo like '01.014.002%'
                                                    ) doc on (doc.id = doctoComp.id)
when matched then update set doctoComp.UNIDADEORCAMENTARIA_ID = (select subordinada_id from VWHIERARQUIAORCAMENTARIA vworc
where doctoComp.DATADOCUMENTO between vworc.INICIOVIGENCIA and coalesce(vworc.FIMVIGENCIA, doctoComp.DATADOCUMENTO)
      and vworc.codigo like '01.014.001.000'),
  doctoComp.UNIDADEADMINISTRATIVA_ID = (select subordinada_id from vwhierarquiaadministrativa vwadm
  where doctoComp.DATADOCUMENTO between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, doctoComp.DATADOCUMENTO)
        and vwadm.codigo like '01.13.00.00000.000.00')
