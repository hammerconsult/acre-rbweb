merge into CONTAAUXILIARDETALHADA cad using (
  select
    cdest.id as cdest,
    cd.id as cadId
  from CONTAAUXILIARDETALHADA cd
    inner join conta c on cd.id = c.id
    inner join tipocontaauxiliar tc on cd.TIPOCONTAAUXILIAR_ID = tc.id
    inner join fontederecursos fr on c.exercicio_id = fr.EXERCICIO_ID and fr.codigo = substr(c.codigo, 12, 3)
    inner join contadedestinacao cdest on fr.id = cdest.FONTEDERECURSOS_ID
  where tc.codigo in ('94')
        and substr(c.codigo, 12, 3) is not null
        and cd.CONTADEDESTINACAO_ID is null
  union all
  select
    cdest.id as cdest,
    cd.id as cadId
  from CONTAAUXILIARDETALHADA cd
    inner join conta c on cd.id = c.id
    inner join tipocontaauxiliar tc on cd.TIPOCONTAAUXILIAR_ID = tc.id
    inner join fontederecursos fr on c.exercicio_id = fr.EXERCICIO_ID and fr.codigo = substr(c.codigo, 10, 3)
    inner join contadedestinacao cdest on fr.id = cdest.FONTEDERECURSOS_ID
  where tc.codigo in ('95', '96')
        and substr(c.codigo, 10, 3) is not null
        and cd.CONTADEDESTINACAO_ID is null
  union all
  select
    cdest.id as cdest,
    cd.id as cadId
  from CONTAAUXILIARDETALHADA cd
    inner join conta c on cd.id = c.id
    inner join tipocontaauxiliar tc on cd.TIPOCONTAAUXILIAR_ID = tc.id
    inner join fontederecursos fr on c.exercicio_id = fr.EXERCICIO_ID and fr.codigo = substr(c.codigo, 16, 3)
    inner join contadedestinacao cdest on fr.id = cdest.FONTEDERECURSOS_ID
  where tc.codigo in ('97', '99')
        and substr(c.codigo, 16, 3) is not null
        and cd.CONTADEDESTINACAO_ID is null
) contas on (contas.cadId = cad.id)
when matched then update set CONTADEDESTINACAO_ID = contas.cdest
