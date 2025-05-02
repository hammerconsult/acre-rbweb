insert into eventofp(id, codigo, descricao, descricaoreduzida, tipoeventofp, regra, formula, formulavalorintegral,
                     referencia, valorbasedecalculo,
                     tipocalculo13, ativo, verbafixa, automatico, calculoretroativo, proporcionalizadiastrab,
                     remuneracaoprincipal,
                     arredondarvalor, controlecargolotacao, unidadereferencia, naopermitelancamento, apuracaoir,
                     tipolancamentofp, quantificacao,
                     valormaximolancamento, ordemprocessamento, tipoexecucaoep, dataregistro, classificacaoverba_id)
values (HIBERNATE_SEQUENCE.nextval, 555, 'REMUNERAÇÃO ACOMPANHAMENTO FAMILIAR', 'REM ACOMP FAMILIAR', 'VANTAGEM',
        'var diasAcompFam = calculador.diasDeAfastamentoAteCompAtual(''6'', true);

if (diasAcompFam > 0) {
    return true;
}

return false;',
        'var base = calculador.calculaBase(''1555'');
var diasMes = calculador.obterDiasDoMes();
var diasAcompFamMes = calculador.diasDeAfastamento(true, ''6'');
var diasAcompFam = calculador.diasDeAfastamentoAteCompAtual(''6'', false);
var percentual = calculador.obterReferenciaFaixaFP(''29'', diasAcompFam).percentual;


var remuneracao = base / diasMes * diasAcompFamMes * percentual / 100;

return remuneracao;',
        'var base = calculador.calculaBase(''1555'');
return base;',
        'var diasAcompFam = calculador.diasDeAfastamentoAteCompAtual(''6'', true);
return diasAcompFam;',
        'var base = calculador.calculaBase(''1555'');

return base;', 'NAO', 1, 0, 1, 1, 0, 0, 0, 0, 'Dias', 0, 0, 'VALOR', 0.0000, 999999.00, 555, 'FOLHA', sysdate, 10489910247)
