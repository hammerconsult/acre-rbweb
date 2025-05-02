UPDATE concessaoferiaslicenca
SET datainicial = TO_DATE('01/07/2014', 'dd/MM/yyyy'),
    datafinal   = TO_DATE('30/07/2014', 'dd/MM/yyyy')
WHERE datainicial = TO_DATE('01/07/2014', 'dd/MM/yyyy')
	AND datafinal = TO_DATE('01/06/1996', 'dd/MM/yyyy')