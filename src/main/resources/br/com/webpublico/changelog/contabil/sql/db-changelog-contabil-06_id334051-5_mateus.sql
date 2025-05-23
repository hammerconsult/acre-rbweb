merge into CONTAAUXILIARDETALHADA contaaux using (
  select cad.id as cadId, cd.id as contaId
  from CONTAAUXILIARDETALHADA cad
    inner join FONTEDERECURSOS fr on cad.fonteDeRecursos_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) cauxd on (cauxd.cadId = contaaux.id)
when matched then update set contaaux.contadedestinacao_id = cauxd.contaId
