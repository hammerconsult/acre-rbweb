update eventofp
set VALORBASEDECALCULO = replace(VALORBASEDECALCULO, 'obterValorPensionista', 'obterValorBasePensionista')
where CODIGO in ('600', '355')
