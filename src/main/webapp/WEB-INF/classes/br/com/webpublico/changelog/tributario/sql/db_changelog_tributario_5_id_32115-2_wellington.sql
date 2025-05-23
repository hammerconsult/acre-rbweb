UPDATE CALCULOISS SET ISSQNFMTIPOLANCAMENTONFSE = 'PROPRIO'
WHERE ID IN (
SELECT C.ID FROM CALCULOISS C
INNER JOIN PROCESSOCALCULOISS PC ON PC.ID = C.PROCESSOCALCULOISS_ID
INNER JOIN DECLARACAOMENSALSERVICO DMS ON DMS.PROCESSOCALCULO_ID = PC.ID
WHERE DMS.TIPOMOVIMENTO IN ('NORMAL', 'INSTITUICAO_FINANCEIRA'));
UPDATE CALCULOISS SET ISSQNFMTIPOLANCAMENTONFSE = 'SUBSTITUTO'
WHERE ID IN (
SELECT C.ID FROM CALCULOISS C
INNER JOIN PROCESSOCALCULOISS PC ON PC.ID = C.PROCESSOCALCULOISS_ID
INNER JOIN DECLARACAOMENSALSERVICO DMS ON DMS.PROCESSOCALCULO_ID = PC.ID
WHERE DMS.TIPOMOVIMENTO NOT IN ('NORMAL', 'INSTITUICAO_FINANCEIRA'));
