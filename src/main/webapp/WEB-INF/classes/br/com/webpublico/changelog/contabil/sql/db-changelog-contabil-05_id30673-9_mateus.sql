merge into empenho e using (
select emp.id,
       fr.id as fonteId,
       cd.id as contaDeDestinacaoId,
       cDesp.id as contaDeDespesaId
  from empenho emp
 inner join despesaorc desp on emp.despesaorc_id = desp.id
 inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id
 inner join contadespesa cDesp on provdesp.CONTADEDESPESA_ID = cdesp.id
 inner join fontedespesaorc fontdesp on emp.fontedespesaorc_id = fontdesp.id
 inner join provisaoppafonte provfonte on fontdesp.provisaoppafonte_id = provfonte.id
 inner join contadedestinacao cd on provfonte.destinacaoderecursos_id = cd.id
 inner join fontederecursos fr on cd.fontederecursos_id = fr.id
 where emp.CATEGORIAORCAMENTARIA = 'NORMAL'
 ) empNormal on (empNormal.id = e.id)
when matched then
update set e.FONTEDERECURSOS_ID = empNormal.fonteId,
e.CONTADESPESA_ID = empNormal.contaDeDespesaId,
e.CONTADEDESTINACAO_ID = empNormal.contaDeDestinacaoId
