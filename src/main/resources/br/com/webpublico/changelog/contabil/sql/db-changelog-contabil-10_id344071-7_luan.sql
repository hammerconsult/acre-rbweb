update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'o-que-e';
update PAGINAPREFEITURAPORTAL set ORDEM = 1 where CHAVE = 'acessibilidade';
update PAGINAPREFEITURAPORTAL set ORDEM = 2 where NOME = 'Mapa do Site' and CHAVE = 'mapa-do-site';
update PAGINAPREFEITURAPORTAL set ORDEM = 3 where CHAVE = 'glossario';
update PAGINAPREFEITURAPORTAL set ORDEM = 4 where NOME = 'Manual de Navegação' and CHAVE = 'mapa-do-site';
update PAGINAPREFEITURAPORTAL set ORDEM = 5 where CHAVE = 'http://eouv.riobranco.ac.gov.br/';

update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'arrecadacao';
update PAGINAPREFEITURAPORTAL set ORDEM = 1 where CHAVE = 'receita-por-conta-de-receita';
update PAGINAPREFEITURAPORTAL set ORDEM = 2 where CHAVE = 'receita-por-fonte-de-recurso';
update PAGINAPREFEITURAPORTAL set ORDEM = 3 where CHAVE = 'receita-por-unidade';

update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'despesa-por-acao';
update PAGINAPREFEITURAPORTAL set ORDEM = 1 where CHAVE = 'despesa-por-credor';
update PAGINAPREFEITURAPORTAL set ORDEM = 2 where CHAVE = 'despesa-por-empenho';
update PAGINAPREFEITURAPORTAL set ORDEM = 3 where CHAVE = 'despesa-por-fonte-de-recurso';
update PAGINAPREFEITURAPORTAL set ORDEM = 4 where CHAVE = 'despesa-por-funcao';
update PAGINAPREFEITURAPORTAL set ORDEM = 5 where CHAVE = 'despesa-por-natureza-da-despesa';
update PAGINAPREFEITURAPORTAL set ORDEM = 6 where CHAVE = 'despesa-por-programa';
update PAGINAPREFEITURAPORTAL set ORDEM = 7 where CHAVE = 'despesa-por-unidade';
update PAGINAPREFEITURAPORTAL set ORDEM = 8 where CHAVE = 'restos-a-pagar-por-acao';
update PAGINAPREFEITURAPORTAL set ORDEM = 9 where CHAVE = 'restos-a-pagar-por-credor';
update PAGINAPREFEITURAPORTAL set ORDEM = 10 where CHAVE = 'restos-a-pagar-por-empenho';
update PAGINAPREFEITURAPORTAL set ORDEM = 11 where CHAVE = 'restos-a-pagar-por-fonte-de-recurso';
update PAGINAPREFEITURAPORTAL set ORDEM = 12 where CHAVE = 'restos-a-pagar-por-funcao';
update PAGINAPREFEITURAPORTAL set ORDEM = 13 where CHAVE = 'restos-a-pagar-por-natureza-de-despesa';
update PAGINAPREFEITURAPORTAL set ORDEM = 14 where CHAVE = 'restos-a-pagar-por-programa';
update PAGINAPREFEITURAPORTAL set ORDEM = 15 where CHAVE = 'restos-a-pagar-por-unidade';
update PAGINAPREFEITURAPORTAL set ORDEM = 16 where CHAVE = 'diaria';
update PAGINAPREFEITURAPORTAL set ORDEM = 17 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/diarias/demonstrativo-das-concessoes-e-comprovacoes/';
update PAGINAPREFEITURAPORTAL set ORDEM = 18 where CHAVE = 'diaria-ato';
update PAGINAPREFEITURAPORTAL set ORDEM = 19, NOME = 'Legislação até 2016' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/diarias/legislacao-e-formu';

update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'convenio-despesa';
update PAGINAPREFEITURAPORTAL set ORDEM = 1 where CHAVE = 'convenio-receita';
update PAGINAPREFEITURAPORTAL set ORDEM = 2 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/convenios/legislacao-2/';

