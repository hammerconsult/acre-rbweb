merge into empenho e using (
select emp.dataempenho,
       emp.id,
       fre.FONTEDERECURSOSDESTINO_ID as fonteDestino,
       cDestEq.CONTADESTINO_ID as contaEquivalenteDestino,
       cDespEq.CONTADESTINO_ID as contadespesadestino
  from empenho emp
 inner join despesaorc desp on emp.despesaorc_id = desp.id
 inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id
 inner join contadespesa cDesp on provdesp.CONTADEDESPESA_ID = cdesp.id
 inner join CONTAEQUIVALENTE cDespEq ON cDesp.ID = cDespEq.CONTAorigem_ID
 inner join fontedespesaorc fontdesp on emp.fontedespesaorc_id = fontdesp.id
 inner join provisaoppafonte provfonte on fontdesp.provisaoppafonte_id = provfonte.id
 inner join contadedestinacao cDest on provfonte.destinacaoderecursos_id = cDest.id
 inner join conta contaDest on contaDest.id = cDest.id
 inner join CONTAEQUIVALENTE cDestEq ON contaDest.ID = cDestEq.CONTAorigem_ID
 inner join fontederecursos fr on cDest.fontederecursos_id = fr.id
 inner join FonteDeRecursosEquivalente fre on fr.id = fre.FONTEDERECURSOSORIGEM_ID
 where emp.CATEGORIAORCAMENTARIA = 'RESTO'
 order by emp.dataempenho asc
 )
 empRest on (empRest.id = e.id)
when matched then
update set e.FONTEDERECURSOS_ID = empRest.fonteDestino,
e.CONTADESPESA_ID = empRest.contadespesadestino,
e.CONTADEDESTINACAO_ID = empRest.contaEquivalenteDestino
