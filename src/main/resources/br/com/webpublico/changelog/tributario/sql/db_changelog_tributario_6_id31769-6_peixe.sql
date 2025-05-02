CREATE OR REPLACE TRIGGER updateCodigoCnaePJParaBi
    AFTER INSERT or UPDATE or DELETE
    ON PESSOAJURIDICA
    FOR EACH ROW
BEGIN
    update pessoa
    set CODIGOCNAEBI = (select max(cnae.codigocnae)
                        from cnae
                                 left join pessoacnae pescnae on cnae.id = pescnae.cnae_id
                            and pescnae.tipo = 'Primaria'
                            and cnae.situacao = 'ATIVO'
                        where pescnae.pessoa_id = :OLD.ID)
    where id = :OLD.ID;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('exception');
END;
