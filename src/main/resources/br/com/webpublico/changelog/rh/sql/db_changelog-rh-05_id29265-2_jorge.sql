update DEPENDENTEVINCULOFP
set INICIOVIGENCIA    = TO_DATE('17/07/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA     = TO_DATE('17/07/2019', 'dd/MM/yyyy')
where INICIOVIGENCIA is null
	and FINALVIGENCIA is null
	and TIPODEPENDENTE_ID is null