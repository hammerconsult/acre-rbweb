CREATE OR REPLACE
TRIGGER ESOCIALSITPESSOAMUDANCACAMPOS BEFORE
UPDATE ON PESSOAFISICA FOR EACH ROW
  DECLARE QUALIFICACAO_ID NUMBER;
BEGIN
  IF :NEW.DATANASCIMENTO <> :OLD.DATANASCIMENTO OR
     :NEW.CPF <> :OLD.CPF OR
     :NEW.NOME <> :OLD.NOME
  THEN
    select distinct qualificacaopessoa.ID
    into QUALIFICACAO_ID
    from QualificacaoCadastral qualificacao
      inner join QUALIFICACAOCADPESSOA qualificacaopessoa on qualificacaopessoa.QUALIFICACAOCADASTRAL_ID = qualificacao.id
                                                             and replace(replace(replace(:new.cpf,'.',''),'-',''),'/','') = replace(replace(replace(qualificacaopessoa.cpf,'.',''),'-',''),'/','')
                                                             and qualificacao.DATAENVIO =
                                                                 (select max(QUALSUB.dataenvio) from QualificacaoCadastral QUALSUB
                                                                   inner join QUALIFICACAOCADPESSOA PESSOASUB on PESSOAsub.QUALIFICACAOCADASTRAL_ID = QUALSUB.id
                                                                                                                 and replace(replace(replace(qualificacaopessoa.CPF,'.',''),'-',''),'/','') = replace(replace(replace(PESSOASUB.cpf,'.',''),'-',''),'/','')
                                                                 );
    UPDATE QUALIFICACAOCADPESSOA SET PROCESSADOREJEITADO = 'PENDENTE' WHERE PESSOA_ID = :NEW.ID AND PROCESSADOREJEITADO = 'QUALIFICADO' and id = QUALIFICACAO_ID;
    :new.SITUACAOQUALIFICACAOCADASTRAL := 'PENDENTE';
  END IF;
END;
