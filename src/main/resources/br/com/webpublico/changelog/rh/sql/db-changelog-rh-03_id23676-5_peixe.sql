create or replace TRIGGER ATUALIZARSITUACAOFUNCIONAL AFTER
    INSERT ON situacaocontratofp FOR EACH ROW DECLARE situacaofuncional_id NUMBER;
    BEGIN
       UPDATE contratofp SET situacaofuncional_id = :NEW.situacaofuncional_id
       WHERE ID = :NEW.contratoFP_id;
    END;
