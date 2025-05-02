update empenho set dividapublica_id = null where id in (
select emp.id from empenho emp
inner join fontedespesaorc fontDesp on emp.FONTEDESPESAORC_ID = fontDesp.id
inner join provisaoppafonte provFont on fontDesp.PROVISAOPPAFONTE_ID = provFont.id
inner join CONTADEDESTINACAO cd on provFont.DESTINACAODERECURSOS_ID = cd.id
inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_ID = fr.id
where fr.TIPOFONTERECURSO = 'OPERACAO_CREDITO')
