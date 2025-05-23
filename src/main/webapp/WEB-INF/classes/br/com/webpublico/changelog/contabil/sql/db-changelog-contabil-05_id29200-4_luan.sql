merge into ITENS_PLAN it using (
  select ip.id, moe.id as MACROOBJETIVOESTRATEGICO_ID
  from ITENS_PLAN ip
    inner join MACROOBJETIVOESTRATEGICO moe on ip.nome  = moe.DESCRICAO and ip.PLANEJAMENTOESTRATEGICO_ID = moe.PLANEJAMENTOESTRATEGICO_ID
) i on (i.id = it.id)
when matched then update set MACROOBJETIVOESTRATEGICO_ID = i.MACROOBJETIVOESTRATEGICO_ID
