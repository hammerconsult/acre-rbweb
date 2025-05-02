MERGE INTO uniorc2013_2014 b
USING (
  SELECT 
     ID,ho.codigo
  FROM hierarquiaorganizacional HO 
  WHERE ho.iniciovigencia BETWEEN '01/01/2014' AND '31/12/2014'
  AND ho.tipohierarquiaorganizacional = 'ORCAMENTARIA'
  ) HO ON (b.cd_2014= HO.CODIGO)
WHEN MATCHED THEN
  UPDATE SET b.HO_ID_2014=HO.ID