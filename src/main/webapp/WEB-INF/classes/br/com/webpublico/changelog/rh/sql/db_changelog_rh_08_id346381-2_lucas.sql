update eventofp set formula = replace(formula, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' ),
                    regra = replace(regra, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' ),
                    referencia = replace(referencia, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' ),
                    valorbasedecalculo = replace(valorbasedecalculo, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' )
where codigo = '901';

update eventofp set formula = replace(formula, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' ),
                    regra = replace(regra, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' ),
                    referencia = replace(referencia, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' ),
                    valorbasedecalculo = replace(valorbasedecalculo, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' )
where codigo = '418';

update eventofp set formula = replace(formula, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' ),
                    regra = replace(regra, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' ),
                    formulavalorintegral = replace(formulavalorintegral, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' ),
                    referencia = replace(referencia, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' ),
                    valorbasedecalculo = replace(valorbasedecalculo, 'calculaBaseMultiplosVinculos(', 'calculaBaseMultiplosVinculosIR(' )
where codigo = '500';
