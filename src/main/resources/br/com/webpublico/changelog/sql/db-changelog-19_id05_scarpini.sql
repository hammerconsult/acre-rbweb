create or replace
TRIGGER numeroDam BEFORE INSERT ON DAM 
FOR EACH ROW
BEGIN
    IF :NEW.numero is null and :NEW.exercicio_ID is not null then
       :NEW.numero := GETPROXIMONUMERODAM(:NEW.exercicio_ID);
       :NEW.numeroDAM := GETPROXIMONUMERODAMASSTRING(:NEW.numero, :NEW.exercicio_ID);
    END IF;
END;