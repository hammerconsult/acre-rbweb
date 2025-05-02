delete from MENU where PAI_ID = (select id from MENU where LABEL = 'DOCUMENTO DE HABILITAÇÃO - LICITAÇÃO');
delete from MENU where PAI_ID = (select id from MENU where LABEL = 'SERVIÇO');
delete from MENU where PAI_ID = (select id from MENU where LABEL = 'OBJETO DE COMPRA');
delete from MENU where PAI_ID = (select id from MENU where LABEL = 'CADASTROS GERAIS - LICITAÇÃO');
delete from MENU where PAI_ID = (select id from MENU where LABEL = 'PREGÃO');
delete from MENU where PAI_ID = (select id from MENU where LABEL = 'ADESÃO INTERNA');
delete from MENU where PAI_ID = (select id from MENU where LABEL = 'ADESÃO EXTERNA');
delete from MENU where PAI_ID = (select id from MENU where LABEL = 'RELATÓRIOS - LICITAÇÃO');
delete from MENU where CAMINHO = '/dashboard/dashboard-contrato-licitacao.xhtml';
delete from MENU where LABEL = 'CONFIGURAÇÕES E UTILITÁRIOS - COMPRAS';
delete from MENU where PAI_ID = (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES');
COMMIT;


insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CONFIGURAÇÕES E UTILITÁRIOS - COMPRAS', null, (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES'), 1, 'fa-cog');

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CONFIGURAÇÕES DE LICITAÇÃO', '/administrativo/configuracao/geral/lista.xhtml', (select id from MENU where LABEL = 'CONFIGURAÇÕES E UTILITÁRIOS - COMPRAS'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'ALTERAÇÃO FORNECEDOR LICITAÇÃO', '/administrativo/licitacao/alteracao-fornecedor-licitacao/lista.xhtml',  (select id from MENU where LABEL = 'CONFIGURAÇÕES E UTILITÁRIOS - COMPRAS'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'EXCLUSÃO DE DISPENSA', '/administrativo/licitacao/exclusao-dispensa-licitacao/lista.xhtml',  (select id from MENU where LABEL = 'CONFIGURAÇÕES E UTILITÁRIOS - COMPRAS'), 10, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'EXCLUSÃO DE LICITAÇÃO', '/administrativo/licitacao/exclusao-licitacao/lista.xhtml',  (select id from MENU where LABEL = 'CONFIGURAÇÕES E UTILITÁRIOS - COMPRAS'), 15, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'SUBSTITUIÇÃO DE OBJETO DE COMPRA', '/administrativo/licitacao/substituicao-objeto-compra/lista.xhtml',  (select id from MENU where LABEL = 'CONFIGURAÇÕES E UTILITÁRIOS - COMPRAS'), 20, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CONFIGURAÇÃO DE OBJETO DE COMPRA', '/administrativo/licitacao/alteracaogrupooc/lista.xhtml',  (select id from MENU where LABEL = 'CONFIGURAÇÕES E UTILITÁRIOS - COMPRAS'), 25, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CADASTROS GERAIS - COMPRAS', null, (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES'), 5, 'fa-bars');

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'DOCUMENTO DE HABILITAÇÃO - LICITAÇÃO', null, (select id from MENU where LABEL = 'CADASTROS GERAIS - COMPRAS'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'TIPO DE DOCUMENTO DE HABILITAÇÃO', '/administrativo/licitacao/tipodoctohabilitacao/lista.xhtml', (select id from MENU where LABEL = 'DOCUMENTO DE HABILITAÇÃO - LICITAÇÃO'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'DOCUMENTO DE HABILITAÇÃO', '/administrativo/licitacao/doctohabilitacao/lista.xhtml', (select id from MENU where LABEL = 'DOCUMENTO DE HABILITAÇÃO - LICITAÇÃO'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'MODELO DE DOCUMENTO', '/administrativo/licitacao/modelodocto/lista.xhtml', (select id from MENU where LABEL = 'CADASTROS GERAIS - COMPRAS'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'SERVIÇO', null, (select id from MENU where LABEL = 'CADASTROS GERAIS - COMPRAS'), 10, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'GRUPO DE SERVIÇO', '/administrativo/licitacao/gruposervicocompravel/lista.xhtml', (select id from MENU where LABEL = 'SERVIÇO'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'SERVIÇO - CADASTRO', '/administrativo/licitacao/servicocompravel/lista.xhtml', (select id from MENU where LABEL = 'SERVIÇO'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'OBJETO DE COMPRA', null, (select id from MENU where LABEL = 'CADASTROS GERAIS - COMPRAS'), 15, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'GRUPO DE OBJETO DE COMPRA', '/administrativo/licitacao/grupoobjetocompra/lista.xhtml', (select id from MENU where LABEL = 'OBJETO DE COMPRA'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CADASTRO DE OBJETO DE COMPRA', '/administrativo/licitacao/objetocompra/lista.xhtml', (select id from MENU where LABEL = 'OBJETO DE COMPRA'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'EFETIVAÇÃO DE OBJETO DE COMPRA', '/administrativo/patrimonio/efetivacaoobjetocompra/lista.xhtml', (select id from MENU where LABEL = 'OBJETO DE COMPRA'), 10, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CADASTRO DE TIPO DE DOCUMENTO ANEXO', '/administrativo/licitacao/tipodocumentoanexo/lista.xhtml', (select id from MENU where LABEL = 'CADASTROS GERAIS - COMPRAS'), 20, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CADASTRO DE COMISSÃO', '/administrativo/licitacao/comissao/lista.xhtml', (select id from MENU where LABEL = 'CADASTROS GERAIS - COMPRAS'), 25, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'PLANO DE CONTRATAÇÕES ANUAL', '/administrativo/licitacao/pac/lista.xhtml', (select id from MENU where LABEL = 'CADASTROS GERAIS - COMPRAS'), 35, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'FASE INTERNA - PREPARATÓRIA', null, (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES'), 10, 'fa-clipboard');

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'INTENÇÃO DE REGISTRO DE PREÇO - IRP', null, (select id from MENU where LABEL = 'FASE INTERNA - PREPARATÓRIA'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CADASTRO DE INTENÇÃO DE REGISTRO DE PREÇO - IRP', '/administrativo/licitacao/intencaoderegistrodepreco/lista.xhtml', (select id from MENU where LABEL = 'INTENÇÃO DE REGISTRO DE PREÇO - IRP'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'PARTICIPANTES DE INTENÇÃO DE REGISTRO DE PREÇO - IRP', '/administrativo/licitacao/intencao-registro-preco-participante/lista.xhtml', (select id from MENU where LABEL = 'INTENÇÃO DE REGISTRO DE PREÇO - IRP'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'PESQUISA DE PREÇO / COTAÇÕES - PP', null, (select id from MENU where LABEL = 'FASE INTERNA - PREPARATÓRIA'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'FORMULÁRIO DE COTAÇÃO', '/administrativo/licitacao/formulariocotacao/lista.xhtml', (select id from MENU where LABEL = 'PESQUISA DE PREÇO / COTAÇÕES - PP'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'COLETA DE COTAÇÃO', '/administrativo/licitacao/cotacao/lista.xhtml', (select id from MENU where LABEL = 'PESQUISA DE PREÇO / COTAÇÕES - PP'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'SOLICITAÇÃO DE COMPRA - LICITAÇÃO', '/administrativo/licitacao/solicitacaomaterial/lista.xhtml', (select id from MENU where LABEL = 'FASE INTERNA - PREPARATÓRIA'), 10, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'RESERVA DE DOTAÇÃO PARA SOLICITAÇÃO DE COMPRA', '/administrativo/licitacao/reservadedotacao/lista.xhtml', (select id from MENU where LABEL = 'FASE INTERNA - PREPARATÓRIA'), 15, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'AVALIAÇÃO DE SOLICITAÇÃO DE COMPRA', '/administrativo/licitacao/avaliacaosolicitacao/lista.xhtml', (select id from MENU where LABEL = 'FASE INTERNA - PREPARATÓRIA'), 20, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'PROCESSO DE COMPRA', '/administrativo/licitacao/processodecompra/lista.xhtml', (select id from MENU where LABEL = 'FASE INTERNA - PREPARATÓRIA'), 25, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'PARECER DA LICITAÇÃO', '/administrativo/licitacao/parecerlicitacao/lista.xhtml', (select id from MENU where LABEL = 'FASE INTERNA - PREPARATÓRIA'), 30, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'LICITAÇÃO - LCT', '/administrativo/licitacao/licitacao/lista.xhtml', (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES'), 15, 'fa-arrow-circle-right');

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'DISPENSA/INEXIGIBILIDADE', '/administrativo/licitacao/dispensalicitacao/lista.xhtml', (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES'), 20, 'fa-arrow-circle-right');

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CREDENCIAMENTO/CHAMAMENTO PÚBLICO', '/administrativo/licitacao/credenciamento/lista.xhtml', (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES'), 25, 'fa-arrow-circle-right');

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'FASE EXTERNA - DIVULGAÇÃO E CERTAME', null, (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES'), 30, 'fa-gavel');

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'PARTICIPANTES DE LICITAÇÃO', '/administrativo/licitacao/participantesdelicitacao/lista.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'GERAÇÃO/IMPORTAÇÃO DE ARQUIVO PROPOSTA LICITAÇÃO', '/administrativo/licitacao/arquivopropostafornecedor/edita.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'PROPOSTA DO FORNECEDOR', '/administrativo/licitacao/propostafornecedor/lista.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 10, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'PROPOSTA TÉCNICA DO FORNECEDOR', '/administrativo/licitacao/propostatecnica/lista.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 15, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'RECURSOS DA LICITAÇÃO', '/administrativo/licitacao/recurso/lista.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 20, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'MAPA COMPARATIVO', '/administrativo/licitacao/certame/lista.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 25, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'MAPA COMPARATIVO TÉCNICA E PREÇO', '/administrativo/licitacao/mapacomparativotecnicapreco/lista.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 30, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'ATA DA LICITAÇÃO', '/administrativo/licitacao/atalicitacao/lista.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 35, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'PREGÃO', null, (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 40, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'PREGÃO POR MATERIAL E SERVIÇO', '/administrativo/licitacao/pregao/por-item/lista.xhtml', (select id from MENU where LABEL = 'PREGÃO'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'PREGÃO POR LOTE DE MATERIAL E SERVIÇO', '/administrativo/licitacao/pregao/por-lote/lista.xhtml', (select id from MENU where LABEL = 'PREGÃO'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'LICITAÇÃO - PRÓXIMO VENCEDOR', '/administrativo/licitacao/proximo-vencedor-licitacao/lista.xhtml', (select id from MENU where LABEL = 'PREGÃO'), 10, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'HABILITAÇÃO DE FORNECEDORES', '/administrativo/licitacao/habilitacaofornecedor/edita.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 45, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'ADJUDICAÇÃO E HOMOLOGAÇÃO', '/administrativo/licitacao/adjudicacaohomologacao/lista.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 50, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'STATUS DA LICITAÇÃO', '/administrativo/licitacao/statuslicitacao/lista.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 55, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'LIBERAÇÃO DE RESERVA DA LICITAÇÃO', '/administrativo/licitacao/liberacaoreserva/lista.xhtml', (select id from MENU where LABEL = 'FASE EXTERNA - DIVULGAÇÃO E CERTAME'), 60, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'ATA DE REGISTRO DE PREÇO INTERNA - ARPI', null, (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES'), 35, 'fa-indent');

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CADASTRO DE ATA DE REGISTRO DE PREÇOS NTERNA - ARPI','/administrativo/licitacao/ataregistrodepreco/lista.xhtml', (select id from MENU where LABEL = 'ATA DE REGISTRO DE PREÇO INTERNA - ARPI'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'SOLICITAÇÃO DE ADESÃO À ATA DE REGISTRO DE PREÇO INTERNA - ARPI','/administrativo/licitacao/solicitacaomaterialexterno/lista-interna.xhtml', (select id from MENU where LABEL = 'ATA DE REGISTRO DE PREÇO INTERNA - ARPI'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'REPACTUAÇÃO DE PREÇO','/administrativo/licitacao/repactuacaodepreco/lista.xhtml', (select id from MENU where LABEL = 'ATA DE REGISTRO DE PREÇO INTERNA - ARPI'), 15, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'ATA DE REGISTRO DE PREÇO EXTERNA - ARPE', null, (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES'), 40, 'fa-outdent');

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'SOLICITAÇÃO DE ADESÃO À ATA DE REGISTRO DE PREÇO EXTERNA - ARPE', '/administrativo/licitacao/solicitacaomaterialexterno/lista-externa.xhtml', (select id from MENU where LABEL = 'ATA DE REGISTRO DE PREÇO EXTERNA - ARPE'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CADASTRO DE ATA DE REGISTRO DE PREÇO EXTERNO', '/administrativo/licitacao/registrosolicitacaomaterialexterno/lista.xhtml', (select id from MENU where LABEL = 'ATA DE REGISTRO DE PREÇO EXTERNA - ARPE'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'EXECUÇÃO SEM CONTRATO', null, (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES'), 45, 'fa-outdent');

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'EXECUÇÃO ATA REGISTRO DE PREÇOS', '/administrativo/licitacao/execucao-ata/lista.xhtml', (select id from MENU where LABEL = 'EXECUÇÃO SEM CONTRATO'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'ESTORNO EXECUÇÃO ATA REGISTRO DE PREÇOS', '/administrativo/licitacao/execucao-ata-estorno/lista.xhtml', (select id from MENU where LABEL = 'EXECUÇÃO SEM CONTRATO'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CONSULTAS E RELATÓRIOS - COMPRAS', null, (select id from MENU where LABEL = 'COMPRAS E LICITAÇÕES'), 50, 'fa-line-chart');

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'DASHBOARD DE CONTRATO E LICITAÇÃO', '/dashboard/dashboard-contrato-licitacao.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 1, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'RELATÓRIO DE SOLICITAÇÃO DE COMPRA', '/administrativo/relatorios/relatoriosolicitacao.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 5, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'RELATÓRIO DE HISTÓRICO DE FORNECEDOR', '/administrativo/relatorios/relatoriohistoricofornecedor.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 10, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'RELATÓRIO DE AVALIAÇÃO DE SOLICITAÇÃO DE COMPRA', '/administrativo/relatorios/relatorioavaliacaosolicitacaocompra.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 15, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'FORNECEDORES E PREÇOS POR MATERIAL/SERVIÇO', '/administrativo/relatorios/relatorio-fornecedores-e-precos-material-servico.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 20, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'RELATÓRIO DE LICITAÇÃO EM ANDAMENTO', '/administrativo/relatorios/relatoriolicitacaoemandamento.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 25, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'RELATÓRIO DE LICITAÇÃO FINALIZADA', '/administrativo/relatorios/relatoriolicitacaofinalizada.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 30, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'RELATÓRIO ATA REGISTRO DE PREÇO VIGENTE', '/administrativo/relatorios/relatorio-ata-registro-preco-vigente.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 35, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'DEMONSTRATIVO DE LICITAÇÕES, CONTRATOS E OBRAS', '/administrativo/relatorios/demonstrativocompras.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 40, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'RELATÓRIO DE ATA DE REGISTRO DE PREÇO', '/administrativo/relatorios/consulta-ata-registro-preco.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 45, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'CONSULTA ADESÃO A ATA DE REGISTRO DE PREÇO', '/administrativo/relatorios/consulta-adesao-ata.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 50, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'RELATÓRIO DE GERENCIAMENTO DE ATA DE REGISTRO DE PREÇO', '/administrativo/patrimonio/relatorios/gerenciamento-ata-registro-preco.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 55, null);

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval,'RELATÓRIO DE LICITAÇÕES ADJUDICADAS/HOMOLOGADAS', '/administrativo/relatorios/licitacao-adjudicada-homologada.xhtml', (select id from MENU where LABEL = 'CONSULTAS E RELATÓRIOS - COMPRAS'), 60, null);