update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'liberacao-financeira';
update PAGINAPREFEITURAPORTAL set ORDEM = 1 where CHAVE = 'repasse-ato';

update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'servidor';
update PAGINAPREFEITURAPORTAL set ORDEM = 1 where CHAVE = 'servidor-ato';
update PAGINAPREFEITURAPORTAL set ORDEM = 2, NOME = 'Estagiários', CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/estagiarios/demonstrativos-dos-recrutamentos/' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/estagiarios/demonstrativos';
update PAGINAPREFEITURAPORTAL set ORDEM = 3, CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/atribuicoes-e-descricoes-dos-cargos/' where NOME = 'Atribuições e Descrições dos Cargos';
update PAGINAPREFEITURAPORTAL set ORDEM = 4 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/calendario-de-feriados-e-pontos-facultativos-decreto/';
update PAGINAPREFEITURAPORTAL set ORDEM = 5 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/calendario-de-pagamentos-e-remuneracoes-decretos/';
update PAGINAPREFEITURAPORTAL set ORDEM = 6 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/consignacao-em-folha-de-pagamento/';
update PAGINAPREFEITURAPORTAL set ORDEM = 7 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/grupo-ocupacional';
update PAGINAPREFEITURAPORTAL set ORDEM = 8, CHAVE = 'http://http://portalcgm.riobranco.ac.gov.br/portal/servidores/jornada-de-trabalho/' where NOME = 'Jornada de Trabalho';
update PAGINAPREFEITURAPORTAL set ORDEM = 9, MODULO_ID = (select id from MODULOPREFEITURAPORTAL where modulo = 'PESSOAL') where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/legislacao/';
update PAGINAPREFEITURAPORTAL set ORDEM = 10, NOME = 'Remuneração - Ativos até 2016' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/remuneracao-ativosdados-funcionais_ativos_marco/';
update PAGINAPREFEITURAPORTAL set ORDEM = 11 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/remuneracao-inativostemporarios/';
update PAGINAPREFEITURAPORTAL set ORDEM = 12 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/remuneracao-temporarios/';
update PAGINAPREFEITURAPORTAL set ORDEM = 13 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/responsabilidade-dos-prefeitos-e-vereadores-decreto/';
update PAGINAPREFEITURAPORTAL set ORDEM = 14 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/sancoes-aplicaveis-aos-agentes-publicos-lei/';
update PAGINAPREFEITURAPORTAL set ORDEM = 15 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/tabela-de-adicional-de-atencao-a-saude/';
update PAGINAPREFEITURAPORTAL set ORDEM = 16 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/tabela-de-adicionais-de-esf-pab-e-avs/';
update PAGINAPREFEITURAPORTAL set ORDEM = 17 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/tabela-de-dedicacao-integral-de-chefe-de-campo-tabela-de-indenizacao-de-campo-e-tabela-de-direcao-de-unidade-de-saude-da-familia/';
update PAGINAPREFEITURAPORTAL set ORDEM = 18 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/tabela-de-gratificacao-de-diretores-e-coordenadores-administrativos/';
update PAGINAPREFEITURAPORTAL set ORDEM = 19 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/tabela-de-valores-de-plantoes/';
update PAGINAPREFEITURAPORTAL set ORDEM = 20 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/tabela-de-vantagem-por-desempenho-profissional-dos-professores-vdp/';
update PAGINAPREFEITURAPORTAL set ORDEM = 21 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/tabela-de-vencimentos/';

