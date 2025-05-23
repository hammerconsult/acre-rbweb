merge into SubContaDividaPublica sc using (
  select subcontadp.id as scdpId, cd.id as contaId
  from SubContaDividaPublica subcontadp
    inner join FONTEDERECURSOS fr on subcontadp.fonteDeRecursos_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) scdp on (scdp.scdpId = sc.id)
when matched then update set sc.contadedestinacao_id = scdp.contaId
