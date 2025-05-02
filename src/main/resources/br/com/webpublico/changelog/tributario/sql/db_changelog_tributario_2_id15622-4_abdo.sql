create or replace
trigger ajustasituacaoparceladelete BEFORE
DELETE ON situacaoparcelavalordivida FOR EACH row
  DECLARE
    situacao_id number;
      Pragma Autonomous_Transaction;
begin
    dbms_output.put_line('EXECUTANDO');
    SELECT id
    INTO SITUACAO_ID
    FROM
      (SELECT id
       FROM situacaoparcelavalordivida
       WHERE PARCELA_ID = :old.parcela_id and id <> :old.id
       ORDER BY DATALANCAMENTO DESC,
         id DESC
      )
    where rownum = 1;
    dbms_output.put_line(situacao_id);
    UPDATE parcelavalordivida
    SET situacaoatual_id = SITUACAO_ID
    where id             = :old.parcela_id;
    COMMIT;
END;
