update recursosistema set caminho = '/tributario/relatorio/relatorioparcelamentodebitos.xhtml',
                          nome  = 'TRIBUTÁRIO > CONTA CORRENTE > PARCELAMENTO DE DÉBITOS  > RELATÓRIO DE PARCELAMENTO DE DÉBITOS'
where caminho = '/tributario/relatorio/relatoriosituacaoparcelamento.xhtml';

update menu set caminho = '/tributario/relatorio/relatorioparcelamentodebitos.xhtml',
                label = 'RELATÓRIO DE PARCELAMENTO DE DÉBITOS'
where caminho = '/tributario/relatorio/relatoriosituacaoparcelamento.xhtml';
