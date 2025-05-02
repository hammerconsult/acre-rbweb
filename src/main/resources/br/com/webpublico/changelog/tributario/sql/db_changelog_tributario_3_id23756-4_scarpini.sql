create or replace TRIGGER AJUSTA_HASH_DAM AFTER DELETE OR INSERT OR UPDATE
  ON ITEMDAM
FOR EACH ROW
  BEGIN
    update dam set HASH =  ORA_HASH(hash || :new.parcela_id)
    WHERE ID = :NEW.DAM_ID;
  END;
