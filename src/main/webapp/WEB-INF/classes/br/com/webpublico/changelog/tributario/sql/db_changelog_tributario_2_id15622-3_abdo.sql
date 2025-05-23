CREATE OR REPLACE TRIGGER AJUSTASITUACAOPARCELAINSERT before
  INSERT ON situacaoparcelavalordivida FOR EACH row DECLARE situacao_id NUMBER;
  situacao_data TIMESTAMP;
  BEGIN
    SELECT id,
      datalancamento
    INTO SITUACAO_ID,
      SITUACAO_DATA
    FROM
      (SELECT id,
        datalancamento
      FROM situacaoparcelavalordivida
      WHERE PARCELA_ID = :NEW.parcela_id
      ORDER BY DATALANCAMENTO DESC,
        id DESC
      )
    WHERE rownum = 1;
    dbms_output.put_line(situacao_id);
    DBMS_OUTPUT.PUT_LINE(SITUACAO_DATA);
    IF :NEW.DATALANCAMENTO > SITUACAO_DATA THEN
      UPDATE parcelavalordivida
      SET situacaoatual_id = :new.id
      where id             = :new.parcela_id;
      END IF;
  END;
