insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Identidade', (select id from CONFIGPORTALCONTRIBUINTE), 'FISICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Cadastro de Pessoa Física CPF', (select id from CONFIGPORTALCONTRIBUINTE), 'FISICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Comprovante PIS/PASEP/NIT', (select id from CONFIGPORTALCONTRIBUINTE), 'FISICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Comprovante Conta Bancária', (select id from CONFIGPORTALCONTRIBUINTE), 'FISICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Comprovante de Endereço', (select id from CONFIGPORTALCONTRIBUINTE), 'FISICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Identidade do Empresário Individual ou dos Sócios', (select id from CONFIGPORTALCONTRIBUINTE), 'JURIDICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Cadastro de Pessoa Física (CPF) do Empresário Individual ou dos Sócios', (select id from CONFIGPORTALCONTRIBUINTE), 'JURIDICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Comprovante de Endereço (água, luz ou telefone)', (select id from CONFIGPORTALCONTRIBUINTE), 'JURIDICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Cadastro Nacional de Pessoa Jurídica (CNPJ)', (select id from CONFIGPORTALCONTRIBUINTE), 'JURIDICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Inscrição Estadual (I.E.)', (select id from CONFIGPORTALCONTRIBUINTE), 'JURIDICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Requerimento de Regis de Empresa Individual ou Contra Social, devidamente atualizados e registrados no Órgão competente', (select id from CONFIGPORTALCONTRIBUINTE), 'JURIDICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Comprovante de conta bancária da empresa', (select id from CONFIGPORTALCONTRIBUINTE), 'JURIDICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Certidão negativa de Débitos com o Tesouro Estadual e Municipal', (select id from CONFIGPORTALCONTRIBUINTE), 'JURIDICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Certidão negativa de Débitos com a Previdência Social', (select id from CONFIGPORTALCONTRIBUINTE), 'JURIDICA', 'CONTABIL');

insert into DocumentoObrigatorioPortal(ID, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA, TIPOSOLICITACAOCADASTROPESSOA)
values (hibernate_sequence.nextval, 'Certidão negativa de Débitos com a Receita Federal', (select id from CONFIGPORTALCONTRIBUINTE), 'JURIDICA', 'CONTABIL');

