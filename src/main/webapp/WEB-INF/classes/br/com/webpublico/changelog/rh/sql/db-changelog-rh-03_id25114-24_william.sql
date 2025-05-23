CREATE OR REPLACE
TRIGGER ESOCIALSITPESSOAMUDANCAPIS AFTER
UPDATE ON CARTEIRATRABALHO FOR EACH ROW
 DECLARE ID_PESSOA NUMBER;
 QUALIFICACAO_ID NUMBER;
  BEGIN
    IF :NEW.PISPASEP <> :OLD.PISPASEP
    THEN
      select pf.id
      into ID_PESSOA
      from DOCUMENTOPESSOAL doc
      inner join PESSOAFISICA pf on pf.id = doc.PESSOAFISICA_ID
      where doc.id = :new.ID;

      select distinct qualificacaopessoa.ID
      into QUALIFICACAO_ID
      from QualificacaoCadastral qualificacao
        inner join QUALIFICACAOCADPESSOA qualificacaopessoa on qualificacaopessoa.QUALIFICACAOCADASTRAL_ID = qualificacao.id
                                                               and ID_PESSOA = qualificacaopessoa.PESSOA_ID
                                                               and qualificacao.DATAENVIO =
                                                                   (select max(QUALSUB.dataenvio) from QualificacaoCadastral QUALSUB
                                                                     inner join QUALIFICACAOCADPESSOA PESSOASUB on PESSOAsub.QUALIFICACAOCADASTRAL_ID = QUALSUB.id
                                                                                                                   and replace(replace(replace(qualificacaopessoa.CPF,'.',''),'-',''),'/','') = replace(replace(replace(PESSOASUB.cpf,'.',''),'-',''),'/','')
                                                                   );
      UPDATE QUALIFICACAOCADPESSOA SET PROCESSADOREJEITADO = 'PENDENTE' WHERE PROCESSADOREJEITADO = 'QUALIFICADO' and id = QUALIFICACAO_ID;
      UPDATE PESSOAFISICA SET SITUACAOQUALIFICACAOCADASTRAL = 'PENDENTE' WHERE SITUACAOQUALIFICACAOCADASTRAL = 'QUALIFICADO' AND ID = ID_PESSOA;
    END IF;
  END;
