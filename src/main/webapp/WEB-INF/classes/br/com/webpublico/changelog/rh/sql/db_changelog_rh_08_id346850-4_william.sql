CREATE OR REPLACE TRIGGER TRG_GERAR_CODIGO_VINCULOFPCENSO
BEFORE INSERT ON VinculoFPCenso
FOR EACH ROW
DECLARE
v_ano_atual NUMBER;
    v_ano_ultimo_registro NUMBER;
BEGIN
    v_ano_atual := EXTRACT(YEAR FROM SYSDATE);

BEGIN
SELECT EXTRACT(YEAR FROM MAX(horaRegistro))
INTO v_ano_ultimo_registro
FROM VinculoFPCenso;
EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_ano_ultimo_registro := v_ano_atual;
END;
    :NEW.codigo := TO_CHAR(v_ano_atual) || LPAD(SEQ_CODIGO_VINCULO_CENSO.NEXTVAL, 6, '0');
END;
