create or replace
    TRIGGER ESOCIALSITPESSOAMUDANCACAMPOS
    BEFORE
        UPDATE
    ON PESSOAFISICA
    FOR EACH ROW
DECLARE
    QUALIFICACAO_ID NUMBER;
BEGIN
    IF :NEW.DATANASCIMENTO <> :OLD.DATANASCIMENTO OR
       :NEW.CPF <> :OLD.CPF OR
       :NEW.NOME <> :OLD.NOME
    THEN
        select distinct qualificacaopessoa.ID
        into QUALIFICACAO_ID
        from QualificacaoCadastral qualificacao
                 inner join QUALIFICACAOCADPESSOA qualificacaopessoa
                            on qualificacaopessoa.QUALIFICACAOCADASTRAL_ID = qualificacao.id
                                and :new.id = qualificacaopessoa.PESSOA_ID
                                and qualificacao.DATAENVIO =
                                    (select max(QUALSUB.dataenvio)
                                     from QualificacaoCadastral QUALSUB
                                              inner join QUALIFICACAOCADPESSOA PESSOASUB
                                                         on PESSOAsub.QUALIFICACAOCADASTRAL_ID = QUALSUB.id
                                                             and :new.ID = PESSOASUB.PESSOA_ID
                                    ) and rownum = 1;
        UPDATE QUALIFICACAOCADPESSOA
        SET PROCESSADOREJEITADO = 'PENDENTE'
        WHERE PESSOA_ID = :NEW.ID
          AND PROCESSADOREJEITADO = 'QUALIFICADO'
          AND ID = QUALIFICACAO_ID;
        :new.SITUACAOQUALIFICACAOCADASTRAL := 'PENDENTE';
    end if;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('');
END;
