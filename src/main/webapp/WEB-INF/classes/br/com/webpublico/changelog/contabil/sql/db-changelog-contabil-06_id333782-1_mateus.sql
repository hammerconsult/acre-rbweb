INSERT ALL
    into COMPONENTEFORMULA (id, dataregistro, FORMULAITEMDEMONSTRATIVO_ID) values (hibernate_sequence.nextval, sysdate, idFormula)
    into COMPONENTEFORMULACONTA (id, CONTA_ID) values (hibernate_sequence.currval, idConta)
select
  form.id as idFormula,
  c.id as idConta
from relatoriositemdemonst rel
  inner join itemdemonstrativo it on rel.id = it.RELATORIOSITEMDEMONST_ID
  inner join FORMULAITEMDEMONSTRATIVO form on it.id = form.ITEMDEMONSTRATIVO_ID
  inner join COMPONENTEFORMULA cf on cf.FORMULAITEMDEMONSTRATIVO_ID = form.ID
  inner join COMPONENTEFORMULAFONTEREC cfr on cf.id = cfr.ID
  inner join FONTEDERECURSOS fr on cfr.FONTEDERECURSOS_ID = fr.id
  inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
  inner join conta c on cd.id = c.id
where cd.DETALHAMENTOFONTEREC_ID is null
