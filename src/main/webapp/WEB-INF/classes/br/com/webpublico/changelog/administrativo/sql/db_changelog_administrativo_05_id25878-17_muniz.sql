create or replace TRIGGER AJUSTAHORASEQUIPAMENTOINSERT AFTER
INSERT ON HORAPERCORRIDAEQUIPAMENTO FOR EACH ROW DECLARE HORAPERCORRIDAEQUIPAMENTO_ID NUMBER;
  BEGIN
    UPDATE EQUIPAMENTO SET HORAPERCORRIDA_ID = :NEW.ID
    WHERE ID = :NEW.EQUIPAMENTO_ID;
  END;
