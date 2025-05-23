update SOLICITACAOMATERIAL set UNIDADEORGANIZACIONAL_ID = (
  select vwadm.subordinada_id from VWHIERARQUIAADMINISTRATIVA vwadm
  where codigo like '01.19.00.00000.000.00'
        and sysdate between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, sysdate)
) where id = 748944536
