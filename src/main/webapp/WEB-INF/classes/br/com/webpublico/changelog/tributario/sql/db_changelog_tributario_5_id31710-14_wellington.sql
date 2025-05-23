UPDATE calculo SET isentaacrescimos = 1 WHERE id in(
SELECT c.ID FROM declaracaomensalservico dms
INNER JOIN processocalculo pc ON pc.id = dms.PROCESSOCALCULO_ID
INNER JOIN calculo c ON c.PROCESSOCALCULO_ID = pc.id
INNER JOIN CADASTROECONOMICO ce ON ce.id = dms.PRESTADOR_ID
INNER JOIN NATUREZAJURIDICA nj ON nj.id = ce.NATUREZAJURIDICA_ID
INNER JOIN NATUREZAJURIDicaisencao nji ON nji.naturezajuridica_id = nj.id AND nji.tipomovimentomensal = dms.TIPOMOVIMENTO)
