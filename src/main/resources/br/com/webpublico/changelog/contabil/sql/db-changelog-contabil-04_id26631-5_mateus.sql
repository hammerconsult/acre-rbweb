update empenho e set e.extensaoFonteRecurso_id = (
select provFonte.EXTENSAOFONTERECURSO_ID from empenho emp
inner join fontedespesaorc fontDesp on emp.FONTEDESPESAORC_ID = fontDesp.id
inner join provisaoppafonte provFonte on fontDesp.PROVISAOPPAFONTE_ID = provFonte.id
where e.id = emp.id)