update PAGINAPREFEITURAPORTAL set ORDEM = 0, CHAVE = 'ppa-programas' where CHAVE = 'PPA-programas';
update PAGINAPREFEITURAPORTAL set ORDEM = 1, CHAVE = 'ppa-acoes' where CHAVE = 'PPA-acoes';
update PAGINAPREFEITURAPORTAL set ORDEM = 2 where CHAVE = 'ldo';
update PAGINAPREFEITURAPORTAL set ORDEM = 3 where CHAVE = 'loa';
update PAGINAPREFEITURAPORTAL set ORDEM = 4 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/planejamento-municipal/mapa-de-processos/';
update PAGINAPREFEITURAPORTAL set ORDEM = 5 where CHAVE = 'plano-municipal';
update PAGINAPREFEITURAPORTAL set ORDEM = 6, NOME = 'PPA até 2017' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/planejamento-municipal/ppa';
update PAGINAPREFEITURAPORTAL set ORDEM = 7, NOME = 'LDO até 2017' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/planejamento-municipal/ldo2010/';
update PAGINAPREFEITURAPORTAL set ORDEM = 8, NOME = 'LOA até 2017' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/planejamento-municipal/loa2010/';

update PAGINAPREFEITURAPORTAL set ORDEM = 0, MODULO_ID = (select id from MODULOPREFEITURAPORTAL where modulo = 'CONTRATO'), NOME = 'Licitações' where CHAVE = 'licitacao-outras';
update PAGINAPREFEITURAPORTAL set ORDEM = 1, NOME = 'Adesão/Carona' where CHAVE = 'licitacao-adesao-carona';
update PAGINAPREFEITURAPORTAL set ORDEM = 2, MODULO_ID = (select id from MODULOPREFEITURAPORTAL where modulo = 'CONTRATO') where CHAVE = 'licitacao-dispensa';
update PAGINAPREFEITURAPORTAL set ORDEM = 3, NOME = 'Contratos' where CHAVE = 'contrato';
update PAGINAPREFEITURAPORTAL set ORDEM = 4, NOME = 'Licitação até 2016' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/licitacoes-e-contratos/demonstrativo-de-licitacoes-e-contratos/';

update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'obra';

update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'anexo-contabilidade';
update PAGINAPREFEITURAPORTAL set ORDEM = 1, NOME = 'Até 2016 - Aviso aos Contribuintes' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/prestacao-de-contas/aviso-aos-contribuintes/';
update PAGINAPREFEITURAPORTAL set ORDEM = 2, NOME = 'Até 2016 - Decretos de Acesso a Movimentação Bancária' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/prestacao-de-contas/decretos-de-acesso-a-movimentacao-bancaria/';
update PAGINAPREFEITURAPORTAL set ORDEM = 3, NOME = 'Até 2016 - Julgamento das Prestações de Contas no TCE/AC' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/prestacao-de-contas/julgamento-das-prestacoes-de-contas-no-tceac/';

update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'responsabilidade-fiscal';
update PAGINAPREFEITURAPORTAL set ORDEM = 1, NOME = 'Até 2016 - Programação Financeira e Cronograma Mensal de Desembolso' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/responsabilidade-fiscal/programacao-financeira-e-cronograma-mensal-de-desembolso/';
update PAGINAPREFEITURAPORTAL set ORDEM = 2, NOME = 'Até 2016 - Relatório Resumido da Execução Orçamentária – RREO' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/responsabilidade-fiscal/relatorio-resumido-da-execucao-orcamentaria/';
update PAGINAPREFEITURAPORTAL set ORDEM = 3, NOME = 'Até 2016 - Relatório de Gestão Fiscal – RGF' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/responsabilidade-fiscal/relatorio-de-gestao-fiscal/';

update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/lai/legislacao/';
update PAGINAPREFEITURAPORTAL set ORDEM = 1 where CHAVE = 'ato-legal';
update PAGINAPREFEITURAPORTAL set ORDEM = 2, NOME = 'Legislação até 2016' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/legislacao/';

update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/precatorios/deposito/';
update PAGINAPREFEITURAPORTAL set ORDEM = 1 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/precatorios/lista/';
update PAGINAPREFEITURAPORTAL set ORDEM = 2 where CHAVE = 'precatorio-ato';

