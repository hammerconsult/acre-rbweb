UPDATE FichaFinanceiraFP
SET unidadeOrganizacional_id =
        (SELECT max(rdv.unidadeOrganizacional_id)
         FROM LotacaoFUncional rdv
         WHERE rdv.vinculoFP_ID = FichaFinanceiraFP.vinculoFP_ID
           AND (SELECT calculadaEm
                FROM FolhaDePagamento fp
                WHERE fp.id = FichaFinanceiraFP.folhaDePagamento_ID) BETWEEN rdv.inicioVigencia AND coalesce(rdv.finalVigencia, current_date))
WHERE unidadeOrganizacional_id is null;


UPDATE FichaFinanceiraFP
SET unidadeOrganizacional_id =
        (SELECT max(rdv.unidadeOrganizacional_id)
         FROM vinculofp rdv
         WHERE rdv.id = FichaFinanceiraFP.vinculoFP_ID)
WHERE unidadeOrganizacional_id is null;


update FICHAFINANCEIRAFP
set UNIDADEORGANIZACIONAL_ID = 58758827
where id in (
    select ficha.id
    from FICHAFINANCEIRAFP ficha
    where ficha.UNIDADEORGANIZACIONAL_ID = 638875979
      and ficha.id not in (
        select ficha.id
        from FICHAFINANCEIRAFP ficha
                 inner join VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = ficha.UNIDADEORGANIZACIONAL_ID
                 inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
        where ficha.UNIDADEORGANIZACIONAL_ID = 638875979
          and folha.CALCULADAEM between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, folha.CALCULADAEM)));
