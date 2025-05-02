update menu set label = 'SOLICITAÇÃO DO PROCESSO DE ISENÇÃO DE IPTU' where CAMINHO = '/tributario/iptu/isencaoimpostos/processoisencao/lista.xhtml';
update menu set caminho = '/tributario/iptu/isencaoimpostos/solicitacaoprocessoisencao/lista.xhtml' where CAMINHO = '/tributario/iptu/isencaoimpostos/processoisencao/lista.xhtml';
update RECURSOSISTEMA set CAMINHO = '/tributario/iptu/isencaoimpostos/solicitacaoprocessoisencao/lista.xhtml' where CAMINHO = '/tributario/iptu/isencaoimpostos/processoisencao/lista.xhtml';
update RECURSOSISTEMA set CAMINHO = '/tributario/iptu/isencaoimpostos/solicitacaoprocessoisencao/visualizar.xhtml' where CAMINHO = '/tributario/iptu/isencaoimpostos/processoisencao/visualizar.xhtml';
update RECURSOSISTEMA set CAMINHO = '/tributario/iptu/isencaoimpostos/solicitacaoprocessoisencao/edita.xhtml' where CAMINHO = '/tributario/iptu/isencaoimpostos/processoisencao/edita.xhtml';
