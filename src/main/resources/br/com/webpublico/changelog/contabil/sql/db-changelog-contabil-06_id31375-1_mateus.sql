delete from COMPONENTEFORMULA
where id not in (
  select id from COMPONENTEFORMULACONTA
  union all
  select id from ComponenteFormulaAcao
  union all
  select id from COMPONENTEFORMULAFONTEREC
  union all
  select id from ComponenteFormulaFuncao
  union all
  select id from ComponenteFormulaItem
  union all
  select id from ComponenteFormulaPrograma
  union all
  select id from ComponenteFormulaSubConta
  union all
  select id from ComponenteFormulaSubFuncao
  union all
  select id from ComponenteFormulaSubacao
  union all
  select id from ComponenteFormulaTipoDesp
  union all
  select id from COMPONENTEFORMULAUO
)
