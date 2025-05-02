insert into eventofp(id, codigo, descricao, descricaoreduzida, tipoeventofp, ativo, automatico, arredondarvalor,
                     unidadereferencia, quantificacao, valormaximolancamento, ordemprocessamento, tipoexecucaoep,
                     dataregistro, naoenviarverbasicap, exibirnafichafinanceira, exibirnaficharpa, tipocalculo13,
                     regra, formula, formulavalorintegral, referencia, valorbasedecalculo)
values (hibernate_sequence.nextval, 940, 'Provisão de 13º Salário', 'Provisao 13 Salario', 'INFORMATIVO', 1, 1, 1,
        '/12', 0, 0, 940, 'FOLHA', sysdate, 1, 1, 1, 'NAO',
        'if (calculador.obterModalidadeContratoFP().getCodigo == 7) {
    return false;
}
return true;' ,
        'var base13int = calculador.calculaBase(''1200'');
var base13med = calculador.buscarTotalBases(''SALARIO_13'', ''VALOR'', new Array(''NORMAL'',''COMPLEMENTAR''), ''1205'', true, 12, true, ''COM_RETROACAO'') / 12;
var base13 = base13int + base13med;
return base13 / 12;',
        'return 0;',
        'return 1;',
        'var base13int = calculador.calculaBase(''1200'');
var base13med = calculador.buscarTotalBases(''SALARIO_13'', ''VALOR'', new Array(''NORMAL'',''COMPLEMENTAR''), ''1205'', true, 12, true, ''COM_RETROACAO'') / 12;
var base13 = base13int + base13med;
return base13;')
