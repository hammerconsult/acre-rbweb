    create or replace FUNCTION numero_ajuizamento_parcela(
        PARCELA_ID IN NUMBER )
      RETURN VARCHAR2
    IS
      PROCESSOS       VARCHAR2(255);
      TIPO_CALCULO    VARCHAR2(255);
      NOVA_PARCELA_ID NUMBER;
    BEGIN
      dbms_output.put_line('ID da parcela ' || NOVA_PARCELA_ID);
      SELECT calculo.TIPOCALCULO
      INTO TIPO_CALCULO
      FROM calculo
      inner join valordivida vd on vd.calculo_id = calculo.id
      inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
      WHERE pvd.id = PARCELA_ID;
      dbms_output.put_line('Tipo de calcuilo ' || TIPO_CALCULO);
      IF TIPO_CALCULO    = 'PROCESSO_COMPENSACAO' THEN
       nova_parcela_id := parcela_original_pgto_judicial(parcela_id);
     ELSIF tipo_calculo = 'CANCELAMENTO_PARCELAMENTO' THEN
        nova_parcela_id := original_canc_parcelamento(parcela_id);
      ELSE
        NOVA_PARCELA_ID := PARCELA_ID;
    END IF;
    dbms_output.put_line('ID da parcela ' || NOVA_PARCELA_ID);
    SELECT listagg(numeroprocessoforum, ',') within GROUP (
    ORDER BY numeroprocessoforum) AS numero
    INTO PROCESSOS
    FROM
      ( WITH cteparcelamento(originada, parcelamento, original, calculo) AS
      (SELECT pvd.id    AS originada,
        parcelamento.id AS parcelamento,
        pvdoriginal.id  AS original,
        calculo.id      AS calculo
      FROM parcelavalordivida pvd
      INNER JOIN valordivida vd
      ON vd.id = pvd.valordivida_id
      INNER JOIN processoparcelamento parcelamento
      ON parcelamento.id = vd.CALCULO_ID
      INNER JOIN ITEMPROCESSOPARCELAMENTO itemparcelamento
      ON itemparcelamento.PROCESSOPARCELAMENTO_ID = parcelamento.id
      INNER JOIN parcelavalordivida pvdoriginal
      ON pvdoriginal.id = itemparcelamento.PARCELAVALORDIVIDA_ID
      INNER JOIN valordivida vdoriginal
      ON vdoriginal.id = pvdoriginal.valordivida_id
      INNER JOIN calculo
      ON calculo.id = vdoriginal.calculo_id
      WHERE pvd.id  = NOVA_PARCELA_ID
      AND rownum    = 1
      UNION ALL
      SELECT pvd.id     AS originada,
        parcelamento.id AS parcelamento,
        pvdoriginal.id  AS original,
        calculo.id      AS calculo
      FROM parcelavalordivida pvd
      INNER JOIN CTEPARCELAMENTO cte
      ON cte.original = pvd.id
      LEFT JOIN valordivida vd
      ON vd.id = pvd.valordivida_id
      LEFT JOIN processoparcelamento parcelamento
      ON parcelamento.id = vd.CALCULO_ID
      LEFT JOIN ITEMPROCESSOPARCELAMENTO itemparcelamento
      ON itemparcelamento.PROCESSOPARCELAMENTO_ID = parcelamento.id
      LEFT JOIN parcelavalordivida pvdoriginal
      ON pvdoriginal.id = itemparcelamento.PARCELAVALORDIVIDA_ID
      LEFT JOIN valordivida vdoriginal
      ON vdoriginal.id = pvdoriginal.valordivida_id
      INNER JOIN calculo
      ON calculo.id = vdoriginal.calculo_id
      WHERE rownum  = 1
      ) SEARCH DEPTH FIRST BY parcelamento
      SET SORTING CYCLE parcelamento
      SET is_cycle TO 1 DEFAULT 0
    SELECT DISTINCT processo.numeroprocessoforum
    FROM CTEPARCELAMENTO cte
    INNER JOIN parcelavalordivida pvd
    ON pvd.id = cte.originada
    LEFT JOIN ITEMINSCRICAODIVIDAATIVA itemDA
    ON itemDA.id= cte.calculo
    LEFT JOIN ITEMCERTIDAODIVIDAATIVA itemcda
    ON itemcda.iteminscricaodividaativa_id = itemda.id
    LEFT JOIN CERTIDAODIVIDAATIVA cda
    ON cda.id = itemcda.certidao_id
    LEFT JOIN processojudicialcda processocda
    ON processocda.certidaodividaativa_id = cda.id
    LEFT JOIN processojudicial processo
    ON processo.id          = processocda.processojudicial_id
    WHERE processo.situacao = 'ATIVO'
    UNION ALL
    SELECT processo.numeroprocessoforum
    FROM processojudicial processo
    INNER JOIN ProcessoJudicialCDA processocda
    ON processocda.processojudicial_id = processo.id
    INNER JOIN certidaodividaativa cda
    ON cda.id = processocda.certidaodividaativa_id
    INNER JOIN itemcertidaodividaativa itemcda
    ON itemcda.certidao_id = cda.id
    INNER JOIN iteminscricaodividaativa itemda
    ON itemda.id = itemcda.iteminscricaodividaativa_id
    INNER JOIN valordivida vd
    ON vd.calculo_id = itemda.id
    INNER JOIN parcelavalordivida pvd
    ON pvd.valordivida_id = vd.id
    WHERE pvd.id          = NOVA_PARCELA_ID
    AND processo.situacao = 'ATIVO'
      );
    RETURN PROCESSOS;
    END
