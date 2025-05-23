create or replace TRIGGER ESOCIALSITPESSOAMUDANCAPIS AFTER
UPDATE OR INSERT ON CARTEIRATRABALHO FOR EACH ROW
  declare ID_PESSOA number;
    cpf_pessoa varchar2(20);
    QUALIFICACAO_ID NUMBER;
  BEGIN
    IF :NEW.PISPASEP <> :OLD.PISPASEP or :OLD.ID is NULL
    then
      select PF.id, PF.CPF
      into ID_PESSOA, cpf_pessoa
      from DOCUMENTOPESSOAL doc
        inner join PESSOAFISICA pf on pf.id = doc.PESSOAFISICA_ID
      where doc.id = :new.ID and rownum =1;
      select distinct qualificacaopessoa.ID
      into QUALIFICACAO_ID
      from QualificacaoCadastral qualificacao
        inner join QUALIFICACAOCADPESSOA qualificacaopessoa on qualificacaopessoa.QUALIFICACAOCADASTRAL_ID = qualificacao.id
                                                               and ID_PESSOA = qualificacaopessoa.PESSOA_ID
                                                               and qualificacao.DATAENVIO =
                                                                   (select max(QUALSUB.dataenvio) from QualificacaoCadastral QUALSUB
                                                                     inner join QUALIFICACAOCADPESSOA PESSOASUB on PESSOAsub.QUALIFICACAOCADASTRAL_ID = QUALSUB.id
                                                                                                                   and ID_PESSOA = PESSOASUB.PESSOA_ID
                                                                   ) and rownum =1;
      UPDATE QUALIFICACAOCADPESSOA SET PROCESSADOREJEITADO = 'PENDENTE' WHERE PROCESSADOREJEITADO = 'QUALIFICADO' and id = QUALIFICACAO_ID;
      UPDATE PESSOAFISICA SET SITUACAOQUALIFICACAOCADASTRAL = 'PENDENTE' WHERE SITUACAOQUALIFICACAOCADASTRAL = 'QUALIFICADO' AND ID = ID_PESSOA;
    end if;
    EXCEPTION
    WHEN NO_DATA_FOUND THEN
    DBMS_OUTPUT.PUT_LINE('');
  END;
