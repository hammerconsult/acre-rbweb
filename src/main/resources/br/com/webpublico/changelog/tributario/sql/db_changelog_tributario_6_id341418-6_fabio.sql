insert into DocumentoObrigatorioPortal(id, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA)
values (hibernate_sequence.nextval, 'Comprovante de endereço do requisitante', (select id from CONFIGPORTALCONTRIBUINTE), 'FISICA');

insert into DocumentoObrigatorioPortal(id, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA)
values (hibernate_sequence.nextval, 'Documento Oficial de identificação com foto do requerente (RG, Habilitação, Passaporte, Identidade profissional, tce)', (select id from CONFIGPORTALCONTRIBUINTE), 'FISICA');

insert into DocumentoObrigatorioPortal(id, DESCRICAO, CONFIGURACAO_ID, TIPOPESSOA)
values (hibernate_sequence.nextval, 'Comprovante de inscrição no CPF do requerente', (select id from CONFIGPORTALCONTRIBUINTE), 'FISICA');
