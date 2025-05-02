create or replace TRIGGER ATUALIZARSITUACAOFUNCIONALDEL after
    DELETE ON SITUACAOCONTRATOFP FOR EACH ROW
      DECLARE
        situacao_id NUMBER;
        PRAGMA AUTONOMOUS_TRANSACTION;
    BEGIN
        SELECT ID
        INTO situacao_id
        FROM
          (SELECT SITUACAOCONTRATOFP.SITUACAOFUNCIONAL_ID AS id
           FROM SITUACAOCONTRATOFP
           WHERE SITUACAOCONTRATOFP.contratofp_id = :OLD.contratofp_id AND ID <> :OLD.ID
           ORDER BY SITUACAOCONTRATOFP.INICIOVIGENCIA DESC,
             SITUACAOCONTRATOFP.ID DESC)
        WHERE ROWNUM = 1;
        DBMS_OUTPUT.PUT_LINE(situacao_id);
        UPDATE contratofp SET contratofp.situacaofuncional_ID = situacao_id
        WHERE contratofp.ID = :OLD.contratofp_id;
        commit;
        DBMS_OUTPUT.PUT_LINE(situacao_id);
        DBMS_OUTPUT.PUT_LINE(:OLD.contratofp_id);

       EXCEPTION
         WHEN NO_DATA_FOUND THEN
         DBMS_OUTPUT.PUT_LINE('exception');
    END;
