CREATE OR REPLACE FORCE VIEW "VWRHFICHAFINANCEIRA" ("ID_VINCULO", "ID_FICHA","CALCULADAEM", "EFETIVADAEM",  "ANO", "MES", "TIPOFOLHADEPAGAMENTO", "VANTAGEM", "DESCONTO", "UM_TERCO_FERIAS", "RPPS", "BASERPPS", "REFERENCIARPPS", "INSS", "BASEINSS", "REFERENCIAINSS", "FGTS", "BASEFGTS", "REFERENCIAFGTS", "IRRF", "BASEIRRF", "REFERENCIAIRRF", "SALARIOMATERNIDADE", "BASESALARIOMATERNIDADE", "REFERENCIASALARIOMATERNIDADE", "SALARIOFAMILIA", "BASESALARIOFAMILIA", "REFERENCIASALARIOFAMILIA")
                            AS
  SELECT ficha.vinculofp_id AS id_vinculo,
    ficha.id                AS id_ficha,
    folha.calculadaEM,
    folha.efetivadaEm,
    folha.ano,
    folha.mes,
    folha.tipofolhadepagamento,
    (SELECT COALESCE(SUM(item.valor), 0)
    FROM itemfichafinanceirafp item
    WHERE item.tipoeventofp       = 'VANTAGEM'
    AND item.fichafinanceirafp_id = ficha.id
    ) AS vantagem,
    (SELECT COALESCE(SUM(item.valor), 0)
    FROM itemfichafinanceirafp item
    WHERE item.tipoeventofp       = 'DESCONTO'
    AND item.fichafinanceirafp_id = ficha.id
    ) AS desconto,
    (SELECT COALESCE(SUM(item.valor), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id                  = item.eventofp_id
    WHERE evento.codigo          IN ('151', '294','293')
    AND item.fichafinanceirafp_id = ficha.id
    ) AS um_terco_ferias,
    (SELECT COALESCE(SUM(item.valor), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 1
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS RPPS,
    (SELECT COALESCE(SUM(item.valorbasedecalculo), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 1
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS baseRPPS,
    (SELECT COALESCE(SUM(item.valorreferencia), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 1
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS referenciaRPPS,
    (SELECT COALESCE(SUM(item.valor), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 2
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS INSS,
    (SELECT COALESCE(SUM(item.valorbasedecalculo), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 2
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS baseINSS,
    (SELECT COALESCE(SUM(item.valorreferencia), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 2
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS referenciaINSS,
    (SELECT COALESCE(SUM(item.valor), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 3
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS FGTS,
    (SELECT COALESCE(SUM(item.valorbasedecalculo), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 3
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS baseFGTS,
    (SELECT COALESCE(SUM(item.valorreferencia), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 3
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS referenciaFGTS,
    (SELECT COALESCE(SUM(item.valor), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 4
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS IRRF,
    (SELECT COALESCE(SUM(item.valorbasedecalculo), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 4
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS baseIRRF,
    (SELECT COALESCE(SUM(item.valorreferencia), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 4
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS referenciaIRRF,
    (SELECT COALESCE(SUM(item.valor), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 5
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS salariomaternidade,
    (SELECT COALESCE(SUM(item.valorbasedecalculo), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 5
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS basesalariomaternidade,
    (SELECT COALESCE(SUM(item.valorreferencia), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 5
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS referenciasalariomaternidade,
    (SELECT COALESCE(SUM(item.valor), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 6
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS salariofamilia,
    (SELECT COALESCE(SUM(item.valorbasedecalculo), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 6
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS basesalariofamilia,
    (SELECT COALESCE(SUM(item.valorreferencia), 0)
    FROM itemfichafinanceirafp item
    JOIN eventofp evento
    ON evento.id     = item.eventofp_id
    WHERE evento.id IN
      (SELECT item.eventofp_id
      FROM itemgrupoexportacao item
      JOIN grupoexportacao grupo
      ON grupo.id = item.grupoexportacao_id
      JOIN moduloexportacao modulo
      ON modulo.id        = grupo.moduloexportacao_id
      WHERE modulo.codigo = 9
      AND grupo.codigo    = 6
      )
    AND item.fichafinanceirafp_id = ficha.id
    ) AS referenciasalariofamilia
  FROM fichafinanceirafp ficha
  JOIN folhadepagamento folha
  ON ficha.folhadepagamento_id = folha.id
