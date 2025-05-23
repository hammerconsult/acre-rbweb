merge into RECEITAEXTRA re using (
 select recExtra.id as refrId, cd.id as contaId
 from RECEITAEXTRA recExtra
         inner join FONTEDERECURSOS fr on recExtra.fonteDeRecursos_ID = fr.id
         inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
         inner join conta c on cd.id = c.id
 where cd.DETALHAMENTOFONTEREC_ID is null
) refr on (refr.refrId = re.id)
when matched then update set re.contadedestinacao_id = refr.contaId
