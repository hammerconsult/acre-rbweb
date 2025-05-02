insert into eventofp(id, codigo, descricao, descricaoreduzida, tipoeventofp, ativo, automatico, arredondarvalor,
                     unidadereferencia, quantificacao, valormaximolancamento, ordemprocessamento, tipoexecucaoep,
                     dataregistro, naoenviarverbasicap, exibirnafichafinanceira, exibirnaficharpa, tipocalculo13,
                     regra, formula, formulavalorintegral, referencia, valorbasedecalculo)
values (hibernate_sequence.nextval, 960, 'Provisão de Férias', 'Provisao Ferias', 'INFORMATIVO', 1, 1, 1,
        '/12', 0, 0, 960, 'FOLHA', sysdate, 1, 1, 1, 'NAO',
        'if (calculador.obterModalidadeContratoFP().getCodigo == 7 || calculador.identificaAposentado() || calculador.identificaPensionista()) {
    return false;
}
return true;',
        'var basefer = calculador.calculaBaseValorIntegral(''1004'');

return (basefer / 3) / 12;',
        'return 0;',
        'return 1;',
        'return calculador.calculaBaseValorIntegral(''1004'');')
