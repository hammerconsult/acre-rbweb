update eventofp set regra = 'return calculador.valorUltimosMeses(''216'', 1) > 0;',
                    FORMULA = 'return calculador.valorUltimosMeses(''216'', 1);',
                    FORMULAVALORINTEGRAL = 'return calculador.valorUltimosMeses(''216'', 1);',
                    VALORBASEDECALCULO = 'return calculador.valorUltimosMeses(''216'', 1);',
                    AUTOMATICO = 1
where codigo ='623';
update eventofp set CALCULORETROATIVO = 0 where codigo ='216';
