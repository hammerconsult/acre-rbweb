update PESSOAFISICA pf set pf.SITUACAOQUALIFICACAOCADASTRAL = (select distinct qualificacaopessoa.PROCESSADOREJEITADO
       from QualificacaoCadastral qualificacao
       inner join QUALIFICACAOCADPESSOA qualificacaopessoa on qualificacaopessoa.QUALIFICACAOCADASTRAL_ID = qualificacao.id
       and replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') = replace(replace(replace(qualificacaopessoa.cpf,'.',''),'-',''),'/','')
       and qualificacao.DATAENVIO = (select max(dataenvio) from QualificacaoCadastral));
