update parcelavalordivida pvd set pvd.situacaoatual_ID =
  PACOTE_CONSULTA_DE_DEBITOS.GETULTIMASITUACAOPARCELA(pvd.id)
  where pvd.situacaoatual_ID is null