update PAGINAPREFEITURAPORTAL set ORDEM = 0 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/contabilidade-aplicada-setor-publico/';
update PAGINAPREFEITURAPORTAL set ORDEM = 1 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/contratacao-pessoal-tempo-determinado/';
update PAGINAPREFEITURAPORTAL set ORDEM = 2, NOME = 'Decreto de Medidas Administrativas Temporárias para Contenção e Otimização de Despesas' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/decreto-de-medidas-administrativas-temporarias-para-contencao-de-despesas/';
update PAGINAPREFEITURAPORTAL set ORDEM = 3, NOME = 'Decreto de Encerramento do Exercício Financeiro' where ID = 10953478960;
update PAGINAPREFEITURAPORTAL set ORDEM = 3, NOME = 'Decreto de Encerramento do Exercício Financeiro' where ID = 10959159814;
update PAGINAPREFEITURAPORTAL set ORDEM = 4, NOME = 'Instrução Normativa Conjunta Nº 002 de 17 de agosto de 2009' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/instrucao-normativa-conjunta-no-002-de-17-de-agosto-de-2009/';
update PAGINAPREFEITURAPORTAL set ORDEM = 5 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/fiscalizacao-contratos-administrativos//';
update PAGINAPREFEITURAPORTAL set ORDEM = 6, NOME = 'Lei nº 12.846, de 1º de agosto de 2013 - Lei Anticorrupção' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/lei-no-12-846-de-1o-de-agosto-de-2013-lei-anticorrupcao/';
update PAGINAPREFEITURAPORTAL set ORDEM = 7 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/modelos-de-extrato-publicacao-doe/';
update PAGINAPREFEITURAPORTAL set ORDEM = 8 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/prestacao-de-contas/normas-organizacao/';
update PAGINAPREFEITURAPORTAL set ORDEM = 9 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/normas-patrimonial/';
update PAGINAPREFEITURAPORTAL set ORDEM = 10, NOME = 'Organização do Sistema de Controle Interno Municipal' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/organizacao-sistema-controle-interno/';
update PAGINAPREFEITURAPORTAL set ORDEM = 11 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/orientacoes-tecnicas/';
update PAGINAPREFEITURAPORTAL set ORDEM = 12, NOME = 'Instruções Normativas CGM' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/instrucao-normativa-cgm-no-0012014/';
update PAGINAPREFEITURAPORTAL set ORDEM = 13 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/resolucoes-do-tceac/';
update PAGINAPREFEITURAPORTAL set ORDEM = 14, NOME = 'Terceirização de Serviços na Administração Municipal' where ID = 10953478980;
update PAGINAPREFEITURAPORTAL set ORDEM = 14, NOME = 'Terceirização de Serviços na Administração Municipal' where ID = 10959159820;
update PAGINAPREFEITURAPORTAL set ORDEM = 15 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/termo-de-cooperacao-tecnica-entre-o-tce-e-o-municipio-de-rio-branco/';
update PAGINAPREFEITURAPORTAL set ORDEM = 16 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/utilizacao-de-veiculos-oficiais-pela-administracao-publica/';
update PAGINAPREFEITURAPORTAL set ORDEM = 17 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/comissao-permanente-de-controle-do-mobiliario-municipal-cpcm/';
update PAGINAPREFEITURAPORTAL set ORDEM = 18, NOME = 'Decreto Nº 925-2015 - Aplicação do Portal LICON pelo Poder Executivo Municipal' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/decrto-no-925-2015-aplicacao-do-portal-licon-pelo-poder-executivo-municipal/';
update PAGINAPREFEITURAPORTAL set ORDEM = 19 where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/ministerio-publico-focco-recomendacao-no-0012016/';
update PAGINAPREFEITURAPORTAL set ORDEM = 20, NOME = 'DECRETO Nº 060-2016 - Decreto sobre o último ano de mandato' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/decreto-no-060-2016-decreto-sobre-o-ultimo-ano-de-mandato/';
update PAGINAPREFEITURAPORTAL set ORDEM = 21, NOME = 'DECRETO Nº 407-2017 - Delegação de Competência' where CHAVE = 'http://portalcgm.riobranco.ac.gov.br/portal/controle-interno-e-contabilidade/decreto-no-407-2017-regulamentacao-da-delegacao-de-competencia/';
