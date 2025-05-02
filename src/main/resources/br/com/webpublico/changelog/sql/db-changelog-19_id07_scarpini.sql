create or replace
TRIGGER numeroNotaAvulsa BEFORE INSERT ON NFSAVULSA 
FOR EACH ROW
BEGIN
    IF :NEW.numero is null and :NEW.exercicio_ID is not null then
       :NEW.numero := GETPROXIMONUMERONFSAVULSA(:NEW.exercicio_ID);
    END IF;
END;